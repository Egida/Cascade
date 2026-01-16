package me.tom.common.network.protocol.packet.codec;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.EncoderException;
import io.netty.handler.codec.MessageToByteEncoder;
import lombok.AllArgsConstructor;
import me.tom.common.network.NetworkSide;
import me.tom.common.network.protocol.ProtocolAttributes;
import me.tom.common.network.protocol.ProtocolState;
import me.tom.common.network.protocol.ProtocolVersion;
import me.tom.common.network.protocol.packet.Packet;
import me.tom.common.network.protocol.packet.types.VarInt;

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