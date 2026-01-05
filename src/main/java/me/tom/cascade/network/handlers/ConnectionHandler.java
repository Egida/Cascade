package me.tom.cascade.network.handlers;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import me.tom.cascade.network.protocol.ProtocolAttributes;
import me.tom.cascade.network.protocol.ProtocolState;
import me.tom.cascade.network.protocol.ProtocolVersion;
import me.tom.cascade.network.protocol.types.VarInt;

public class ConnectionHandler extends SimpleChannelInboundHandler<ByteBuf> {
	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
	    ctx.channel().attr(ProtocolAttributes.STATE).set(ProtocolState.HANDSHAKE);
	    ctx.channel().attr(ProtocolAttributes.PROTOCOL_VERSION).set(ProtocolVersion.UNKNOWN);
	    super.channelActive(ctx);
	}

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, ByteBuf msg) {
        msg.readableBytes();
        VarInt.read(msg);
        msg.resetReaderIndex();
    }
}