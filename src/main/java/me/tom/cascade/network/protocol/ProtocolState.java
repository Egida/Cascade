package me.tom.cascade.network.protocol;

import io.netty.channel.SimpleChannelInboundHandler;
import me.tom.cascade.network.handlers.minecraft.ConfigurationHandler;
import me.tom.cascade.network.handlers.minecraft.HandshakeHandler;
import me.tom.cascade.network.handlers.minecraft.LoginHandler;
import me.tom.cascade.network.handlers.minecraft.StatusHandler;
import me.tom.cascade.network.handlers.minecraft.TransferHandler;
import me.tom.cascade.network.protocol.packet.Packet;
import me.tom.cascade.network.protocol.packet.PacketRegistry;

public enum ProtocolState {
    HANDSHAKE(PacketRegistry.HANDSHAKE, HandshakeHandler.class),
    STATUS(PacketRegistry.STATUS, StatusHandler.class),
    LOGIN(PacketRegistry.LOGIN, LoginHandler.class),
    TRANSFER(PacketRegistry.TRANSFER, TransferHandler.class),
    CONFIGURATION(PacketRegistry.CONFIGURATION, ConfigurationHandler.class);

    private final PacketRegistry registry;
    private final Class<? extends SimpleChannelInboundHandler<Packet>> handler;

    ProtocolState(PacketRegistry registry, Class<? extends SimpleChannelInboundHandler<Packet>> handler) {
        this.registry = registry;
        this.handler = handler;
    }

    public PacketRegistry getRegistry() {
        return registry;
    }

	public Class<? extends SimpleChannelInboundHandler<Packet>> getHandler() {
		return handler;
	}
	
	public static boolean isValidProtocolState(int state) {
		return state >= 0 && state < values().length;
	}
}