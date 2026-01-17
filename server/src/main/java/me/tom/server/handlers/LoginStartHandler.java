package me.tom.server.handlers;

import java.util.UUID;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import me.tom.common.network.protocol.ProtocolAttributes;
import me.tom.common.network.protocol.packet.packets.clientbound.LoginSuccessPacket;
import me.tom.common.network.protocol.packet.packets.serverbound.LoginStartPacket;
import me.tom.common.network.protocol.packet.types.GameProfile;

public class LoginStartHandler extends SimpleChannelInboundHandler<LoginStartPacket> {

	@Override
	protected void channelRead0(ChannelHandlerContext ctx, LoginStartPacket packet) throws Exception {
		LoginSuccessPacket loginSuccess = new LoginSuccessPacket(new GameProfile(UUID.randomUUID(), "", null));
		ctx.channel().attr(ProtocolAttributes.UUID).set(packet.getUuid());
		
		ctx.writeAndFlush(loginSuccess);
	}
}