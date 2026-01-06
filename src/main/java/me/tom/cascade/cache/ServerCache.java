package me.tom.cascade.cache;

import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.concurrent.atomic.AtomicBoolean;

import me.tom.cascade.CascadeBootstrap;

public class ServerCache {

    private static final long CACHE_DURATION_MS = 5000;

    private final AtomicBoolean online = new AtomicBoolean(false);
    private volatile long lastCheck = 0;

    private final Object lock = new Object();

    public boolean isOnline() {
        long now = System.currentTimeMillis();

        if ((now - lastCheck) < CACHE_DURATION_MS) {
            return online.get();
        }

        synchronized (lock) {
            if ((now - lastCheck) < CACHE_DURATION_MS) {
                return online.get();
            }

            boolean result = checkBackend();
            online.set(result);
            lastCheck = now;

            return result;
        }
    }

    private boolean checkBackend() {
        try (Socket socket = new Socket()) {
            socket.connect(
                new InetSocketAddress(
                    CascadeBootstrap.CONFIG.getTargetHost(),
                    CascadeBootstrap.CONFIG.getTargetPort()
                ),
                500
            );
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}