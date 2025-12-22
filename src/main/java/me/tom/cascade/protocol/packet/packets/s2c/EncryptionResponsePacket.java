package me.tom.cascade.protocol.packet.packets.s2c;

import io.netty.buffer.ByteBuf;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import me.tom.cascade.net.types.ByteArray;
import me.tom.cascade.protocol.packet.Packet;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class EncryptionResponsePacket implements Packet {
	
	private byte[] sharedSecret;
	private byte[] verifyToken;

    @Override
    public void decode(ByteBuf in) {
    	sharedSecret = ByteArray.read(in);
    	verifyToken = ByteArray.read(in);
    }

    @Override
    public void encode(ByteBuf out) {
    	ByteArray.write(out, sharedSecret);
    	ByteArray.write(out, verifyToken);
    }
}