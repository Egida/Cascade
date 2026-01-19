package me.tom.common.network.protocol.packet.packets.serverbound.login;

import io.netty.buffer.ByteBuf;
import me.tom.common.network.protocol.packet.Packet;

public class LoginAckPacket implements Packet {

    @Override
    public void decode(ByteBuf in) {}

    @Override
    public void encode(ByteBuf out) {}
}