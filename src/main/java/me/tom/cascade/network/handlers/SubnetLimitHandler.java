package me.tom.cascade.network.handlers;

import java.net.InetSocketAddress;

import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import me.tom.cascade.network.SubnetRateLimiter;

@Sharable
public class SubnetLimitHandler extends ChannelInboundHandlerAdapter {

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        String ip = ((InetSocketAddress) ctx.channel().remoteAddress())
                .getAddress()
                .getHostAddress();

        if (!SubnetRateLimiter.tryAcquire(ip)) {
            ctx.close();
            return;
        }

        super.channelActive(ctx);
    }
}