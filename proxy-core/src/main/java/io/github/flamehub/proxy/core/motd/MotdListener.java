package io.github.flamehub.proxy.core.motd;

import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyPingEvent;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import com.velocitypowered.api.proxy.server.ServerPing;
import com.velocitypowered.api.util.Favicon;
import io.github.flamehub.commons.server.NetworkServerCache;
import io.github.flamehub.proxy.core.message.VelocityMessage;
import io.github.flamehub.proxy.core.util.TextUtil;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.UUID;

public final class MotdListener {

    private final MotdConfig motdConfig;
    private final ProxyServer proxyServer;
    private final NetworkServerCache networkServerCache;
    private final BufferedImage image;

    public MotdListener(MotdConfig motdConfig, ProxyServer proxyServer, NetworkServerCache networkServerCache) {

        this.motdConfig = motdConfig;
        this.proxyServer = proxyServer;
        this.networkServerCache = networkServerCache;

        File iconFile = new File("server-icon.png");
        try {
            this.image = ImageIO.read(iconFile);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Subscribe
    public void onProxyPingEvent(ProxyPingEvent event) {
        int averagePing = 0;
        if (this.proxyServer.getPlayerCount() != 0) {
            for (Player player : this.proxyServer.getAllPlayers()) {
                averagePing += player.getPing();
            }

            averagePing = averagePing / this.proxyServer.getPlayerCount();
        }

        VelocityMessage velocityMessage = VelocityMessage.from(this.motdConfig.getSample())
                .with("average_ping", String.valueOf(averagePing))
                .with("current_proxy", this.networkServerCache.getCurrent().getName());

        ServerPing.Builder builder = event.getPing().asBuilder();
        builder.description(TextUtil.parse(this.motdConfig.getFormattedMotd()));
        long globalPlayers = this.networkServerCache.getPlayersFrom("proxy");
        builder.onlinePlayers((int) globalPlayers);
        builder.maximumPlayers((int) globalPlayers + 1);
        builder.clearSamplePlayers();
        builder.favicon(Favicon.create(this.image));

        List<String> sample = velocityMessage.apply();
        ServerPing.SamplePlayer[] samplePlayers = new ServerPing.SamplePlayer[sample.size()];
        for (int i = 0; i < samplePlayers.length; i++) {
            samplePlayers[i] = new ServerPing.SamplePlayer(
                    LegacyComponentSerializer.legacySection().serialize(TextUtil.parse(sample.get(i))),
                    UUID.randomUUID()
            );
        }

        builder.samplePlayers(samplePlayers);
        event.setPing(builder.build());
    }
}