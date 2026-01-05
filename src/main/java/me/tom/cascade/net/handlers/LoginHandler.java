package me.tom.cascade.net.handlers;

import static me.tom.cascade.net.ProtocolVersion.*;

import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.security.PrivateKey;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Date;
import java.util.UUID;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.socket.nio.NioSocketChannel;
import lombok.RequiredArgsConstructor;
import me.tom.cascade.CascadeBootstrap;
import me.tom.cascade.auth.GameProfile;
import me.tom.cascade.auth.MojangSessionService;
import me.tom.cascade.crypto.AesDecryptHandler;
import me.tom.cascade.crypto.AesEncryptHandler;
import me.tom.cascade.crypto.Crypto;
import me.tom.cascade.net.ProtocolVersion;
import me.tom.cascade.net.handlers.forward.ClientToServerHandler;
import me.tom.cascade.net.handlers.forward.ServerToClientHandler;
import me.tom.cascade.net.pipeline.BackendInitializer;
import me.tom.cascade.protocol.ConnectionState;
import me.tom.cascade.protocol.ProtocolAttributes;
import me.tom.cascade.protocol.codec.PacketDecoder;
import me.tom.cascade.protocol.codec.PacketEncoder;
import me.tom.cascade.protocol.codec.PacketFramer;
import me.tom.cascade.protocol.packet.Packet;
import me.tom.cascade.protocol.packet.packets.clientbound.CookieRequestPacket;
import me.tom.cascade.protocol.packet.packets.clientbound.EncryptionResponsePacket;
import me.tom.cascade.protocol.packet.packets.clientbound.LoginSuccessPacket;
import me.tom.cascade.protocol.packet.packets.clientbound.OldLoginSuccessPacket;
import me.tom.cascade.protocol.packet.packets.clientbound.StoreCookiePacket;
import me.tom.cascade.protocol.packet.packets.clientbound.TransferPacket;
import me.tom.cascade.protocol.packet.packets.serverbound.CookieResponsePacket;
import me.tom.cascade.protocol.packet.packets.serverbound.EncryptionRequestPacket;
import me.tom.cascade.protocol.packet.packets.serverbound.HandshakePacket;
import me.tom.cascade.protocol.packet.packets.serverbound.LoginAcknowledgedPacket;
import me.tom.cascade.protocol.packet.packets.serverbound.LoginStartPacket;

@RequiredArgsConstructor
public class LoginHandler extends SimpleChannelInboundHandler<Packet> {
	private SecureRandom random = new SecureRandom();
	private byte[] verifyToken = new byte[4];
	private String username;
	private UUID uuid;
	
