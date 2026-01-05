package me.tom.cascade.net;

import java.util.HashMap;
import java.util.Map;

public enum ProtocolVersion {
	UNKNOWN(-1),
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
	
	public static ProtocolVersion getFromVersionNumber(int versionNumber) {
		return ID_TO_CONTANT.getOrDefault(versionNumber, UNKNOWN);
	}
	
	private ProtocolVersion(int versionNumber) {
		this.versionNumber = versionNumber;
	}
	
	public boolean isBefore(ProtocolVersion protocolVersion) {
		return this.versionNumber < protocolVersion.versionNumber;
	}
	
	public boolean isAfter(ProtocolVersion protocolVersion) {
		return this.versionNumber > protocolVersion.versionNumber;
	}
	
	public static ProtocolVersion[] allVersionsExcept(ProtocolVersion... excluded) {
	    ProtocolVersion[] all = ProtocolVersion.values();
	    if (excluded == null || excluded.length == 0) return all;

	    boolean[] skip = new boolean[all.length];
	    for (ProtocolVersion ex : excluded) {
	        skip[ex.ordinal()] = true;
	    }

	    int count = 0;
	    for (int i = 0; i < all.length; i++) {
	        if (!skip[i]) count++;
	    }

	    ProtocolVersion[] result = new ProtocolVersion[count];
	    int idx = 0;
	    for (int i = 0; i < all.length; i++) {
	        if (!skip[i]) result[idx++] = all[i];
	    }

	    return result;
	}
	
	public static ProtocolVersion[] allVersions() {
		return values();
	}
	
	public int getVersionNumber() {
		return versionNumber;
	}
	
	public boolean isVersionSupported() {
		return this.versionNumber >= MINIMUM_VERSION.versionNumber && this.versionNumber <= MAXIMUM_VERSION.versionNumber;
	}
}
