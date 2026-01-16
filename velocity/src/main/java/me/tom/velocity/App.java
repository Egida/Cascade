package me.tom.velocity;

import java.util.ArrayList;
import java.util.UUID;

import javax.inject.Inject;

import org.slf4j.Logger;

import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.player.ServerPreConnectEvent;
import com.velocitypowered.api.event.player.ServerPreConnectEvent.ServerResult;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.proxy.ProxyServer;
import com.velocitypowered.api.proxy.server.RegisteredServer;

@Plugin(
        id = "cascade",
        name = "Cascade",
        version = "1.0"
)
public class App {
	private final ArrayList<UUID> authUsers = new ArrayList<>();

    private final RegisteredServer cascade;
    private final Logger logger;

    @Inject
    public App(ProxyServer proxy, Logger logger) {
        this.logger = logger;
        
        cascade = proxy.getServer("cascade")
                .orElseThrow(() -> new IllegalStateException("Cascade server not found!"));
    }

    @Subscribe
    public void onServerPreConnect(ServerPreConnectEvent event) {
    	if(!authUsers.contains(event.getPlayer().getUniqueId())) {
    		event.setResult(ServerResult.allowed(cascade));
            logger.info("Redirected {} to cascade", event.getPlayer().getUsername());
    	}
    }
}