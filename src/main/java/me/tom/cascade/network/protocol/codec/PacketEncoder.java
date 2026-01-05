package me.tom.cascade.network.protocol.codec;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.EncoderException;
import io.netty.handler.codec.MessageToByteEncoder;
import lombok.AllArgsConstructor;
import me.tom.cascade.network.NetworkSide;
import me.tom.cascade.network.protocol.ProtocolAttributes;
import me.tom.cascade.network.protocol.ProtocolState;
import me.tom.cascade.network.protocol.ProtocolVersion;
import me.tom.cascade.network.protocol.packet.Packet;
import me.tom.cascade.network.protocol.types.VarInt;

@AllArgsConstructor
public class PacketEncoder extends MessageToByteEncoder<Packet> {
	private final NetworkSide side;
	
    @Override
    protected void encode(ChannelHandlerContext ctx, Packet packet, ByteBuf out) throws Exception {
        ProtocolState state = ctx.channel().attr(ProtocolAttributes.STATE).get();
        ProtocolVersion protocolVersion = ctx.channel().attr(ProtocolAttributes.PROTOCOL_VERSION).get();

        ByteBuf body = ctx.alloc().buffer();
        int packetId = state.getRegistry().getPacketId(protocolVersion, side, packet.getClass());

        if (packetId == -1) {
        	throw new EncoderException("Unknown packet ID for packet " + packet);
        }

        VarInt.write(body, packetId);
        packet.encode(body);

        VarInt.write(out, body.readableBytes());
        out.writeBytes(body);

        body.release();
    }
}