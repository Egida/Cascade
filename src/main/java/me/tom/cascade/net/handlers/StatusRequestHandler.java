package me.tom.cascade.net.handlers;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import me.tom.cascade.CascadeBootstrap;
import me.tom.cascade.protocol.packet.packets.clientbound.StatusResponsePacket;
import me.tom.cascade.protocol.packet.packets.serverbound.StatusRequestPacket;

public class StatusRequestHandler extends SimpleChannelInboundHandler<StatusRequestPacket> {
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, StatusRequestPacket packet) {
        StatusResponsePacket statusResponse = new StatusResponsePacket(CascadeBootstrap.PROXY_STATUS_JSON);
        ctx.writeAndFlush(statusResponse);
    }
}