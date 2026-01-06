package me.tom.cascade;

import static me.tom.cascade.network.protocol.ProtocolVersion.MAXIMUM_VERSION;
import static me.tom.cascade.network.protocol.ProtocolVersion.MINIMUM_VERSION;
import static me.tom.cascade.network.protocol.ProtocolVersion.UNKNOWN;
import static me.tom.cascade.network.protocol.ProtocolVersion.getFromVersionNumber;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.Key;
import java.util.UUID;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import io.jsonwebtoken.security.Keys;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import me.tom.cascade.cache.ServerCache;
import me.tom.cascade.cache.StatusCache;
import me.tom.cascade.config.ProxyConfig;
import me.tom.cascade.config.ProxyConfigLoader;
import me.tom.cascade.network.CascadeProxy;

public class CascadeBootstrap 
{
	public static final EventLoopGroup EVENT_LOOP_GROUP = new NioEventLoopGroup();
	public static final StatusCache STATUS_CACHE = new StatusCache();
	public static final ServerCache SERVER_CACHE = new ServerCache();
	public static ProxyConfig CONFIG;
	public static Key JWT_KEY;
	
	private static CascadeProxy PROXY;
	
	public static final Gson GSON = new GsonBuilder()
    	    .registerTypeAdapter(UUID.class, new MojangUUIDAdapter())
    	    .create();
			
    public static void main( String[] args ) throws InterruptedException, FileNotFoundException, IOException
    {
    	CONFIG = ProxyConfigLoader.load();
    	JWT_KEY = Keys.hmacShaKeyFor(CONFIG.getJwtSecret().getBytes());
    	
    	PROXY = new CascadeProxy(CONFIG.getProxyPort());
    	PROXY.start();
    }
}