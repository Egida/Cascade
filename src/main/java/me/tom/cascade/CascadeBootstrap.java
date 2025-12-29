package me.tom.cascade;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.UUID;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import me.tom.cascade.config.ProxyConfig;
import me.tom.cascade.config.ProxyConfigLoader;
import me.tom.cascade.net.CascadeProxy;
import me.tom.cascade.util.MojangUUIDAdapter;

public class CascadeBootstrap 
{
	public static final ProxyConfig CONFIG = ProxyConfigLoader.load();
	
	private static final CascadeProxy PROXY = new CascadeProxy(CONFIG.getProxyPort());
	
	public static final Gson GSON = new GsonBuilder()
    	    .registerTypeAdapter(UUID.class, new MojangUUIDAdapter())
    	    .create();
	

	public static final String PROXY_STATUS_JSON;

	static {
	    try {
	        PROXY_STATUS_JSON = new String(Files.readAllBytes(Paths.get("status.json")));
	    } catch (IOException e) {
	        throw new RuntimeException("Failed to load status.json", e);
	    }
	}
	
	public static final String INVALID_TOKEN_JSON = "";
			
	
    public static void main( String[] args ) throws InterruptedException
    {
    	PROXY.start();
    }
}
