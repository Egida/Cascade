package me.tom.cascade.net;

import java.util.HashMap;
import java.util.Map;

public enum ProtocolVersion {
	MINECRAFT_1_20_5(766),
	MINECRAFT_1_20_6(766),
	MINECRAFT_1_21(767),
	MINECRAFT_1_21_1(767),
	MINECRAFT_1_21_2(768),
	MINECRAFT_1_21_3(768),
	MINECRAFT_1_21_4(769),
	MINECRAFT_1_21_5(770),
	MINECRAFT_1_21_6(771),
	MINECRAFT_1_21_7(772),
	MINECRAFT_1_21_8(772),
	MINECRAFT_1_21_9(773),
	MINECRAFT_1_21_10(773),
	MINECRAFT_1_21_11(774);
	
	private static final ProtocolVersion MINIMUM_VERSION = MINECRAFT_1_20_5;
	private static final ProtocolVersion MAXIMUM_VERSION = MINECRAFT_1_21_11;
	
	private static final Map<Integer, ProtocolVersion> ID_TO_CONTANT = new HashMap<Integer, ProtocolVersion>();
	
	private int versionNumber;
	
	static {
		for(ProtocolVersion protocolVersion : values()) {
			ID_TO_CONTANT.putIfAbsent(protocolVersion.versionNumber, protocolVersion);
		}
	}
	
	private ProtocolVersion(int versionNumber) {
		this.versionNumber = versionNumber;
	}
	
	public int getVersionNumber() {
		return versionNumber;
	}
	
	public boolean isVersionSupported() {
		return this.versionNumber >= MINIMUM_VERSION.versionNumber && this.versionNumber <= MAXIMUM_VERSION.versionNumber;
	}
}
