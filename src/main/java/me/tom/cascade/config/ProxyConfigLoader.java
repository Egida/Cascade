package me.tom.cascade.config;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.util.Properties;

public class ProxyConfigLoader {

    private static final File PROPERTIES_FILE = new File("proxy.properties");

    public static ProxyConfig load() throws FileNotFoundException, IOException {
        Properties props = new Properties();

        if (!PROPERTIES_FILE.exists()) {
        	createDefaultFile();
        } else {
            try (Reader reader = new InputStreamReader(
                    new FileInputStream(PROPERTIES_FILE), StandardCharsets.UTF_8)) {
                props.load(reader);
            }
        }

        try {
	        ProxyConfig config = new ProxyConfig();
	        config.setProxyPort(Short.parseShort(props.getProperty("proxy_port")));
	        config.setTargetHost(props.getProperty("target_host"));
	        config.setTargetPort(Short.parseShort(props.getProperty("target_port")));
	        config.setConcurrentIpConnectionLimit(Integer.parseInt(props.getProperty("concurrent_ip_connection_limit")));
	        config.setMaxIpConnectionsPerSecond(Integer.parseInt(props.getProperty("max_ip_connections_per_second")));
	        config.setMaxSubnetConnectionsPerSecond(Integer.parseInt(props.getProperty("max_subnet_connections_per_second")));
	        config.setFailedProtocolPunishmentSeconds(Integer.parseInt(props.getProperty("failed_protocol_punishment_seconds")));
	        config.setFailedLoginPunishmentSeconds(Integer.parseInt(props.getProperty("failed_login_punishment_seconds")));
	        config.setFailedAuthenticationPunishmentSeconds(Integer.parseInt(props.getProperty("failed_authentication_punishment_seconds")));
	        config.setJwtSecret(props.getProperty("jwt_secret"));
	        config.setAuthVerification(Boolean.parseBoolean(props.getProperty("auth_verification")));
	        
	        return config;
        } catch (Exception e) {
        	createDefaultFile();

            throw new RuntimeException("Failed to load config", e);
		}
    }
    
    private static void createDefaultFile() throws FileNotFoundException, IOException {
        Properties props = new Properties();
        
    	props.setProperty("proxy_port", "13371");
        props.setProperty("target_host", "localhost");
        props.setProperty("target_port", "25565");
        props.setProperty("concurrent_ip_connection_limit", "2");
        props.setProperty("max_ip_connections_per_second", "3");
        props.setProperty("max_subnet_connections_per_second", "10");
        props.setProperty("failed_protocol_punishment_seconds", "30");
        props.setProperty("failed_login_punishment_seconds", "10");
        props.setProperty("failed_authentication_punishment_seconds", "86400");
        props.setProperty("jwt_secret", "CHANGE_ME_IMMEDIATELY_THIS_IS_NOT_SAFE");
        props.setProperty("auth_verification", "true");

        try (Writer writer = new OutputStreamWriter(
                new FileOutputStream(PROPERTIES_FILE), StandardCharsets.UTF_8)) {
            props.store(writer, null);
        }
    }
}