package me.tom.cascade.config;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Properties;

public class ProxyConfigLoader {

    private static final File PROPERTIES_FILE = new File("proxy.properties");

    public static ProxyConfig load() {
        Properties props = new Properties();

        try {
            if (PROPERTIES_FILE.exists()) {
                try (Reader reader = new InputStreamReader(
                        new FileInputStream(PROPERTIES_FILE), StandardCharsets.UTF_8)) {
                    props.load(reader);
                }
            } else {
                try (InputStream in = ProxyConfigLoader.class.getResourceAsStream("/proxy.properties")) {
                    if (in == null) {
                        throw new RuntimeException("Default proxy.properties missing from JAR");
                    }
                    props.load(new InputStreamReader(in, StandardCharsets.UTF_8));
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to load config", e);
        }

        ProxyConfig config = new ProxyConfig();
        config.setProxyVersionProtocol(Integer.parseInt(props.getProperty("proxy_version_protocol")));
        config.setProxyVersionName(props.getProperty("proxy_version_name"));
        config.setProxyDescription(props.getProperty("proxy_description"));
        config.setAuthVerification(Boolean.parseBoolean(props.getProperty("auth_verification")));

        return config;
    }
}