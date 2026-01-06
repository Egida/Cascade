package me.tom.cascade.network;

import java.net.InetSocketAddress;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

import io.netty.channel.ChannelHandlerContext;

public class StrikeManager {
    private static final Cache<String, AtomicInteger> strikes = CacheBuilder.newBuilder()
            .expireAfterWrite(10, TimeUnit.MINUTES)
            .build();

    public static int addStrike(ChannelHandlerContext ctx) {
    	String ip = ((InetSocketAddress) ctx.channel().remoteAddress())
	        .getAddress()
	        .getHostAddress();
    	
        AtomicInteger count = strikes.getIfPresent(ip);
        if (count == null) {
            count = new AtomicInteger(0);
            strikes.put(ip, count);
        }
        return count.incrementAndGet();
    }
}