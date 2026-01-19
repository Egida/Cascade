package me.tom.common.network.protocol.packet.packets.clientbound.play;

import io.netty.buffer.ByteBuf;
import lombok.AllArgsConstructor;
import lombok.Getter;
import me.tom.common.network.protocol.packet.Packet;
import me.tom.common.network.protocol.packet.types.Utf8String;
import me.tom.common.network.protocol.packet.types.VarInt;

@AllArgsConstructor
@Getter
public class LoginPacket implements Packet {

	private int entityId;
	private boolean hardcore;
	private String[] dimensionNames;
	private int maxPlayers;
	private int viewDistance;
	private int simulationDistance;
	private boolean reducedDebugInfo;
	private boolean enableRespawnScreen;
	private boolean doLimitedCrafting;
	private int dimensionType;
	private String dimensionName;
	private long hashedSeed;
	private byte gamemode;
	private byte previousGamemode;
	private boolean debug;
	private boolean flat;
	private int portalCooldown;
	private int seaLevel;
	private boolean enforcesSecureChat;
	
	@Override
	public void decode(ByteBuf in) throws Exception {
		
	}

	@Override
	public void encode(ByteBuf out) throws Exception {
		out.writeInt(entityId);
		out.writeBoolean(hardcore);
		
		VarInt.write(out, dimensionNames.length);
		
		for(String dimension : dimensionNames) {
			Utf8String.write(out, dimension, 32767);
		}
		
		VarInt.write(out, maxPlayers);
		VarInt.write(out, viewDistance);
		VarInt.write(out, simulationDistance);
		out.writeBoolean(reducedDebugInfo);
		out.writeBoolean(enableRespawnScreen);
		out.writeBoolean(doLimitedCrafting);
		VarInt.write(out, dimensionType);
		Utf8String.write(out, dimensionName, 32767);
		out.writeLong(hashedSeed);
		out.writeByte(gamemode);
		out.writeByte(previousGamemode);
		out.writeBoolean(debug);
		out.writeBoolean(flat);
		out.writeBoolean(false);
		VarInt.write(out, portalCooldown);
		VarInt.write(out, seaLevel);
		out.writeBoolean(enforcesSecureChat);
		
	}
}
