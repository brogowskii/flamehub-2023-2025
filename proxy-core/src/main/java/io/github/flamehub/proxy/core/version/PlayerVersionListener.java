package io.github.flamehub.proxy.core.version;

import com.velocitypowered.api.event.PostOrder;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.connection.PreLoginEvent;
import com.velocitypowered.api.network.ProtocolVersion;
import com.velocitypowered.api.proxy.InboundConnection;
import io.github.flamehub.proxy.core.ProxyMessages;
import io.github.flamehub.proxy.core.util.TextUtil;

public final class PlayerVersionListener {

  private final ProxyMessages proxyMessages;

  public PlayerVersionListener(final ProxyMessages proxyMessages) {
    this.proxyMessages = proxyMessages;
  }

  @Subscribe(order = PostOrder.FIRST)
  public void onPreLogin(final PreLoginEvent event) {
    if (event.getResult().isAllowed()) {
      final InboundConnection connection = event.getConnection();
      final ProtocolVersion protocolVersion = connection.getProtocolVersion();
      final String playerVersion = protocolVersion.getVersionIntroducedIn();

      if (compareVersions(playerVersion, "1.16") < 0) {
        event.setResult(
            TextUtil.preDenied(proxyMessages.wrongClientVersion.applyFirstAsComponent()));
      }
    }
  }

  private int compareVersions(final String version1, final String version2) {
    final String[] parts1 = version1.split("\\.");
    final String[] parts2 = version2.split("\\.");

    for (int i = 0; i < Math.min(parts1.length, parts2.length); i++) {
      final int part1 = Integer.parseInt(parts1[i]);
      final int part2 = Integer.parseInt(parts2[i]);
      if (part1 < part2) {
        return -1;
      }
      if (part1 > part2) {
        return 1;
      }
    }

    return Integer.compare(parts1.length, parts2.length);
  }

}
