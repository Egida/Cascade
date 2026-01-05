package me.tom.cascade.network.protocol.packet.packets.clientbound;

import io.netty.buffer.ByteBuf;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import me.tom.cascade.network.protocol.packet.Packet;

@AllArgsConstructor
@NoArgsConstructor
public class PongResponsePacket implements Packet {
	
    public long timestamp;

    @Override
    public void decode(ByteBuf in) {
    	in.readLong();
    }

    @Override
    public void encode(ByteBuf out) {
    	out.writeLong(timestamp);
    }
}