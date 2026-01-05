package me.tom.cascade.network.handlers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import me.tom.cascade.CascadeBootstrap;
import me.tom.cascade.network.protocol.ProtocolAttributes;
import me.tom.cascade.network.protocol.ProtocolVersion;
import me.tom.cascade.network.protocol.packet.Packet;
import me.tom.cascade.network.protocol.packet.packets.clientbound.PongResponsePacket;
import me.tom.cascade.network.protocol.packet.packets.clientbound.StatusResponsePacket;
import me.tom.cascade.network.protocol.packet.packets.serverbound.PingRequestPacket;
import me.tom.cascade.network.protocol.packet.packets.serverbound.StatusRequestPacket;
import me.tom.cascade.status.ServerDescription;
import me.tom.cascade.status.ServerStatus;
import me.tom.cascade.status.ServerVersion;

public class StatusHandler extends SimpleChannelInboundHandler<Packet> {
    private static final Gson GSON = new GsonBuilder().create();

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Packet packet) {
        if (packet instanceof StatusRequestPacket) {
            onStatusRequest(ctx);
        } else if (packet instanceof PingRequestPacket ping) {
            ctx.writeAndFlush(new PongResponsePacket(ping.getTimestamp()));
        }
    }

    private void onStatusRequest(ChannelHandlerContext ctx) {
        ProtocolVersion pv = ctx.channel().attr(ProtocolAttributes.PROTOCOL_VERSION).get();

        ServerVersion version = pv.isVersionSupported()
                ? new ServerVersion(pv.getName(), pv.getVersionNumber())
                : new ServerVersion(
                        ProtocolVersion.MINIMUM_VERSION == ProtocolVersion.MAXIMUM_VERSION
                                ? ProtocolVersion.MINIMUM_VERSION.getName()
                                : ProtocolVersion.MINIMUM_VERSION.getName() + "-" + ProtocolVersion.MAXIMUM_VERSION.getName(),
                        -1
                );

        ServerStatus status = new ServerStatus(
                version,
                null,
                new ServerDescription(CascadeBootstrap.CONFIG.getProxyDescription())
        );

        ctx.writeAndFlush(new StatusResponsePacket(GSON.toJson(status)));
    }
}