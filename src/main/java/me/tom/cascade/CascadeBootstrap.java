package me.tom.cascade;

import java.security.Key;
import java.util.UUID;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import io.jsonwebtoken.security.Keys;
import me.tom.cascade.command.Args;
import me.tom.cascade.config.ProxyConfig;
import me.tom.cascade.config.ProxyConfigLoader;
import me.tom.cascade.network.CascadeProxy;

public class CascadeBootstrap 
{
	public static final ProxyConfig CONFIG = ProxyConfigLoader.load();
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
			
	
    public static void main( String[] args ) throws InterruptedException
    {
    	CONFIG.setProxyPort(Short.parseShort(Args.get(args, "proxyPort")));
    	CONFIG.setTargetHost(Args.get(args, "targetHost"));
    	CONFIG.setTargetPort(Short.parseShort(Args.get(args, "targetPort")));
    	CONFIG.setJwtSecret(Args.get(args, "jwtSecret"));
    	
    	JWT_KEY = Keys.hmacShaKeyFor(CONFIG.getJwtSecret().getBytes());
    	
    	PROXY = new CascadeProxy(CONFIG.getProxyPort());
    	PROXY.start();
    }
}
