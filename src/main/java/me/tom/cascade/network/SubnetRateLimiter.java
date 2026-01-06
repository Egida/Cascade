package me.tom.cascade.network;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

public class SubnetRateLimiter {
    private static int MAX_CONNECTIONS_PER_SUBNET_PER_SECOND = 10;

    private static final Cache<String, AtomicInteger> subnetCounters =
            CacheBuilder.newBuilder()
                    .expireAfterWrite(1, TimeUnit.SECONDS)
                    .build();

    public static boolean tryAcquire(String ip) {
        String subnet = get24Subnet(ip);

        AtomicInteger counter = subnetCounters.getIfPresent(subnet);
        if (counter == null) {
            counter = new AtomicInteger(0);
            subnetCounters.put(subnet, counter);
        }

        return counter.incrementAndGet() <= MAX_CONNECTIONS_PER_SUBNET_PER_SECOND;
    }
    
    public static String get24Subnet(String ip) {
        int lastDot = ip.lastIndexOf('.');
        if (lastDot == -1) return ip;
        return ip.substring(0, lastDot);
    }

}