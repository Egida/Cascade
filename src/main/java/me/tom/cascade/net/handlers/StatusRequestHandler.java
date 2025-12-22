package me.tom.cascade.net.handlers;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import me.tom.cascade.protocol.packet.packets.c2s.StatusRequestPacket;

public class StatusRequestHandler extends SimpleChannelInboundHandler<StatusRequestPacket> {
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, StatusRequestPacket packet) {
    	
    }
}