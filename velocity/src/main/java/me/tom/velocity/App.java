package me.tom.velocity;

import java.net.InetSocketAddress;
import java.time.Duration;
import java.util.Optional;

import javax.inject.Inject;

import org.slf4j.Logger;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
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
import com.velocitypowered.api.proxy.server.ServerInfo;

import io.netty.buffer.ByteBuf;
import me.tom.common.network.protocol.packet.types.Utf8String;
import me.tom.server.IntegratedServer;

@Plugin(
        id = "cascade",
        name = "Cascade",
        version = "1.0"
)
public class App {
    private final Cache<String, Boolean> authUsers =
            Caffeine.newBuilder()
                    .expireAfterWrite(Duration.ofMinutes(1))
                    .build();

    private RegisteredServer cascade;
    private final Logger logger;

    @Inject
    public App(ProxyServer proxy, Logger logger) throws InterruptedException {
        Optional<RegisteredServer> cascade = proxy.getServer("cascade");

        if (!cascade.isPresent()) {
            IntegratedServer server = new IntegratedServer();
            server.start();

            logger.info("Waiting for cascade server to bind...");

            while (server.getPort() == 0) {
                Thread.sleep(100);
            }

            logger.info("Cascade server bound to {}", server.getPort());

            ServerInfo info = new ServerInfo(
                    "cascade",
                    new InetSocketAddress("127.0.0.1", server.getPort())
            );

            proxy.registerServer(info);
        }

        this.cascade = proxy.getServer("cascade").get();
        this.logger = logger;

        proxy.getChannelRegistrar().register(
                MinecraftChannelIdentifier.from("cascade:login")
        );
    }

    @Subscribe
    public void onServerPreConnect(ServerPreConnectEvent event) {
        if (authUsers.getIfPresent(event.getPlayer().getUsername()) == null) {
            event.setResult(ServerResult.allowed(cascade));
        }
    }

    @Subscribe
    public void onPreLogin(PreLoginEvent event) {
        if (authUsers.getIfPresent(event.getUsername()) == null) {
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

        authUsers.put(username, Boolean.TRUE);

        buf.release();
    }
}