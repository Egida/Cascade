package me.tom.cascade.network.handlers.minecraft;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import me.tom.cascade.CascadeBootstrap;
import me.tom.cascade.network.protocol.packet.Packet;
import me.tom.cascade.network.protocol.packet.packets.clientbound.PongResponsePacket;
import me.tom.cascade.network.protocol.packet.packets.clientbound.StatusResponsePacket;
import me.tom.cascade.network.protocol.packet.packets.serverbound.PingRequestPacket;
import me.tom.cascade.network.protocol.packet.packets.serverbound.StatusRequestPacket;

public class StatusHandler extends SimpleChannelInboundHandler<Packet> {
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Packet packet) {
        if (packet instanceof StatusRequestPacket) {
            onStatusRequest(ctx);
        } else if (packet instanceof PingRequestPacket ping) {
            ctx.writeAndFlush(new PongResponsePacket(ping.getTimestamp()));
        }
    }

    private void onStatusRequest(ChannelHandlerContext ctx) {
    	if(CascadeBootstrap.SERVER_CACHE.isOnline()) {
    		ctx.writeAndFlush(new StatusResponsePacket(CascadeBootstrap.STATUS_CACHE.getStatus()));
    	} else {
    		ctx.writeAndFlush(new StatusResponsePacket("{\"version\":{\"name\":\"Proxy Error\",\"protocol\":0},\"players\":{\"max\":0,\"online\":0},\"description\":{\"text\":\"§cProxy not configured correctly: backend is unreachable.\"}}"));
    	}
    }
}