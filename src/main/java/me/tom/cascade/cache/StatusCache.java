package me.tom.cascade.cache;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.socket.nio.NioSocketChannel;
import me.tom.cascade.CascadeBootstrap;
import me.tom.cascade.network.pipeline.StatusPipelineInitializer;
import me.tom.cascade.network.protocol.ProtocolAttributes;
import me.tom.cascade.network.protocol.ProtocolState;
import me.tom.cascade.network.protocol.ProtocolVersion;
import me.tom.cascade.network.protocol.packet.packets.serverbound.HandshakePacket;
import me.tom.cascade.network.protocol.packet.packets.serverbound.StatusRequestPacket;

public class StatusCache {

    private static final long CACHE_DURATION_MS = 5000;

    private final AtomicReference<String> cachedStatus = new AtomicReference<>();
    private volatile long lastUpdate = 0;

    private final Object lock = new Object();

    public String getStatus() {
        long now = System.currentTimeMillis();

        String current = cachedStatus.get();
        if (current != null && (now - lastUpdate) < CACHE_DURATION_MS) {
            return current;
        }

        synchronized (lock) {
            current = cachedStatus.get();
            if (current != null && (now - lastUpdate) < CACHE_DURATION_MS) {
                return current;
            }

            String newStatus = fetchBackendStatus();
            cachedStatus.set(newStatus);
            lastUpdate = now;

            return newStatus;
        }
    }

    private String fetchBackendStatus() {
        CompletableFuture<String> future = new CompletableFuture<>();

        Bootstrap bootstrap = new Bootstrap()
                .channel(NioSocketChannel.class)
                .group(CascadeBootstrap.EVENT_LOOP_GROUP)
                .handler(new StatusPipelineInitializer(future));

        bootstrap.connect(
                CascadeBootstrap.CONFIG.getTargetHost(),
                CascadeBootstrap.CONFIG.getTargetPort()
        ).addListener((ChannelFutureListener) connectFuture -> {
            if (!connectFuture.isSuccess()) {
                future.complete(null);
                return;
            }

            Channel channel = connectFuture.channel();
            channel.attr(ProtocolAttributes.STATE).set(ProtocolState.HANDSHAKE);
            channel.attr(ProtocolAttributes.PROTOCOL_VERSION).set(ProtocolVersion.MAXIMUM_VERSION);

            HandshakePacket handshake = new HandshakePacket(
                    -1,
                    CascadeBootstrap.CONFIG.getTargetHost(),
                    CascadeBootstrap.CONFIG.getTargetPort(),
                    ProtocolState.STATUS.ordinal()
            );

            StatusRequestPacket statusRequest = new StatusRequestPacket();

            channel.writeAndFlush(handshake).addListener(f -> {
                channel.attr(ProtocolAttributes.STATE).set(ProtocolState.STATUS);
                channel.writeAndFlush(statusRequest);
            });
        });

        try {
            return future.get(2, TimeUnit.SECONDS);
        } catch (Exception e) {
            return null;
        }
    }
}