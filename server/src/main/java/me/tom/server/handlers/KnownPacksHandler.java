package me.tom.server.handlers;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import me.tom.common.network.protocol.packet.packets.clientbound.config.FinishConfigPacket;
import me.tom.common.network.protocol.packet.packets.serverbound.config.KnownPacksPacket;

public class KnownPacksHandler extends SimpleChannelInboundHandler<KnownPacksPacket> {

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, KnownPacksPacket packet) throws Exception {
    	ctx.writeAndFlush(new FinishConfigPacket());
    }
}