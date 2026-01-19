package me.tom.common.network.protocol;

import lombok.AllArgsConstructor;
import lombok.Getter;
import me.tom.common.network.protocol.packet.PacketRegistry;

@AllArgsConstructor
@Getter
public enum ProtocolState {
    HANDSHAKE(PacketRegistry.HANDSHAKE),
    STATUS(PacketRegistry.STATUS),
    LOGIN(PacketRegistry.LOGIN),
    TRANSFER(PacketRegistry.TRANSFER),
    CONFIGURATION(PacketRegistry.CONFIGURATION),
    PLAY(PacketRegistry.PLAY);

    private final PacketRegistry registry;
}