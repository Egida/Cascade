package me.tom.cascade.network;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import lombok.AllArgsConstructor;
import me.tom.cascade.CascadeBootstrap;
import me.tom.cascade.network.pipeline.PipelineInitializer;

@AllArgsConstructor
public class CascadeProxy {
	private static final Logger LOGGER = LoggerFactory.getLogger(CascadeProxy.class);
    private final int port;

    public void start() throws InterruptedException {
        ServerBootstrap bootstrap = new ServerBootstrap()
                .group(CascadeBootstrap.EVENT_LOOP_GROUP)
                .channel(NioServerSocketChannel.class)
                .childHandler(new PipelineInitializer());

        ChannelFuture future = bootstrap.bind(port).sync();
        LOGGER.info("Proxy server started on port {}", port);
        LOGGER.debug("Server is directed at target host {} on port {}", CascadeBootstrap.CONFIG.getTargetHost(), CascadeBootstrap.CONFIG.getTargetPort());
        
        if(!CascadeBootstrap.CONFIG.isAuthVerification()) {
        	LOGGER.warn("Authentication verification is not enabled! Offline accounts are able to reach the backend!");
        }

        future.channel().closeFuture().sync();
    }
}