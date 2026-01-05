package me.tom.cascade.network.pipeline;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import me.tom.cascade.network.NetworkSide;
import me.tom.cascade.network.handlers.ConnectionHandler;
import me.tom.cascade.network.handlers.HandshakeHandler;
import me.tom.cascade.network.protocol.codec.PacketDecoder;
import me.tom.cascade.network.protocol.codec.PacketEncoder;
import me.tom.cascade.network.protocol.codec.PacketFramer;

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