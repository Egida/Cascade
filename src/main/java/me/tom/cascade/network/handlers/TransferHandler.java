package me.tom.cascade.network.handlers;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.RequiredArgsConstructor;
import me.tom.cascade.network.protocol.packet.Packet;

@RequiredArgsConstructor
public class TransferHandler extends SimpleChannelInboundHandler<Packet> {
	@Override
	protected void channelRead0(ChannelHandlerContext ctx, Packet msg) throws Exception {}
}