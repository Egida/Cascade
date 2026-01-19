package me.tom.common.network.protocol.packet.packets.serverbound.config;

import io.netty.buffer.ByteBuf;
import lombok.AllArgsConstructor;
import lombok.Getter;
import me.tom.common.network.protocol.packet.Packet;
import me.tom.common.network.protocol.packet.types.Utf8String;

@AllArgsConstructor
@Getter
public class PluginMessage implements Packet {
	private String identifier;
	private byte[] data;
	
	@Override
	public void decode(ByteBuf in) throws Exception {
		Utf8String.read(in, 32767);
		data = new byte[in.readableBytes()];
		in.readBytes(data);
	}
	
	@Override
	public void encode(ByteBuf out) throws Exception {
		Utf8String.write(out, identifier, 32767);
		out.writeBytes(data);
	}
}
