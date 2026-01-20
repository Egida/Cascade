package me.tom.server.handlers;

import java.security.SecureRandom;
import java.util.concurrent.TimeUnit;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.concurrent.ScheduledFuture;
import me.tom.common.network.protocol.packet.packets.clientbound.play.KeepAlivePacket;

public class KeepAliveHandler extends ChannelInboundHandlerAdapter {
	private SecureRandom rnd = new SecureRandom();
    private ScheduledFuture<?> task;

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        super.channelActive(ctx);
    }
    
    @Override
    public void handlerAdded(ChannelHandlerContext ctx) {
        if (ctx.channel().isActive()) {
            task = ctx.executor().scheduleAtFixedRate(() -> {
                if (ctx.channel().isActive()) {
                    ctx.writeAndFlush(new KeepAlivePacket(rnd.nextLong()));
                }
            }, 0, 20, TimeUnit.SECONDS);
        }
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        if (task != null) {
            task.cancel(false);
        }
        super.channelInactive(ctx);
    }
}