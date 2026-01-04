package me.tom.cascade.protocol;

import io.netty.channel.SimpleChannelInboundHandler;
import me.tom.cascade.net.handlers.ConfigurationHandler;
import me.tom.cascade.net.handlers.HandshakeHandler;
import me.tom.cascade.net.handlers.LoginHandler;
import me.tom.cascade.net.handlers.StatusHandler;
import me.tom.cascade.net.handlers.TransferHandler;
import me.tom.cascade.protocol.packet.Packet;
import me.tom.cascade.protocol.packet.PacketRegistry;

public enum ConnectionState {
    HANDSHAKE(PacketRegistry.HANDSHAKE, HandshakeHandler.class),
    STATUS(PacketRegistry.STATUS, StatusHandler.class),
    LOGIN(PacketRegistry.LOGIN, LoginHandler.class),
    TRANSFER(PacketRegistry.TRANSFER, TransferHandler.class),
    CONFIGURATION(PacketRegistry.CONFIGURATION, ConfigurationHandler.class);

    private final PacketRegistry registry;
    private final Class<? extends SimpleChannelInboundHandler<Packet>> handler;

    ConnectionState(PacketRegistry registry, Class<? extends SimpleChannelInboundHandler<Packet>> handler) {
        this.registry = registry;
        this.handler = handler;
    }

    public PacketRegistry getRegistry() {
        return registry;
    }

	public Class<? extends SimpleChannelInboundHandler<Packet>> getHandler() {
		return handler;
	}
}