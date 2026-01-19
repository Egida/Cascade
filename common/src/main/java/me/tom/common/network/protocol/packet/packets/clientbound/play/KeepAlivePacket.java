package me.tom.common.network.protocol.packet.packets.clientbound.play;

import io.netty.buffer.ByteBuf;
import lombok.AllArgsConstructor;
import lombok.Getter;
import me.tom.common.network.protocol.packet.Packet;

@AllArgsConstructor
@Getter
public class KeepAlivePacket implements Packet {
	private long keepAliveId;
	
	@Override
	public void decode(ByteBuf in) throws Exception {
		keepAliveId = in.readLong();
	}

	@Override
	public void encode(ByteBuf out) throws Exception {
		out.writeLong(keepAliveId);
	}
}