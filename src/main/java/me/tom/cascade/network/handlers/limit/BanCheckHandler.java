package me.tom.cascade.network.handlers.limit;

import java.net.InetSocketAddress;

import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import me.tom.cascade.network.BanManager;

@Sharable
public class BanCheckHandler extends ChannelInboundHandlerAdapter {

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        String ip = ((InetSocketAddress) ctx.channel().remoteAddress())
                .getAddress()
                .getHostAddress();

        if (BanManager.isBanned(ip)) {
            ctx.close();
            return;
        }

        super.channelActive(ctx);
    }
}