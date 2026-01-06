package me.tom.cascade.network.pipeline;

import java.util.concurrent.CompletableFuture;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.socket.SocketChannel;
import me.tom.cascade.network.NetworkSide;
import me.tom.cascade.network.protocol.codec.PacketDecoder;
import me.tom.cascade.network.protocol.codec.PacketEncoder;
import me.tom.cascade.network.protocol.codec.PacketFramer;
import me.tom.cascade.network.protocol.packet.Packet;
import me.tom.cascade.network.protocol.packet.packets.clientbound.StatusResponsePacket;

public class StatusPipelineInitializer extends ChannelInitializer<SocketChannel> {
	
    private final CompletableFuture<String> callback;

    public StatusPipelineInitializer(CompletableFuture<String> callback) {
        this.callback = callback;
    }

    @Override
    protected void initChannel(SocketChannel ch) {
        ch.pipeline()
                .addLast("framer", new PacketFramer())
                .addLast("decoder", new PacketDecoder(NetworkSide.CLIENTBOUND))
                .addLast("encoder", new PacketEncoder(NetworkSide.SERVERBOUND))
                .addLast("handler", new SimpleChannelInboundHandler<Packet>() {

                    @Override
                    protected void channelRead0(ChannelHandlerContext ctx, Packet msg) {
                        if (msg instanceof StatusResponsePacket response) {
                            callback.complete(response.getJson());
                            ctx.close();
                        }
                    }

                    @Override
                    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
                        ctx.close();
                    }
                });
    }
}