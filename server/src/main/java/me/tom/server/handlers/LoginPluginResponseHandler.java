package me.tom.server.handlers;

import java.util.UUID;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import me.tom.common.network.protocol.packet.packets.clientbound.login.LoginSuccessPacket;
import me.tom.common.network.protocol.packet.packets.serverbound.login.LoginPluginResponsePacket;
import me.tom.common.network.protocol.packet.types.GameProfile;

public class LoginPluginResponseHandler extends SimpleChannelInboundHandler<LoginPluginResponsePacket> {
	@Override
	protected void channelRead0(ChannelHandlerContext ctx, LoginPluginResponsePacket packet) throws Exception {
		LoginSuccessPacket loginSuccess = new LoginSuccessPacket(new GameProfile(UUID.randomUUID(), ""));
		ctx.writeAndFlush(loginSuccess);
	}
}