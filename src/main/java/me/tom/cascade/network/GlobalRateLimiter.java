package me.tom.cascade.network;

import java.util.concurrent.atomic.AtomicInteger;

public class GlobalRateLimiter {

    private static final int MAX_CONNECTIONS_PER_SECOND = 200;

    private static final AtomicInteger counter = new AtomicInteger(0);
    private static volatile long lastReset = System.currentTimeMillis();

    public static boolean tryAcquire() {
        long now = System.currentTimeMillis();

        if (now - lastReset >= 1000) {
            counter.set(0);
            lastReset = now;
        }

        return counter.incrementAndGet() <= MAX_CONNECTIONS_PER_SECOND;
    }
}