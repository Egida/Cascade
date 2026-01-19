package me.tom.common.network.protocol.packet;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;

import lombok.var;
import me.tom.common.network.NetworkSide;
import me.tom.common.network.protocol.ProtocolVersion;
import me.tom.common.network.protocol.packet.packets.clientbound.config.FinishConfigPacket;
import me.tom.common.network.protocol.packet.packets.clientbound.config.RegistryDataPacket;
import me.tom.common.network.protocol.packet.packets.clientbound.login.LoginPluginRequestPacket;
import me.tom.common.network.protocol.packet.packets.clientbound.login.LoginSuccessPacket;
import me.tom.common.network.protocol.packet.packets.clientbound.play.KeepAlivePacket;
import me.tom.common.network.protocol.packet.packets.clientbound.play.LoginPacket;
import me.tom.common.network.protocol.packet.packets.clientbound.play.TransferPacket;
import me.tom.common.network.protocol.packet.packets.serverbound.config.AckFinishConfigPacket;
import me.tom.common.network.protocol.packet.packets.serverbound.config.PluginMessage;
import me.tom.common.network.protocol.packet.packets.serverbound.handshake.HandshakePacket;
import me.tom.common.network.protocol.packet.packets.serverbound.login.LoginAckPacket;
import me.tom.common.network.protocol.packet.packets.serverbound.login.LoginPluginResponsePacket;
import me.tom.common.network.protocol.packet.packets.serverbound.login.LoginStartPacket;

public enum PacketRegistry {

    HANDSHAKE {
    	{
    		register(NetworkSide.SERVERBOUND, 0x00, HandshakePacket.class, ProtocolVersion.allVersions());
    	}
    },
    
    STATUS {
    	
    },
    
    LOGIN {
    	{
    		register(NetworkSide.SERVERBOUND, 0x00, LoginStartPacket.class, ProtocolVersion.allVersions());
    		register(NetworkSide.SERVERBOUND, 0x03, LoginAckPacket.class, ProtocolVersion.allVersions());
    		register(NetworkSide.SERVERBOUND, 0x02, LoginPluginResponsePacket.class, ProtocolVersion.allVersions());
    		
    		register(NetworkSide.CLIENTBOUND, 0x02, LoginSuccessPacket.class, ProtocolVersion.allVersions());
    		register(NetworkSide.CLIENTBOUND, 0x04, LoginPluginRequestPacket.class, ProtocolVersion.allVersions());
    	}
    },
    
    TRANSFER {
    	
    },
    
    CONFIGURATION {
    	{
    		register(NetworkSide.CLIENTBOUND, 0x03, FinishConfigPacket.class, ProtocolVersion.allVersions());
    		register(NetworkSide.CLIENTBOUND, 0x07, RegistryDataPacket.class, ProtocolVersion.allVersions());
    		register(NetworkSide.CLIENTBOUND, 0x0B, TransferPacket.class, ProtocolVersion.allVersions());
    		register(NetworkSide.CLIENTBOUND, 0x0E, me.tom.common.network.protocol.packet.packets.clientbound.config.KnownPacksPacket.class, ProtocolVersion.allVersions());
    		register(NetworkSide.CLIENTBOUND, 0x01, PluginMessage.class, ProtocolVersion.allVersions());
    		
    		register(NetworkSide.SERVERBOUND, 0x03, AckFinishConfigPacket.class, ProtocolVersion.allVersions());
    		register(NetworkSide.SERVERBOUND, 0x07, me.tom.common.network.protocol.packet.packets.serverbound.config.KnownPacksPacket.class, ProtocolVersion.allVersions());
    	}
    },
    
    PLAY {
    	{
    		register(NetworkSide.CLIENTBOUND, 0x2B, KeepAlivePacket.class, ProtocolVersion.allVersions());
    		register(NetworkSide.CLIENTBOUND, 0x30, LoginPacket.class, ProtocolVersion.allVersions());
    	}
    };

    private static class DirectionRegistry {
        final Map<Integer, Class<? extends Packet>> idToClass = new HashMap<>();
        final Map<Class<? extends Packet>, Integer> classToId = new HashMap<>();
    }

    private final Map<ProtocolVersion, Map<NetworkSide, DirectionRegistry>> registry =
            new EnumMap<>(ProtocolVersion.class);

    private Map<NetworkSide, DirectionRegistry> getOrCreate(ProtocolVersion version) {
        return registry.computeIfAbsent(version, v -> new EnumMap<>(NetworkSide.class));
    }

    private DirectionRegistry getOrCreate(ProtocolVersion version, NetworkSide side) {
        return getOrCreate(version).computeIfAbsent(side, s -> new DirectionRegistry());
    }

    protected void register(NetworkSide side, int id, Class<? extends Packet> clazz,
                            ProtocolVersion... versions) {

        for (ProtocolVersion version : versions) {
            DirectionRegistry dir = getOrCreate(version, side);
            dir.idToClass.put(id, clazz);
            dir.classToId.put(clazz, id);
        }
    }

    protected void registerClientbound(int id, Class<? extends Packet> clazz,
                                       ProtocolVersion... versions) {
        register(NetworkSide.CLIENTBOUND, id, clazz, versions);
    }

    protected void registerServerbound(int id, Class<? extends Packet> clazz,
                                       ProtocolVersion... versions) {
        register(NetworkSide.SERVERBOUND, id, clazz, versions);
    }

    public Class<? extends Packet> getPacket(ProtocolVersion version, NetworkSide side, int id) {
        var versionMap = registry.get(version);
        if (versionMap == null) return null;

        var dir = versionMap.get(side);
        if (dir == null) return null;

        return dir.idToClass.get(id);
    }

    public int getPacketId(ProtocolVersion version, NetworkSide side, Class<? extends Packet> clazz) {
        var versionMap = registry.get(version);
        if (versionMap == null) return -1;

        var dir = versionMap.get(side);
        if (dir == null) return -1;

        return dir.classToId.getOrDefault(clazz, -1);
    }
}