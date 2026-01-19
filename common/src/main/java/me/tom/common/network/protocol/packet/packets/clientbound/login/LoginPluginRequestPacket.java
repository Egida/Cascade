package me.tom.common.network.protocol.packet.packets.clientbound.login;

import io.netty.buffer.ByteBuf;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import me.tom.common.network.protocol.packet.Packet;
import me.tom.common.network.protocol.packet.types.Utf8String;
import me.tom.common.network.protocol.packet.types.VarInt;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class LoginPluginRequestPacket implements Packet {
	private int messageId;
	private String channel;
	private byte[] data;
	
	@Override
	public void decode(ByteBuf in) throws Exception {
	    messageId = VarInt.read(in);
	    channel = Utf8String.read(in, 32767);

	    int length = in.readableBytes();
	    data = new byte[length];
	    in.readBytes(data);
	}
	
	@Override
	public void encode(ByteBuf out) throws Exception {
		VarInt.write(out, messageId);
		Utf8String.write(out, channel, 32767);
		out.writeBytes(data);
	}
}
