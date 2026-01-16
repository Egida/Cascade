package me.tom.server.pipeline;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import me.tom.common.network.NetworkSide;
import me.tom.common.network.protocol.ProtocolAttributes;
import me.tom.common.network.protocol.ProtocolState;
import me.tom.common.network.protocol.ProtocolVersion;
import me.tom.common.network.protocol.packet.codec.PacketDecoder;
import me.tom.common.network.protocol.packet.codec.PacketEncoder;
import me.tom.common.network.protocol.packet.codec.PacketFramer;
import me.tom.server.handlers.HandshakeHandler;
import me.tom.server.handlers.LoginStartHandler;

public class PipelineInitializer extends ChannelInitializer<SocketChannel> {
	
    @Override
    protected void initChannel(SocketChannel ch) {
	    ch.attr(ProtocolAttributes.STATE).set(ProtocolState.HANDSHAKE);
	    ch.attr(ProtocolAttributes.PROTOCOL_VERSION).set(ProtocolVersion.UNKNOWN);
	    
    	ch.pipeline()
    		.addLast(new PacketFramer())
    		.addLast(new PacketDecoder(NetworkSide.SERVERBOUND))
    		.addLast(new PacketEncoder(NetworkSide.CLIENTBOUND))
    		.addLast(new HandshakeHandler())
    		.addLast(new LoginStartHandler());
    }
}