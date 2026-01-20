package me.tom.server;

import java.util.concurrent.TimeUnit;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.EventLoop;
import me.tom.common.network.protocol.ProtocolAttributes;
import me.tom.common.network.protocol.packet.packets.clientbound.play.TransferPacket;
import me.tom.common.network.protocol.packet.packets.serverbound.config.PluginMessage;
import me.tom.common.network.protocol.packet.types.Utf8String;

public class LoginQueueProcessor {
    public static void start(EventLoop eventLoop) {
        eventLoop.scheduleAtFixedRate(() -> {

            while (true) {
                ChannelHandlerContext ctx = LoginQueue.poll();
                if (ctx == null) return;

                if (!ctx.channel().isActive() || !ctx.channel().isOpen()) {
                    continue;
                }

                ByteBuf buf = ctx.alloc().buffer();
                Utf8String.write(buf, ctx.channel().attr(ProtocolAttributes.USERNAME).get(), 16);

                int len = buf.readableBytes();
                byte[] payload = new byte[len];
                buf.readBytes(payload);
                buf.release();
                
                ctx.writeAndFlush(new PluginMessage("cascade:login", payload))
                .addListener(f -> {
                    if (!f.isSuccess()) {
                        f.cause().printStackTrace();
                    }
                });
                ctx.writeAndFlush(new TransferPacket(
                        ctx.channel().attr(ProtocolAttributes.HOSTNAME).get(),
                        ctx.channel().attr(ProtocolAttributes.PORT).get()
                ));

                ctx.close();
                return;
            }
        }, 0, 50, TimeUnit.MILLISECONDS);
    }
}