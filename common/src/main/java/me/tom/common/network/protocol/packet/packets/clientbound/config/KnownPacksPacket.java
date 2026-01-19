package me.tom.common.network.protocol.packet.packets.clientbound.config;

import io.netty.buffer.ByteBuf;
import me.tom.common.network.protocol.packet.Packet;
import me.tom.common.network.protocol.packet.types.KnownPack;
import me.tom.common.network.protocol.packet.types.VarInt;

public class KnownPacksPacket implements Packet {
	private KnownPack[] knownPacks;
	
	public KnownPacksPacket(KnownPack... knownPacks) {
		this.knownPacks = knownPacks;
	}
	
	@Override
	public void decode(ByteBuf in) throws Exception {
		int count = VarInt.read(in);
		KnownPack[] knownPacks = new KnownPack[count];
		
		for(int i = 0; i < count; i++) {
			KnownPack pack = new KnownPack();
			pack.decode(in);
			knownPacks[i] = pack;
		}
		
		this.knownPacks = knownPacks;
	}

	@Override
	public void encode(ByteBuf out) throws Exception {
		VarInt.write(out, knownPacks.length);
		
		for(KnownPack pack : knownPacks) {
			pack.encode(out);
		}
	}
}