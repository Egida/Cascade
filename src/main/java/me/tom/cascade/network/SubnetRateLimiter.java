package me.tom.cascade.network;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class SubnetRateLimiter {
    private static final Cache<String, AtomicInteger> SUBNET_COUNTERS =
            CacheBuilder.newBuilder()
                    .expireAfterWrite(1, TimeUnit.SECONDS)
                    .build();

    public static boolean tryAcquire(String ip, int maxConnectionsPerSubnetPerSecond) {
        String subnet = get24Subnet(ip);

        AtomicInteger counter = SUBNET_COUNTERS.getIfPresent(subnet);
        if (counter == null) {
            counter = new AtomicInteger(0);
            SUBNET_COUNTERS.put(subnet, counter);
        }

        return counter.incrementAndGet() <= maxConnectionsPerSubnetPerSecond;
    }
    
    public static String get24Subnet(String ip) {
        int lastDot = ip.lastIndexOf('.');
        if (lastDot == -1) return ip;
        return ip.substring(0, lastDot);
    }

}