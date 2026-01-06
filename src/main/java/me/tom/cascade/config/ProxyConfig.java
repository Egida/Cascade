package me.tom.cascade.config;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class ProxyConfig {
	private short proxyPort;
	private String targetHost;
	private short targetPort;
	
	private int concurrentIpConnectionLimit;
	private int maxIpConnectionsPerSecond;
	private int maxSubnetConnectionsPerSecond;
	
	private int failedLoginPunishmentSeconds;
	private int failedAuthenticationPunishmentSeconds;
	private int failedProtocolPunishmentSeconds;
	
	private String jwtSecret;
	private boolean authVerification;
}