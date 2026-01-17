package me.tom.common.network.protocol.packet.packets.clientbound;

import io.netty.buffer.ByteBuf;
import lombok.AllArgsConstructor;
import lombok.Getter;
import me.tom.common.network.protocol.packet.Packet;
import me.tom.common.network.protocol.packet.types.Utf8String;
import me.tom.common.network.protocol.packet.types.VarInt;

@AllArgsConstructor
@Getter
public class PluginMessage implements Packet {
	private String identifier;
	private byte[] data;
	
	@Override
	public void decode(ByteBuf in) throws Exception {
		Utf8String.read(in, 32767);
		data = new byte[VarInt.read(in)];
		in.readBytes(data);
	}
	
	@Override
	public void encode(ByteBuf out) throws Exception {
		Utf8String.write(out, identifier, 32767);
		VarInt.write(out, data.length);
		out.writeBytes(data);
	}
}
