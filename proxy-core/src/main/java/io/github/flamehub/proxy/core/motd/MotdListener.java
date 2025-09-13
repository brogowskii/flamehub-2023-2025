package io.github.flamehub.proxy.core.motd;

import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyPingEvent;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import com.velocitypowered.api.proxy.server.ServerPing;
import com.velocitypowered.api.util.Favicon;
import io.github.flamehub.commons.network.player.NetworkPlayerCache;
import io.github.flamehub.commons.server.NetworkServerFacade;
import io.github.flamehub.proxy.core.message.VelocityMessage;
import io.github.flamehub.proxy.core.util.TextUtil;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.UUID;
import javax.imageio.ImageIO;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;

public final class MotdListener {

  private final MotdConfig motdConfig;
  private final ProxyServer proxyServer;
  private final NetworkServerFacade networkServerFacade;
  private final NetworkPlayerCache networkPlayerCache;
  private final BufferedImage image;

  public MotdListener(
      final MotdConfig motdConfig,
      final ProxyServer proxyServer,
      final NetworkServerFacade networkServerFacade,
      final NetworkPlayerCache networkPlayerCache) {

    this.motdConfig = motdConfig;
    this.proxyServer = proxyServer;
    this.networkServerFacade = networkServerFacade;
    this.networkPlayerCache = networkPlayerCache;

    final File iconFile = new File("server-icon.png");
    try {
      image = ImageIO.read(iconFile);
    } catch (final IOException e) {
      throw new RuntimeException(e);
    }
  }

  @Subscribe
  public void onProxyPingEvent(final ProxyPingEvent event) {
    int averagePing = 0;
    if (proxyServer.getPlayerCount() != 0) {
      for (final Player player : proxyServer.getAllPlayers()) {
        averagePing += player.getPing();
      }

      averagePing = averagePing / proxyServer.getPlayerCount();
    }

    final VelocityMessage velocityMessage = VelocityMessage.from(motdConfig.getSample())
        .with("average_ping", String.valueOf(averagePing))
        .with("current_proxy", networkServerFacade.getCurrent().getName());

    final ServerPing.Builder builder = event.getPing().asBuilder();
    builder.description(TextUtil.parse(motdConfig.getFormattedMotd()));
    final long globalPlayers = networkPlayerCache.values().size();
    builder.onlinePlayers((int) globalPlayers);
    builder.maximumPlayers((int) globalPlayers + 1);
    builder.clearSamplePlayers();
    builder.favicon(Favicon.create(image));

    final List<String> sample = velocityMessage.apply();
    final ServerPing.SamplePlayer[] samplePlayers = new ServerPing.SamplePlayer[sample.size()];
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