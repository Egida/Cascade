package me.tom.cascade.net.pipeline;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import me.tom.cascade.net.NetworkSide;
import me.tom.cascade.net.handlers.ConnectionHandler;
import me.tom.cascade.net.handlers.HandshakeHandler;
import me.tom.cascade.protocol.codec.PacketDecoder;
import me.tom.cascade.protocol.codec.PacketEncoder;
import me.tom.cascade.protocol.codec.PacketFramer;

public class PipelineInitializer extends ChannelInitializer<SocketChannel> {

    @Override
    protected void initChannel(SocketChannel ch) {
        ch.pipeline()
            .addLast("packet-framer", new PacketFramer())
            .addLast("packet-decoder", new PacketDecoder(NetworkSide.SERVERBOUND))
            .addLast("packet-encoder", new PacketEncoder(NetworkSide.CLIENTBOUND))
            .addLast("packet-handler", new HandshakeHandler())
            .addLast("connection-handler", new ConnectionHandler());
    }
}