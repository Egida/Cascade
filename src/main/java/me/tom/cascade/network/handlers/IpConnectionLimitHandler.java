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
public class IpConnectionLimitHandler extends ChannelInboundHandlerAdapter {

    private final int maxConnectionsPerIp;

    private final Cache<String, AtomicInteger> connectionCounts = CacheBuilder.newBuilder()
            .expireAfterAccess(10, TimeUnit.MINUTES)
            .build();

    public IpConnectionLimitHandler(int maxConnectionsPerIp) {
        this.maxConnectionsPerIp = maxConnectionsPerIp;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        String ip = ((InetSocketAddress) ctx.channel().remoteAddress())
                .getAddress()
                .getHostAddress();

        AtomicInteger counter = connectionCounts.getIfPresent(ip);
        if (counter == null) {
            counter = new AtomicInteger(0);
            connectionCounts.put(ip, counter);
        }

        int current = counter.incrementAndGet();

        if (current > maxConnectionsPerIp) {
            counter.decrementAndGet();
            ctx.close();
            return;
        }

        super.channelActive(ctx);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        String ip = ((InetSocketAddress) ctx.channel().remoteAddress())
                .getAddress()
                .getHostAddress();

        AtomicInteger counter = connectionCounts.getIfPresent(ip);
        if (counter != null) {
            if (counter.decrementAndGet() <= 0) {
                connectionCounts.invalidate(ip);
            }
        }

        super.channelInactive(ctx);
    }
}