	@Override
	protected void channelRead0(ChannelHandlerContext ctx, Packet packet) throws Exception {
		ProtocolVersion protocolVersion = ctx.channel().attr(ProtocolAttributes.PROTOCOL_VERSION).get();
		
		if(packet instanceof LoginStartPacket) {
			LoginStartPacket loginStart = (LoginStartPacket)packet;
			
			this.username = loginStart.getName();
			this.uuid = loginStart.getUuid();
			
			CookieRequestPacket cookieRequest = new CookieRequestPacket("cascade:token");
			ctx.writeAndFlush(cookieRequest);
		} else if(packet instanceof CookieResponsePacket) {
			CookieResponsePacket cookieResponse = (CookieResponsePacket)packet;

			if(cookieResponse.getKey().contentEquals("cascade:token")) {
		        if(!ctx.channel().attr(ProtocolAttributes.TRANSFER).get()) {
		            random.nextBytes(verifyToken);

		            EncryptionRequestPacket encryptionRequest = new EncryptionRequestPacket(
		                    "",
		                    Crypto.KEY_PAIR.getPublic().getEncoded(),
		                    verifyToken,
		                    CascadeBootstrap.CONFIG.isAuthVerification()
		            );

		            ctx.writeAndFlush(encryptionRequest);
		            return;
		        }
		        
		        boolean validToken = false;
		        if(cookieResponse.getPayload() != null) {
			        String jwtToken = new String(cookieResponse.getPayload(), StandardCharsets.UTF_8);
			        validToken = isValidJwtToken(jwtToken, ctx);
		        }
		        
		        if (validToken) {
		            Channel client = ctx.channel();

		            HandshakePacket handshake = new HandshakePacket(
		                    protocolVersion.getVersionNumber(),
		                    CascadeBootstrap.CONFIG.getTargetHost(),
		                    CascadeBootstrap.CONFIG.getTargetPort(),
		                    ConnectionState.LOGIN.ordinal()
		            );
		            LoginStartPacket loginStart = new LoginStartPacket(username, uuid);

		            Bootstrap backendBootstrap = new Bootstrap()
		                    .group(client.eventLoop())
		                    .channel(NioSocketChannel.class)
		                    .handler(new BackendInitializer());

		            backendBootstrap.connect(
		                    CascadeBootstrap.CONFIG.getTargetHost(),
		                    CascadeBootstrap.CONFIG.getTargetPort()
		            ).addListener(future -> {
		                if (!future.isSuccess()) {
		                    client.close();
		                    return;
		                }

		                Channel backend = ((ChannelFuture) future).channel();
		                
		                backend.attr(ProtocolAttributes.PROTOCOL_VERSION).set(protocolVersion);

		                client.pipeline().addLast("client-to-server", new ClientToServerHandler(backend));
		                backend.pipeline().addLast("server-to-client", new ServerToClientHandler(client));

		                backend.attr(ProtocolAttributes.STATE).set(ConnectionState.HANDSHAKE);
		                backend.writeAndFlush(handshake).addListener(f -> {
		                    backend.attr(ProtocolAttributes.STATE).set(ConnectionState.LOGIN);
		                    backend.writeAndFlush(loginStart);
		                });
		                
		                backend.pipeline().remove(PacketFramer.class);
		                backend.pipeline().remove(PacketDecoder.class);
		                backend.pipeline().remove(PacketEncoder.class);

		                client.pipeline().remove(PacketFramer.class);
		                client.pipeline().remove(PacketDecoder.class);
		                client.pipeline().remove(PacketEncoder.class);
		                client.pipeline().remove(ConnectionHandler.class);
		            });
		        } else {
		            ctx.close();
		            return;
		        }
			}
		} else if(packet instanceof EncryptionResponsePacket) {
			EncryptionResponsePacket encryptionResponse = (EncryptionResponsePacket)packet;
			PrivateKey privateKey = Crypto.KEY_PAIR.getPrivate();

	        byte[] sharedSecret = Crypto.rsaDecrypt(encryptionResponse.getSharedSecret(), privateKey);
	        byte[] verifyToken = Crypto.rsaDecrypt(encryptionResponse.getVerifyToken(), privateKey);
	        boolean validToken = validateVerifyToken(ctx, verifyToken);
	        boolean onlineMode = CascadeBootstrap.CONFIG.isAuthVerification();

	        if (!validToken) {
	            ctx.close();
	            return;
	        }
	        
	        GameProfile profile = getGameProfile(ctx, onlineMode, sharedSecret);
	        enableEncryption(ctx.pipeline(), sharedSecret);
	        if(protocolVersion.isBefore(MINECRAFT_1_21_1)) {
		        ctx.writeAndFlush(new OldLoginSuccessPacket(profile, false));
	        } else {
	        	ctx.writeAndFlush(new LoginSuccessPacket(profile));
	        }
		} else if(packet instanceof LoginAcknowledgedPacket) {
			String ip = ((InetSocketAddress) ctx.channel().remoteAddress()).getHostString();
			String jwt = Jwts.builder()
			        .setSubject(username)
			        .claim("ip", ip)
			        .setIssuedAt(new Date())
			        .setExpiration(new Date(System.currentTimeMillis() + 5_000))
			        .signWith(CascadeBootstrap.JWT_KEY, SignatureAlgorithm.HS256)
			        .compact();
			ctx.channel().attr(ProtocolAttributes.STATE).set(ConnectionState.CONFIGURATION);
	        ctx.pipeline().replace(this, "packet-handler", ConnectionState.CONFIGURATION.getHandler().newInstance());

			StoreCookiePacket storeCookie = new StoreCookiePacket(
						"cascade:token", 
						jwt.getBytes()
					);
			
			TransferPacket transfer = new TransferPacket(
						((InetSocketAddress)ctx.channel().remoteAddress()).getHostString(), 
						CascadeBootstrap.CONFIG.getProxyPort()
					);
			
			ctx.writeAndFlush(storeCookie);
			ctx.writeAndFlush(transfer);
		}
	}
	
	private boolean isValidJwtToken(String jwt, ChannelHandlerContext ctx) {
        try {
            Key key = CascadeBootstrap.JWT_KEY;

            Claims claims = Jwts.parser()
                    .setSigningKey(key)
                    .parseClaimsJws(jwt)
                    .getBody();

            if(!claims.getSubject().contentEquals(username))
            	return false;
            
            if(!claims.get("ip", String.class)
            		.contentEquals(((InetSocketAddress) ctx.channel().remoteAddress()).getHostString()))
            		return false;
            
            return true;
        } catch (Exception ignored) {
            return false;
        }
    }
	

    
    private GameProfile getGameProfile(ChannelHandlerContext ctx, boolean onlineMode, byte[] sharedSecret) {
    	if(onlineMode) {
	    	GameProfile profile = authenticate(ctx, sharedSecret);
	        if (profile == null) {
	            ctx.close();
	        }
	        return profile;
    	} else {
    		return new GameProfile(UUID.randomUUID(), "", null);
    	}
    }

    private boolean validateVerifyToken(ChannelHandlerContext ctx, byte[] token) {
        return Arrays.equals(token, verifyToken);
    }

    private GameProfile authenticate(ChannelHandlerContext ctx, byte[] sharedSecret) {
        byte[] publicKey = Crypto.KEY_PAIR.getPublic().getEncoded();
        String serverHash = Crypto.minecraftSha1Hash("", sharedSecret, publicKey);
        String ip = ((InetSocketAddress) ctx.channel().remoteAddress())
                .getAddress()
                .getHostAddress();

        return MojangSessionService.hasJoined(username, serverHash, ip);
    }

    private void enableEncryption(ChannelPipeline pipeline, byte[] sharedSecret) {
        SecretKey aesKey = new SecretKeySpec(sharedSecret, "AES");
        pipeline.addFirst("decrypt", new AesDecryptHandler(aesKey));
        pipeline.addBefore("packet-encoder", "encrypt", new AesEncryptHandler(aesKey));
    }
}