package me.tom.server.handlers;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import me.tom.common.network.protocol.ProtocolAttributes;
import me.tom.common.network.protocol.ProtocolState;
import me.tom.common.network.protocol.packet.packets.clientbound.PluginMessage;
import me.tom.common.network.protocol.packet.packets.serverbound.AckFinishConfigPacket;
import me.tom.common.network.protocol.packet.types.UuidType;

public class AckFinishConfigHandler extends SimpleChannelInboundHandler<AckFinishConfigPacket> {
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, AckFinishConfigPacket packet) throws Exception {
        ctx.channel().attr(ProtocolAttributes.STATE).set(ProtocolState.PLAY);
        
        ByteBuf buf = Unpooled.buffer(16);
        UuidType.write(buf, ctx.channel().attr(ProtocolAttributes.UUID).get());
        
        PluginMessage pluginMessage = new PluginMessage("cascade:login", ByteBufUtil.getBytes(buf));
        ctx.writeAndFlush(pluginMessage);
    }
}