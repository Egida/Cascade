package me.tom.server;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import io.netty.channel.ChannelHandlerContext;

public class LoginQueue {
    private static final Queue<ChannelHandlerContext> QUEUE = new ConcurrentLinkedQueue<>();

    public static void enqueue(ChannelHandlerContext ctx) {
        QUEUE.add(ctx);
    }

    public static ChannelHandlerContext poll() {
        return QUEUE.poll();
    }
}
