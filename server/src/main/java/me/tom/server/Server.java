package me.tom.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import lombok.AllArgsConstructor;
import me.tom.server.pipeline.PipelineInitializer;

@AllArgsConstructor
public abstract class Server extends Thread {
    protected int port;

    public int getPort() {
        return port;
    }

    @Override
    public void run() {
        NioEventLoopGroup boss = new NioEventLoopGroup(1);
        NioEventLoopGroup worker = new NioEventLoopGroup();

        try {
            ServerBootstrap bootstrap = new ServerBootstrap()
                    .group(boss, worker)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new PipelineInitializer());

            ChannelFuture channelFuture = bootstrap.bind(port).sync();

            this.port = ((java.net.InetSocketAddress) 
                         channelFuture.channel().localAddress()).getPort();

            channelFuture.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            boss.shutdownGracefully();
            worker.shutdownGracefully();
        }
    }
}