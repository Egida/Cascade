package me.tom.common.network.protocol;

import io.netty.util.AttributeKey;

public class ProtocolAttributes {
    public static final AttributeKey<ProtocolState> STATE =
            AttributeKey.valueOf("state");
    
    public static final AttributeKey<ProtocolVersion> PROTOCOL_VERSION =
            AttributeKey.valueOf("version");
}