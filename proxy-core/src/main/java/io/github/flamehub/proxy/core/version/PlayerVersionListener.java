package io.github.flamehub.proxy.core.version;

import com.velocitypowered.api.event.PostOrder;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.connection.PreLoginEvent;
import com.velocitypowered.api.proxy.InboundConnection;
import io.github.flamehub.proxy.core.message.VelocityMessagesService;
import io.github.flamehub.proxy.core.util.TextUtil;

public final class PlayerVersionListener {

  private final VelocityMessagesService messagesService;

  public PlayerVersionListener(VelocityMessagesService messagesService) {
    this.messagesService = messagesService;
  }

  @Subscribe(order = PostOrder.FIRST)
  public void onPreLogin(PreLoginEvent event) {
    if (event.getResult().isAllowed()) {
      InboundConnection connection = event.getConnection();

      String playerVersion = connection.getProtocolVersion().getVersionIntroducedIn();

      if (compareVersions(playerVersion, "1.16") < 0) {
        event.setResult(
            TextUtil.preDenied(this.messagesService.getMessage("wrong.client.version")));
      }
    }
  }

  private int compareVersions(String version1, String version2) {
    String[] parts1 = version1.split("\\.");
    String[] parts2 = version2.split("\\.");

    for (int i = 0; i < Math.min(parts1.length, parts2.length); i++) {
      int part1 = Integer.parseInt(parts1[i]);
      int part2 = Integer.parseInt(parts2[i]);
      if (part1 < part2) {
        return -1;
      } else if (part1 > part2) {
        return 1;
      }
    }

    return Integer.compare(parts1.length, parts2.length);
  }

}
