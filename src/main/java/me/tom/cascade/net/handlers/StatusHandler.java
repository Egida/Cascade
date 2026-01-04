package me.tom.cascade.net.handlers;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import me.tom.cascade.CascadeBootstrap;
import me.tom.cascade.protocol.packet.Packet;
import me.tom.cascade.protocol.packet.packets.clientbound.PongResponsePacket;
import me.tom.cascade.protocol.packet.packets.clientbound.StatusResponsePacket;
import me.tom.cascade.protocol.packet.packets.serverbound.PingRequestPacket;
import me.tom.cascade.protocol.packet.packets.serverbound.StatusRequestPacket;

public class StatusHandler extends SimpleChannelInboundHandler<Packet> {
	@Override
	protected void channelRead0(ChannelHandlerContext ctx, Packet packet) throws Exception {
		if(packet instanceof StatusRequestPacket) {
	        StatusResponsePacket statusResponse = new StatusResponsePacket(CascadeBootstrap.PROXY_STATUS_JSON);
	        ctx.writeAndFlush(statusResponse);
		} else if(packet instanceof PingRequestPacket) {
			PingRequestPacket pingRequest = (PingRequestPacket)packet;
	        PongResponsePacket pongResponse = new PongResponsePacket(pingRequest.getTimestamp());
	
	        ctx.writeAndFlush(pongResponse);
		}
	}
}
