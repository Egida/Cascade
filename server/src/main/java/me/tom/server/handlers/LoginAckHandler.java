package me.tom.server.handlers;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import me.tom.common.network.protocol.ProtocolAttributes;
import me.tom.common.network.protocol.ProtocolState;
import me.tom.common.network.protocol.packet.packets.clientbound.FinishConfigurationPacket;
import me.tom.common.network.protocol.packet.packets.serverbound.LoginAckPacket;

public class LoginAckHandler extends SimpleChannelInboundHandler<LoginAckPacket> {
	@Override
	protected void channelRead0(ChannelHandlerContext ctx, LoginAckPacket packet) throws Exception {
		ctx.channel().attr(ProtocolAttributes.STATE).set(ProtocolState.CONFIGURATION);
		
		FinishConfigurationPacket finishConfiguration = new FinishConfigurationPacket();
		ctx.writeAndFlush(finishConfiguration);
	}
}