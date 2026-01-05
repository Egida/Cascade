package me.tom.cascade.network.protocol.packet;

import static me.tom.cascade.network.protocol.ProtocolVersion.MINECRAFT_1_20_5;
import static me.tom.cascade.network.protocol.ProtocolVersion.MINECRAFT_1_20_6;
import static me.tom.cascade.network.protocol.ProtocolVersion.MINECRAFT_1_21;
import static me.tom.cascade.network.protocol.ProtocolVersion.UNKNOWN;
import static me.tom.cascade.network.protocol.ProtocolVersion.allVersions;
import static me.tom.cascade.network.protocol.ProtocolVersion.allVersionsExcept;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;

import me.tom.cascade.network.NetworkSide;
import me.tom.cascade.network.protocol.ProtocolVersion;
import me.tom.cascade.network.protocol.packet.packets.clientbound.CookieRequestPacket;
import me.tom.cascade.network.protocol.packet.packets.clientbound.DisconnectPacket;
import me.tom.cascade.network.protocol.packet.packets.clientbound.EncryptionResponsePacket;
import me.tom.cascade.network.protocol.packet.packets.clientbound.LoginSuccessPacket;
import me.tom.cascade.network.protocol.packet.packets.clientbound.OldLoginSuccessPacket;
import me.tom.cascade.network.protocol.packet.packets.clientbound.PongResponsePacket;
import me.tom.cascade.network.protocol.packet.packets.clientbound.StatusResponsePacket;
import me.tom.cascade.network.protocol.packet.packets.clientbound.StoreCookiePacket;
import me.tom.cascade.network.protocol.packet.packets.clientbound.TransferPacket;
import me.tom.cascade.network.protocol.packet.packets.serverbound.CookieResponsePacket;
import me.tom.cascade.network.protocol.packet.packets.serverbound.EncryptionRequestPacket;
import me.tom.cascade.network.protocol.packet.packets.serverbound.HandshakePacket;
import me.tom.cascade.network.protocol.packet.packets.serverbound.LoginAcknowledgedPacket;
import me.tom.cascade.network.protocol.packet.packets.serverbound.LoginStartPacket;
import me.tom.cascade.network.protocol.packet.packets.serverbound.PingRequestPacket;
import me.tom.cascade.network.protocol.packet.packets.serverbound.StatusRequestPacket;

public enum PacketRegistry {

    HANDSHAKE {
        {
            registerServerbound(0x00, HandshakePacket.class, allVersions());
        }
    },

    STATUS {
        {
            registerClientbound(0x00, StatusResponsePacket.class, allVersionsExcept(UNKNOWN));
            registerClientbound(0x01, PongResponsePacket.class, allVersionsExcept(UNKNOWN));

            registerServerbound(0x00, StatusRequestPacket.class, allVersionsExcept(UNKNOWN));
            registerServerbound(0x01, PingRequestPacket.class, allVersionsExcept(UNKNOWN));
        }
    },

    LOGIN {
        {
            registerClientbound(0x00, DisconnectPacket.class, allVersionsExcept(UNKNOWN));
            registerClientbound(0x01, EncryptionRequestPacket.class, allVersionsExcept(UNKNOWN));
            
            registerClientbound(0x02, LoginSuccessPacket.class, allVersionsExcept(UNKNOWN, MINECRAFT_1_21, MINECRAFT_1_20_6, MINECRAFT_1_20_5));
            registerClientbound(0x02, OldLoginSuccessPacket.class, MINECRAFT_1_21, MINECRAFT_1_20_6, MINECRAFT_1_20_5);
            
            registerClientbound(0x05, CookieRequestPacket.class, allVersionsExcept(UNKNOWN));

            registerServerbound(0x00, LoginStartPacket.class, allVersionsExcept(UNKNOWN));
            registerServerbound(0x01, EncryptionResponsePacket.class, allVersionsExcept(UNKNOWN));
            registerServerbound(0x03, LoginAcknowledgedPacket.class, allVersionsExcept(UNKNOWN));
            registerServerbound(0x04, CookieResponsePacket.class, allVersionsExcept(UNKNOWN));
        }
    },

    TRANSFER,

    CONFIGURATION {
        {
            registerClientbound(0x0A, StoreCookiePacket.class, ProtocolVersion.values());
            registerClientbound(0x0B, TransferPacket.class, ProtocolVersion.values());
        }
    };

    private final Map<ProtocolVersion, Map<Integer, Class<? extends Packet>>> clientbound =
            new EnumMap<>(ProtocolVersion.class);

    private final Map<ProtocolVersion, Map<Integer, Class<? extends Packet>>> serverbound =
            new EnumMap<>(ProtocolVersion.class);

    private final Map<ProtocolVersion, Map<Class<? extends Packet>, Integer>> reverseClientbound =
            new EnumMap<>(ProtocolVersion.class);

    private final Map<ProtocolVersion, Map<Class<? extends Packet>, Integer>> reverseServerbound =
            new EnumMap<>(ProtocolVersion.class);

    protected void registerClientbound(int id, Class<? extends Packet> clazz, ProtocolVersion... versions) {
        for (ProtocolVersion version : versions) {
            clientbound.computeIfAbsent(version, v -> new HashMap<>()).put(id, clazz);
            reverseClientbound.computeIfAbsent(version, v -> new HashMap<>()).put(clazz, id);
        }
    }

    protected void registerServerbound(int id, Class<? extends Packet> clazz, ProtocolVersion... versions) {
        for (ProtocolVersion version : versions) {
            serverbound.computeIfAbsent(version, v -> new HashMap<>()).put(id, clazz);
            reverseServerbound.computeIfAbsent(version, v -> new HashMap<>()).put(clazz, id);
        }
    }

    public Class<? extends Packet> getPacket(ProtocolVersion version, NetworkSide dir, int id) {
        Map<Integer, Class<? extends Packet>> map =
                (dir == NetworkSide.CLIENTBOUND ? clientbound.get(version) : serverbound.get(version));
        if (map == null) return null;
        return map.get(id);
    }

    public int getPacketId(ProtocolVersion version, NetworkSide dir, Class<? extends Packet> clazz) {
        Map<Class<? extends Packet>, Integer> map =
                (dir == NetworkSide.CLIENTBOUND ? reverseClientbound.get(version) : reverseServerbound.get(version));
        if (map == null) return -1;
        return map.getOrDefault(clazz, -1);
    }
}