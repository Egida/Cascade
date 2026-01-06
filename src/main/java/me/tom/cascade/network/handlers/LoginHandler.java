package me.tom.cascade.network.handlers;

import static me.tom.cascade.network.protocol.ProtocolVersion.MINECRAFT_1_21_2;
import static me.tom.cascade.network.protocol.ProtocolVersion.UNKNOWN;

import java.lang.reflect.InvocationTargetException;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.security.PrivateKey;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Date;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

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
import me.tom.cascade.network.BanManager;
import me.tom.cascade.network.StrikeManager;
import me.tom.cascade.network.handlers.forward.ClientToServerHandler;
import me.tom.cascade.network.handlers.forward.ServerToClientHandler;
import me.tom.cascade.network.pipeline.BackendInitializer;
import me.tom.cascade.network.protocol.ProtocolAttributes;
import me.tom.cascade.network.protocol.ProtocolState;
import me.tom.cascade.network.protocol.ProtocolVersion;
import me.tom.cascade.network.protocol.codec.PacketDecoder;
import me.tom.cascade.network.protocol.codec.PacketEncoder;
import me.tom.cascade.network.protocol.codec.PacketFramer;
import me.tom.cascade.network.protocol.packet.Packet;
import me.tom.cascade.network.protocol.packet.packets.clientbound.CookieRequestPacket;
import me.tom.cascade.network.protocol.packet.packets.clientbound.DisconnectPacket;
import me.tom.cascade.network.protocol.packet.packets.clientbound.EncryptionResponsePacket;
import me.tom.cascade.network.protocol.packet.packets.clientbound.LoginSuccessPacket;
import me.tom.cascade.network.protocol.packet.packets.clientbound.OldLoginSuccessPacket;
import me.tom.cascade.network.protocol.packet.packets.clientbound.StoreCookiePacket;
import me.tom.cascade.network.protocol.packet.packets.clientbound.TransferPacket;
import me.tom.cascade.network.protocol.packet.packets.serverbound.CookieResponsePacket;
import me.tom.cascade.network.protocol.packet.packets.serverbound.EncryptionRequestPacket;
import me.tom.cascade.network.protocol.packet.packets.serverbound.HandshakePacket;
import me.tom.cascade.network.protocol.packet.packets.serverbound.LoginAcknowledgedPacket;
import me.tom.cascade.network.protocol.packet.packets.serverbound.LoginStartPacket;

@RequiredArgsConstructor
public class LoginHandler extends SimpleChannelInboundHandler<Packet> {
	private LoginState loginState = LoginState.WAITING_FOR_LOGIN_START;
	private boolean cookieHandled;
	
