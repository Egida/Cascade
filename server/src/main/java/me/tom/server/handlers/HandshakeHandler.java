package me.tom.server.handlers;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import me.tom.common.network.protocol.ProtocolAttributes;
import me.tom.common.network.protocol.packet.packets.serverbound.HandshakePacket;

public class HandshakeHandler extends SimpleChannelInboundHandler<HandshakePacket> {

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, HandshakePacket packet) {
        ctx.channel().attr(ProtocolAttributes.STATE).set(packet.getNextState());
    }
}