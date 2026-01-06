package me.tom.cascade.network;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

import java.util.concurrent.TimeUnit;

public class BanManager {
    private static final Cache<String, Long> bans = CacheBuilder.newBuilder()
            .expireAfterWrite(10, TimeUnit.MINUTES)
            .build();

    public static void ban(String ip, long durationMillis) {
        long expiresAt = System.currentTimeMillis() + durationMillis;
        bans.put(ip, expiresAt);
    }

    public static boolean isBanned(String ip) {
        Long expiresAt = bans.getIfPresent(ip);
        if (expiresAt == null) return false;

        if (System.currentTimeMillis() > expiresAt) {
            bans.invalidate(ip);
            return false;
        }

        return true;
    }

    public static void ban(io.netty.channel.ChannelHandlerContext ctx, long durationMillis) {
        String ip = ((java.net.InetSocketAddress) ctx.channel().remoteAddress())
                .getAddress()
                .getHostAddress();
        ban(ip, durationMillis);
    }
}