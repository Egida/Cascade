package me.tom.cascade.protocol;

import io.netty.util.AttributeKey;
import me.tom.cascade.net.ProtocolVersion;

public class ProtocolAttributes {
    public static final AttributeKey<ConnectionState> STATE =
            AttributeKey.valueOf("cascade-connection-state");
    
    public static final AttributeKey<ProtocolVersion> PROTOCOL_VERSION =
            AttributeKey.valueOf("cascade-protocol-version");
    
    public static final AttributeKey<Boolean> TRANSFER =
            AttributeKey.valueOf("cascade-transfer");
}