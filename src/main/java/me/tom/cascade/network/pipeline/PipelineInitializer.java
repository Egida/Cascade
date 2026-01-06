package me.tom.cascade.network.pipeline;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import me.tom.cascade.network.NetworkSide;
import me.tom.cascade.network.handlers.BanCheckHandler;
import me.tom.cascade.network.handlers.ConnectionHandler;
import me.tom.cascade.network.handlers.GlobalLimitHandler;
import me.tom.cascade.network.handlers.HandshakeHandler;
import me.tom.cascade.network.handlers.IpConnectionLimitHandler;
import me.tom.cascade.network.handlers.IpRateLimitHandler;
import me.tom.cascade.network.protocol.codec.PacketDecoder;
import me.tom.cascade.network.protocol.codec.PacketEncoder;
import me.tom.cascade.network.protocol.codec.PacketFramer;

public class PipelineInitializer extends ChannelInitializer<SocketChannel> {
	private static final BanCheckHandler BAN_CHECK_HANDLER = new BanCheckHandler();
	private static final GlobalLimitHandler GLOBAL_LIMIT_HANDLER = new GlobalLimitHandler();
	private static final IpConnectionLimitHandler CONNECTION_LIMIT_HANDLER = new IpConnectionLimitHandler(2);
	private static final IpRateLimitHandler RATE_LIMIT_HANDLER = new IpRateLimitHandler(3);

    @Override
    protected void initChannel(SocketChannel ch) {
        ch.pipeline()
    		.addLast("ban-checker", BAN_CHECK_HANDLER)
			.addLast("global-rate-limiter", GLOBAL_LIMIT_HANDLER)
        	.addLast("per-ip-limiter", CONNECTION_LIMIT_HANDLER)
        	.addLast("per-ip-rate-limit", RATE_LIMIT_HANDLER)
            .addLast("packet-framer", new PacketFramer())
            .addLast("packet-decoder", new PacketDecoder(NetworkSide.SERVERBOUND))
            .addLast("packet-encoder", new PacketEncoder(NetworkSide.CLIENTBOUND))
            .addLast("packet-handler", new HandshakeHandler())
            .addLast("connection-handler", new ConnectionHandler());
    }
}