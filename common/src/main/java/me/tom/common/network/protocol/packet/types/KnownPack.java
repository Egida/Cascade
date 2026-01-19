package me.tom.common.network.protocol.packet.types;

import io.netty.buffer.ByteBuf;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@Getter
@NoArgsConstructor
public class KnownPack {
	private String namespace;
	private String id;
	private String version;
	
	public void encode(ByteBuf out) {
		Utf8String.write(out, namespace, 32767);
		Utf8String.write(out, id, 32767);
		Utf8String.write(out, version, 32767);
	}
	
	public void decode(ByteBuf in) {
		namespace = Utf8String.read(in, 32767);
		id = Utf8String.read(in, 32767);
		version = Utf8String.read(in, 32767);
	}
}
