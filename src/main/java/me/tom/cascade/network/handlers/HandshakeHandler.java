package me.tom.cascade.network.handlers;

import static me.tom.cascade.network.protocol.ProtocolState.LOGIN;
import static me.tom.cascade.network.protocol.ProtocolState.TRANSFER;

import java.lang.reflect.InvocationTargetException;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.NoArgsConstructor;
import me.tom.cascade.network.protocol.ProtocolAttributes;
import me.tom.cascade.network.protocol.ProtocolState;
import me.tom.cascade.network.protocol.ProtocolVersion;
import me.tom.cascade.network.protocol.packet.Packet;
import me.tom.cascade.network.protocol.packet.packets.serverbound.HandshakePacket;

@NoArgsConstructor
public class HandshakeHandler extends SimpleChannelInboundHandler<Packet> {
	@Override
    protected void channelRead0(ChannelHandlerContext ctx, Packet packet) throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException {
		if(packet instanceof HandshakePacket) {
			HandshakePacket handshake = (HandshakePacket)packet;
			onHandshake(ctx, handshake);
		} else {
			ctx.fireChannelRead(packet);
		}
    }
	
	private void onHandshake(ChannelHandlerContext ctx, HandshakePacket packet) throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException {
		ProtocolState nextState = ProtocolState.values()[packet.getNextState()];

		boolean serverTransfer = nextState == TRANSFER;
		
		if(serverTransfer)
			nextState = LOGIN;
		
		ctx.channel().attr(ProtocolAttributes.TRANSFER).set(serverTransfer);
        ctx.channel().attr(ProtocolAttributes.STATE).set(nextState);
        ctx.channel().attr(ProtocolAttributes.PROTOCOL_VERSION).set(ProtocolVersion.getFromVersionNumber(packet.getProtocolVersion()));
        
        ctx.pipeline().replace(this, "packet-handler", nextState.getHandler().getConstructor().newInstance());
	}
}
