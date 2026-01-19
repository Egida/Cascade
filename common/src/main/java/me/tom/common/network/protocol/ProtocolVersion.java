package me.tom.common.network.protocol;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum ProtocolVersion {
    UNKNOWN(-1, "Unknown"),

    MINECRAFT_1_21_11(774, "Minecraft 1.21.11");

    private final int protocol;
    private final String name;

    public static ProtocolVersion[] allVersions() {
        return values();
    }
}