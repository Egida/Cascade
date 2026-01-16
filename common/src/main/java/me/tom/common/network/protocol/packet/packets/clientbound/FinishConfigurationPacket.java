package me.tom.common.network.protocol.packet.packets.clientbound;

import io.netty.buffer.ByteBuf;
import me.tom.common.network.protocol.packet.Packet;

public class FinishConfigurationPacket implements Packet {
	@Override
	public void decode(ByteBuf in) throws Exception {}

	@Override
	public void encode(ByteBuf out) throws Exception {}
}
