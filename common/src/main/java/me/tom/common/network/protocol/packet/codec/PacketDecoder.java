package me.tom.common.network.protocol.packet.codec;

import java.util.List;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;
import lombok.AllArgsConstructor;
import me.tom.common.network.NetworkSide;
import me.tom.common.network.protocol.ProtocolAttributes;
import me.tom.common.network.protocol.ProtocolState;
import me.tom.common.network.protocol.ProtocolVersion;
import me.tom.common.network.protocol.packet.Packet;
import me.tom.common.network.protocol.packet.types.VarInt;

@AllArgsConstructor
public class PacketDecoder extends MessageToMessageDecoder<ByteBuf> {
	private final NetworkSide side;
	
    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        ProtocolState state = ctx.channel().attr(ProtocolAttributes.STATE).get();
        ProtocolVersion protocolVersion = ctx.channel().attr(ProtocolAttributes.PROTOCOL_VERSION).get();
        int packetId = VarInt.read(in);

        Class<? extends Packet> clazz = state.getRegistry().getPacket(protocolVersion, side, packetId);
        if (clazz == null) {
        	byte[] remaining = new byte[in.readableBytes()];
        	in.readBytes(remaining);
        	return;
        }

        Packet packet = clazz.getConstructor().newInstance();
        packet.decode(in);
        System.out.println(packet);
        
        out.add(packet);
    }
}