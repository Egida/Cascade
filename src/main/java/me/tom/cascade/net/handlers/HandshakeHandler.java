package me.tom.cascade.net.handlers;

import static me.tom.cascade.protocol.ConnectionState.LOGIN;
import static me.tom.cascade.protocol.ConnectionState.TRANSFER;

import java.lang.reflect.InvocationTargetException;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.NoArgsConstructor;
import me.tom.cascade.net.ProtocolVersion;
import me.tom.cascade.protocol.ConnectionState;
import me.tom.cascade.protocol.ProtocolAttributes;
import me.tom.cascade.protocol.packet.Packet;
import me.tom.cascade.protocol.packet.packets.serverbound.HandshakePacket;

@NoArgsConstructor
public class HandshakeHandler extends SimpleChannelInboundHandler<Packet> {
	@Override
    protected void channelRead0(ChannelHandlerContext ctx, Packet packet) throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException {
		if(packet instanceof HandshakePacket) {
			HandshakePacket handshake = (HandshakePacket)packet;

			ConnectionState nextState = ConnectionState.values()[handshake.getNextState()];

			boolean serverTransfer = nextState == TRANSFER;
			
			if(serverTransfer)
				nextState = LOGIN;
			
			ctx.channel().attr(ProtocolAttributes.TRANSFER).set(serverTransfer);
	        ctx.channel().attr(ProtocolAttributes.STATE).set(nextState);
	        ctx.channel().attr(ProtocolAttributes.PROTOCOL_VERSION).set(ProtocolVersion.getFromVersionNumber(handshake.protocolVersion));
	        
	        ctx.pipeline().replace(this, "packet-handler", nextState.getHandler().getConstructor().newInstance());
		} else {
			ctx.fireChannelRead(packet);
		}
    }
}
