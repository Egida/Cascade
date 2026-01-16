package me.tom.server.handlers;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import me.tom.common.network.protocol.packet.packets.serverbound.LoginStartPacket;

public class LoginStartHandler extends SimpleChannelInboundHandler<LoginStartPacket> {

	@Override
	protected void channelRead0(ChannelHandlerContext ctx, LoginStartPacket packet) throws Exception {
		
	}
}