package me.tom.cascade.network.handlers.limit;

import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import me.tom.cascade.network.GlobalRateLimiter;

@Sharable
public class GlobalLimitHandler extends ChannelInboundHandlerAdapter {

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        if (!GlobalRateLimiter.tryAcquire()) {
            ctx.close();
            return;
        }

        super.channelActive(ctx);
    }
}