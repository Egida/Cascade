package me.tom.server.handlers;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import me.tom.common.network.protocol.ProtocolAttributes;
import me.tom.common.network.protocol.ProtocolState;
import me.tom.common.network.protocol.packet.packets.serverbound.handshake.HandshakePacket;

public class HandshakeHandler extends SimpleChannelInboundHandler<HandshakePacket> {

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, HandshakePacket packet) {
        ctx.channel().attr(ProtocolAttributes.STATE).set(packet.getNextState());
        
        if(ctx.channel().attr(ProtocolAttributes.STATE).get() == ProtocolState.TRANSFER)
        	return;
        
        ctx.channel().attr(ProtocolAttributes.HOSTNAME).set(packet.getHostname());
        ctx.channel().attr(ProtocolAttributes.PORT).set(packet.getPort());
    }
}