package me.tom.cascade.protocol.packet.packets.clientbound;

import io.netty.buffer.ByteBuf;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import me.tom.cascade.auth.GameProfile;
import me.tom.cascade.protocol.packet.Packet;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class OldLoginSuccessPacket implements Packet {
	private GameProfile profile;
	private boolean strictErrorHandling;

    @Override
    public void decode(ByteBuf in) {
    	profile = GameProfile.read(in);
    	strictErrorHandling = in.readBoolean();
    }

    @Override
    public void encode(ByteBuf out) {
    	profile.write(out);
    	out.writeBoolean(strictErrorHandling);
    }
}