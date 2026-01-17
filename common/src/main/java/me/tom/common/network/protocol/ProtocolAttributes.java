package me.tom.common.network.protocol;

import java.util.UUID;

import io.netty.util.AttributeKey;

public class ProtocolAttributes {
    public static final AttributeKey<ProtocolState> STATE =
            AttributeKey.valueOf("state");
    
    public static final AttributeKey<ProtocolVersion> PROTOCOL_VERSION =
            AttributeKey.valueOf("version");
    
    public static final AttributeKey<UUID> UUID =
            AttributeKey.valueOf("uuid");
}