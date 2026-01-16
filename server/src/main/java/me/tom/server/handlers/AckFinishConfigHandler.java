package me.tom.server.handlers;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import me.tom.common.network.protocol.ProtocolAttributes;
import me.tom.common.network.protocol.ProtocolState;
import me.tom.common.network.protocol.packet.packets.serverbound.AckFinishConfigPacket;

public class AckFinishConfigHandler extends SimpleChannelInboundHandler<AckFinishConfigPacket> {
	@Override
	protected void channelRead0(ChannelHandlerContext ctx, AckFinishConfigPacket packet) throws Exception {
		ctx.channel().attr(ProtocolAttributes.STATE).set(ProtocolState.PLAY);
	}
}