package me.tom.cascade;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.Key;
import java.util.UUID;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import io.jsonwebtoken.security.Keys;
import me.tom.cascade.config.ProxyConfig;
import me.tom.cascade.config.ProxyConfigLoader;
import me.tom.cascade.network.CascadeProxy;
import me.tom.cascade.network.protocol.ProtocolVersion;

public class CascadeBootstrap 
{
	public static ProxyConfig CONFIG;
	public static Key JWT_KEY;
	
	private static CascadeProxy PROXY;
	
	public static final Gson GSON = new GsonBuilder()
    	    .registerTypeAdapter(UUID.class, new MojangUUIDAdapter())
    	    .create();
	
	public static final String PROXY_STATUS_JSON;
	public static final String INVALID_TOKEN_JSON;

	static {
	    try {
	        PROXY_STATUS_JSON = new String(
	            CascadeBootstrap.class.getResourceAsStream("/status.json").readAllBytes()
	        );
	        INVALID_TOKEN_JSON = new String(
	            CascadeBootstrap.class.getResourceAsStream("/invalid_token.json").readAllBytes()
	        );
	    } catch (Exception e) {
	        throw new RuntimeException("Failed to load bundled JSON files", e);
	    }
	}
			
	
    public static void main( String[] args ) throws InterruptedException, FileNotFoundException, IOException
    {
    	CONFIG = ProxyConfigLoader.load();
    	JWT_KEY = Keys.hmacShaKeyFor(CONFIG.getJwtSecret().getBytes());
    	
    	ProtocolVersion.MINIMUM_VERSION = ProtocolVersion.getFromVersionNumber(CONFIG.getProxyVersionProtocolMin());
    	ProtocolVersion.MAXIMUM_VERSION = ProtocolVersion.getFromVersionNumber(CONFIG.getProxyVersionProtocolMax());
    	
    	PROXY = new CascadeProxy(CONFIG.getProxyPort());
    	PROXY.start();
    }
}