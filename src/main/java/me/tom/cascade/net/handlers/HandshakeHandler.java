package me.tom.cascade.net.handlers;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.RequiredArgsConstructor;
import me.tom.cascade.protocol.ConnectionState;
import me.tom.cascade.protocol.ProtocolAttributes;
import me.tom.cascade.protocol.packet.Packet;
import me.tom.cascade.protocol.packet.packets.serverbound.HandshakePacket;

import static me.tom.cascade.protocol.ConnectionState.*;

@RequiredArgsConstructor
public class HandshakeHandler extends SimpleChannelInboundHandler<Packet> {
	@Override
    protected void channelRead0(ChannelHandlerContext ctx, Packet packet) throws InstantiationException, IllegalAccessException {
		if(packet instanceof HandshakePacket) {
			HandshakePacket handshake = (HandshakePacket)packet;

			ConnectionState nextState = ConnectionState.values()[handshake.getNextState()];

			boolean serverTransfer = false;
	    	if(nextState == TRANSFER) {
	    		nextState = LOGIN;
	    		serverTransfer = true;
	    	}

	        ctx.channel().attr(ProtocolAttributes.TRANSFER).set(serverTransfer);
	        ctx.channel().attr(ProtocolAttributes.STATE).set(nextState);
	        
	        ctx.pipeline().replace(this, "packet-handler", nextState.getHandler().newInstance());
		} else {
			ctx.fireChannelRead(packet);
		}
    }
}
