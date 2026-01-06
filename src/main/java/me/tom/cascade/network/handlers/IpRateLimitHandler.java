package me.tom.cascade.network.handlers;

import java.net.InetSocketAddress;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

@Sharable
public class IpRateLimitHandler extends ChannelInboundHandlerAdapter {

    private final int maxConnectionsPerSecond;

    private final Cache<String, AtomicInteger> connectionAttempts = CacheBuilder.newBuilder()
            .expireAfterWrite(1, TimeUnit.SECONDS)
            .build();

    public IpRateLimitHandler(int maxConnectionsPerSecond) {
        this.maxConnectionsPerSecond = maxConnectionsPerSecond;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        String ip = ((InetSocketAddress) ctx.channel().remoteAddress())
                .getAddress()
                .getHostAddress();

        AtomicInteger counter = connectionAttempts.getIfPresent(ip);
        if (counter == null) {
            counter = new AtomicInteger(0);
            connectionAttempts.put(ip, counter);
        }

        int current = counter.incrementAndGet();

        System.out.println(current);
        if (current > maxConnectionsPerSecond) {
        	System.out.println("limit?");
            ctx.close();
            return;
        }

        super.channelActive(ctx);
    }
}