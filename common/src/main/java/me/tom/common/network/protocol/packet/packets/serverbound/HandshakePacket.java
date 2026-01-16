package me.tom.common.network.protocol.packet.packets.serverbound;

import io.netty.buffer.ByteBuf;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import me.tom.common.network.protocol.ProtocolState;
import me.tom.common.network.protocol.packet.Packet;
import me.tom.common.network.protocol.packet.types.Utf8String;
import me.tom.common.network.protocol.packet.types.VarInt;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class HandshakePacket implements Packet {

    public int protocolVersion;
    public String hostname;
    public int port;
    public ProtocolState nextState;

    @Override
    public void decode(ByteBuf in) {
        protocolVersion = VarInt.read(in);
        hostname = Utf8String.read(in, 255);
        port = in.readUnsignedShort();
        nextState = ProtocolState.values()[VarInt.read(in)];
    }

    @Override
    public void encode(ByteBuf out) {
        VarInt.write(out, protocolVersion);
        Utf8String.write(out, hostname, 255);
        out.writeShort(port);
        VarInt.write(out, nextState.ordinal());
    }
}