package me.tom.cascade.network.handlers.limit;

import java.net.InetSocketAddress;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.RequiredArgsConstructor;

@Sharable
@RequiredArgsConstructor
public class IpRateLimitHandler extends ChannelInboundHandlerAdapter {

    private final int maxConnectionsPerSecond;

    private final Cache<String, AtomicInteger> connectionAttempts = CacheBuilder.newBuilder()
            .expireAfterWrite(1, TimeUnit.SECONDS)
            .build();

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

        if (current > maxConnectionsPerSecond) {
            ctx.close();
            return;
        }

        super.channelActive(ctx);
    }
}