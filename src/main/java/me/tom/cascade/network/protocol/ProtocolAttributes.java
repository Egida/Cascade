package me.tom.cascade.network.protocol;

import io.netty.util.AttributeKey;

public class ProtocolAttributes {
    public static final AttributeKey<ProtocolState> STATE =
            AttributeKey.valueOf("cascade-connection-state");
    
    public static final AttributeKey<ProtocolVersion> PROTOCOL_VERSION =
            AttributeKey.valueOf("cascade-protocol-version");
    
    public static final AttributeKey<Boolean> TRANSFER =
            AttributeKey.valueOf("cascade-transfer");
}