    private final SecureRandom random = new SecureRandom();
    private final byte[] verifyToken = new byte[4];
    private String username;
    private UUID uuid;

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Packet packet) throws Exception {
        if (packet instanceof LoginStartPacket) {
        	LoginStartPacket loginStart = (LoginStartPacket)packet;
            onLoginStart(ctx, loginStart);
        } else if (packet instanceof CookieResponsePacket) {
        	CookieResponsePacket cookieResponse = (CookieResponsePacket)packet;
            onCookieResponse(ctx, cookieResponse);
        } else if (packet instanceof EncryptionResponsePacket) {
        	EncryptionResponsePacket encryptionResponse = (EncryptionResponsePacket)packet;
            onEncryptionResponse(ctx, encryptionResponse);
        } else if (packet instanceof LoginAcknowledgedPacket) {
            onLoginAcknowledged(ctx);
        }
    }

    private void onLoginStart(ChannelHandlerContext ctx, LoginStartPacket packet) {
    	if(loginState != LoginState.WAITING_FOR_LOGIN_START) {
    		ctx.close();
    		return;
    	}
    	
    	if(!CascadeBootstrap.SERVER_CACHE.isOnline()) {
    		ctx.writeAndFlush(new DisconnectPacket("{\"text\":\"§cProxy configuration error: backend is unreachable.\"}"));
    		ctx.close();
    		return;
    	}
    	
        this.username = packet.getName();
        this.uuid = packet.getUuid();
        ctx.writeAndFlush(new CookieRequestPacket("cascade:token"));
        
        ctx.executor().schedule(() -> {
            if (loginState != LoginState.COMPLETED && !ctx.channel().attr(ProtocolAttributes.TRANSFER).get().equals(Boolean.TRUE)) {
                int strikes = StrikeManager.addStrike(ctx);

                if (strikes >= 3) {
                    BanManager.ban(ctx, 10000L);
                }

                ctx.close();
            }
        }, 10, TimeUnit.SECONDS);
        
        loginState = LoginState.WAITING_FOR_COOKIE_RESPONSE;
    }

    private void onEncryptionResponse(ChannelHandlerContext ctx, EncryptionResponsePacket packet) {
    	if(loginState != LoginState.WAITING_FOR_ENCRYPTION_RESPONSE) {
    		ctx.close();
    		return;
    	}
    	
        PrivateKey privateKey = Crypto.KEY_PAIR.getPrivate();

        byte[] sharedSecret = Crypto.rsaDecrypt(packet.getSharedSecret(), privateKey);
        byte[] token = Crypto.rsaDecrypt(packet.getVerifyToken(), privateKey);

        if (!validateVerifyToken(token)) {
            ctx.close();
            return;
        }

        GameProfile profile = getGameProfile(ctx, CascadeBootstrap.CONFIG.isAuthVerification(), sharedSecret);

        if(profile == null) {
	        BanManager.ban(ctx, 1000 * 60 * 60 * 24);
	    	ctx.close();
        }
        
        enableEncryption(ctx.pipeline(), sharedSecret);
        sendLoginSuccess(ctx, profile);
        
        loginState = LoginState.WAITING_FOR_LOGIN_ACK;
    }

    private void sendLoginSuccess(ChannelHandlerContext ctx, GameProfile profile) {
        ProtocolVersion protocolVersion = ctx.channel().attr(ProtocolAttributes.PROTOCOL_VERSION).get();
        
        if (protocolVersion != null && protocolVersion.isBefore(MINECRAFT_1_21_2)) {
            ctx.writeAndFlush(new OldLoginSuccessPacket(profile, false));
        } else {
            ctx.writeAndFlush(new LoginSuccessPacket(profile));
        }
    }

    private void onCookieResponse(ChannelHandlerContext ctx, CookieResponsePacket packet) {
    	if(loginState != LoginState.WAITING_FOR_COOKIE_RESPONSE) {
    		ctx.close();
    		return;
    	}
    	
    	if(cookieHandled) {
    		ctx.close();
    		return;
    	}
    	
    	cookieHandled = true;
    	
        if (!"cascade:token".equals(packet.getKey())) {
            return;
        }
        
        Channel client = ctx.channel();
        ProtocolVersion protocolVersion = client.attr(ProtocolAttributes.PROTOCOL_VERSION).get();

        if (Boolean.FALSE.equals(client.attr(ProtocolAttributes.TRANSFER).get())) {
            random.nextBytes(verifyToken);

            EncryptionRequestPacket encryptionRequest = new EncryptionRequestPacket(
                    "",
                    Crypto.KEY_PAIR.getPublic().getEncoded(),
                    verifyToken,
                    CascadeBootstrap.CONFIG.isAuthVerification()
            );

            ctx.writeAndFlush(encryptionRequest);
            
            loginState = LoginState.WAITING_FOR_ENCRYPTION_RESPONSE;
            return;
        }

        boolean validToken = false;
        if (packet.getPayload() != null) {
            String jwtToken = new String(packet.getPayload(), StandardCharsets.UTF_8);
            validToken = isValidJwtToken(jwtToken, ctx);
        }

        if (!validToken) {
            return;
        }

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

            doClientLogin(ctx, backend);
            clearPipelines(client, backend);
        });
    }

    private void clearPipelines(Channel client, Channel backend) {
        client.pipeline().remove(PacketFramer.class);
        client.pipeline().remove(PacketDecoder.class);
        client.pipeline().remove(PacketEncoder.class);
        client.pipeline().remove(ConnectionHandler.class);

        backend.pipeline().remove(PacketFramer.class);
        backend.pipeline().remove(PacketDecoder.class);
        backend.pipeline().remove(PacketEncoder.class);
    }

    private void doClientLogin(ChannelHandlerContext ctx, Channel backend) {
        ProtocolVersion protocolVersion = ctx.channel().attr(ProtocolAttributes.PROTOCOL_VERSION).get();

        HandshakePacket handshake = new HandshakePacket(
                protocolVersion != null ? protocolVersion.getVersionNumber() : UNKNOWN.getVersionNumber(),
                CascadeBootstrap.CONFIG.getTargetHost(),
                CascadeBootstrap.CONFIG.getTargetPort(),
                ProtocolState.LOGIN.ordinal()
        );

        LoginStartPacket loginStart = new LoginStartPacket(username, uuid);

        backend.attr(ProtocolAttributes.STATE).set(ProtocolState.HANDSHAKE);
        backend.writeAndFlush(handshake).addListener(f -> {
            backend.attr(ProtocolAttributes.STATE).set(ProtocolState.LOGIN);
            backend.writeAndFlush(loginStart);
        });
    }

    private void onLoginAcknowledged(ChannelHandlerContext ctx) throws InstantiationException, IllegalAccessException,
            IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException {

    	if(loginState != LoginState.WAITING_FOR_LOGIN_ACK) {
    		ctx.close();
    		return;
    	}
    	
    	loginState = LoginState.COMPLETED;
    	
        String ip = ((InetSocketAddress) ctx.channel().remoteAddress()).getHostString();

        String jwt = Jwts.builder()
                .setSubject(username)
                .claim("ip", ip)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 5_000))
                .signWith(CascadeBootstrap.JWT_KEY, SignatureAlgorithm.HS256)
                .compact();

        ctx.channel().attr(ProtocolAttributes.STATE).set(ProtocolState.CONFIGURATION);
        ctx.pipeline().replace(this, "packet-handler",
                ProtocolState.CONFIGURATION.getHandler().getConstructor().newInstance());

        StoreCookiePacket storeCookie = new StoreCookiePacket(
                "cascade:token",
                jwt.getBytes(StandardCharsets.UTF_8)
        );

        TransferPacket transfer = new TransferPacket(
                ((InetSocketAddress) ctx.channel().remoteAddress()).getHostString(),
                CascadeBootstrap.CONFIG.getProxyPort()
        );

        ctx.writeAndFlush(storeCookie);
        ctx.writeAndFlush(transfer);
    }

    private boolean validateVerifyToken(byte[] token) {
        return Arrays.equals(token, verifyToken);
    }

    private void enableEncryption(ChannelPipeline pipeline, byte[] sharedSecret) {
        SecretKey aesKey = new SecretKeySpec(sharedSecret, "AES");
        pipeline.addFirst("decrypt", new AesDecryptHandler(aesKey));
        pipeline.addBefore("packet-encoder", "encrypt", new AesEncryptHandler(aesKey));
    }
    

	private boolean isValidJwtToken(String jwt, ChannelHandlerContext ctx) {
        try {
            Key key = CascadeBootstrap.JWT_KEY;

            Claims claims = Jwts.parserBuilder()
            		.setSigningKey(key)
            		.build()
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
	        return profile;
    	} else {
    		return new GameProfile(UUID.randomUUID(), "", null);
    	}
    }

    private GameProfile authenticate(ChannelHandlerContext ctx, byte[] sharedSecret) {
        byte[] publicKey = Crypto.KEY_PAIR.getPublic().getEncoded();
        String serverHash = Crypto.minecraftSha1Hash("", sharedSecret, publicKey);
        String ip = ((InetSocketAddress) ctx.channel().remoteAddress())
                .getAddress()
                .getHostAddress();

        return MojangSessionService.hasJoined(username, serverHash, ip);
    }
}