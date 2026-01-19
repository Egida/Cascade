package me.tom.common.network.protocol;

import io.netty.util.AttributeKey;

public class ProtocolAttributes {
    public static final AttributeKey<ProtocolState> STATE =
            AttributeKey.valueOf("state");
    
    public static final AttributeKey<ProtocolVersion> PROTOCOL_VERSION =
            AttributeKey.valueOf("version");

    public static final AttributeKey<String> HOSTNAME =
            AttributeKey.valueOf("hostname");
    
    public static final AttributeKey<Integer> PORT =
            AttributeKey.valueOf("port");
    
    public static final AttributeKey<String> USERNAME =
            AttributeKey.valueOf("username");
}