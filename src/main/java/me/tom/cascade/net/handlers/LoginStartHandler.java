package me.tom.cascade.net.handlers;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import me.tom.cascade.protocol.packet.packets.c2s.LoginStartPacket;

public class LoginStartHandler extends SimpleChannelInboundHandler<LoginStartPacket> {

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, LoginStartPacket packet) {

    }
}