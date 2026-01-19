package me.tom.velocity;

import java.util.ArrayList;

import javax.inject.Inject;

import org.slf4j.Logger;

import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.connection.PluginMessageEvent;
import com.velocitypowered.api.event.connection.PreLoginEvent;
import com.velocitypowered.api.event.connection.PreLoginEvent.PreLoginComponentResult;
import com.velocitypowered.api.event.player.ServerPreConnectEvent;
import com.velocitypowered.api.event.player.ServerPreConnectEvent.ServerResult;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.proxy.ProxyServer;
import com.velocitypowered.api.proxy.messages.MinecraftChannelIdentifier;
import com.velocitypowered.api.proxy.server.RegisteredServer;

import io.netty.buffer.ByteBuf;
import me.tom.common.network.protocol.packet.types.Utf8String;

@Plugin(
        id = "cascade",
        name = "Cascade",
        version = "1.0"
)
public class App {
	private final ArrayList<String> authUsers = new ArrayList<>();

    private final RegisteredServer cascade;
    private final Logger logger;

    @Inject
    public App(ProxyServer proxy, Logger logger) {
    	proxy.getChannelRegistrar().register(MinecraftChannelIdentifier.from("cascade:login"));
        this.logger = logger;
        
        cascade = proxy.getServer("cascade")
                .orElseThrow(() -> new IllegalStateException("Cascade server not found!"));
    }

    @Subscribe
    public void onServerPreConnect(ServerPreConnectEvent event) {
    	if(!authUsers.contains(event.getPlayer().getUsername())) {
    		event.setResult(ServerResult.allowed(cascade));
    	}
    }
    
    @Subscribe
    public void onPreLogin(PreLoginEvent event) {
    	if(!authUsers.contains(event.getUsername())) {
    		event.setResult(PreLoginComponentResult.forceOfflineMode());
    	}
    }
    
    @Subscribe
    public void onPluginMessage(PluginMessageEvent event) {
    	if (!event.getIdentifier().getId().equals("cascade:login"))
            return;

        byte[] data = event.getData();
        ByteBuf buf = io.netty.buffer.Unpooled.wrappedBuffer(data);
        String username = Utf8String.read(buf, 16);
        authUsers.add(username);

        buf.release();
    }
}