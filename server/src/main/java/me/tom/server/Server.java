package me.tom.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.DefaultEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import lombok.AllArgsConstructor;
import me.tom.server.pipeline.PipelineInitializer;

@AllArgsConstructor
public abstract class Server extends Thread {
	private int port;
	
	@Override
	public void run() {
        ServerBootstrap bootstrap = new ServerBootstrap()
                .group(new DefaultEventLoopGroup())
                .channel(NioServerSocketChannel.class)
                .childHandler(new PipelineInitializer());
        
        bootstrap.bind(port);
	}
}