package me.tom.common.network.protocol.packet.packets.clientbound.play;

import io.netty.buffer.ByteBuf;
import lombok.AllArgsConstructor;
import lombok.Getter;
import me.tom.common.network.protocol.packet.Packet;
import me.tom.common.network.protocol.packet.types.Utf8String;
import me.tom.common.network.protocol.packet.types.VarInt;

@AllArgsConstructor
@Getter
public class TransferPacket implements Packet {
	private String host;
	private int port;
	
	@Override
	public void decode(ByteBuf in) throws Exception {
		host = Utf8String.read(in, 255);
		port = VarInt.read(in);
	}

	@Override
	public void encode(ByteBuf out) throws Exception {
		Utf8String.write(out, host, 255);
		VarInt.write(out, port);
	}
}