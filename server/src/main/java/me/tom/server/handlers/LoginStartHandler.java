package me.tom.server.handlers;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import me.tom.common.network.protocol.ProtocolAttributes;
import me.tom.common.network.protocol.packet.packets.clientbound.login.LoginPluginRequestPacket;
import me.tom.common.network.protocol.packet.packets.serverbound.login.LoginStartPacket;

public class LoginStartHandler extends SimpleChannelInboundHandler<LoginStartPacket> {
	@Override
	protected void channelRead0(ChannelHandlerContext ctx, LoginStartPacket packet) throws Exception {
		byte[] data = {0x01};
		LoginPluginRequestPacket loginPluginRequest = new LoginPluginRequestPacket(0x00, "velocity:player_info", data);
		ctx.writeAndFlush(loginPluginRequest);
		
		ctx.channel().attr(ProtocolAttributes.USERNAME).set(packet.getName());
	}
}