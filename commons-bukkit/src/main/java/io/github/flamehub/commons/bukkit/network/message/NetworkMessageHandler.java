package io.github.flamehub.commons.bukkit.network.message;

import io.github.flamehub.commons.bukkit.text.TextUtil;
import io.github.flamehub.commons.bukkit.util.TitleUtil;
import io.github.flamehub.commons.messenger.packet.PacketHandler;
import io.github.flamehub.commons.network.message.NetworkMessage;
import io.github.flamehub.commons.network.message.NetworkMessageFilter;
import io.github.flamehub.commons.server.NetworkServer;
import io.github.flamehub.commons.server.NetworkServerCache;
import java.util.Collection;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public final class NetworkMessageHandler {

  private final NetworkServerCache networkServerCache;

  public NetworkMessageHandler(NetworkServerCache networkServerCache) {
    this.networkServerCache = networkServerCache;
  }

  @PacketHandler
  public void handle(NetworkMessage networkMessage) {
    NetworkMessageFilter filter = networkMessage.getFilter();
    NetworkServer current = this.networkServerCache.getCurrent();
    if (filter.getTargetServers() != null) {
      if (!filter.getTargetServers().contains(current.getName())) {
        return;
      }
    }

    if (filter.getTargetServerCategory() != null) {
      if (!filter.getTargetServerCategory().equals(current.getCategory())) {
        return;
      }
    }

    Collection<? extends Player> targetPlayers = Bukkit.getOnlinePlayers();
    if (filter.getTargetPlayers() != null) {
      targetPlayers = filter.getTargetPlayers()
          .stream()
          .map(Bukkit::getPlayer)
          .toList();
    }

    for (Player player : targetPlayers) {

      if (player == null) {
        continue;
      }

      if (filter.getTargetPermission() != null) {
        if (!player.hasPermission(filter.getTargetPermission())) {
          continue;
        }
      }

      switch (networkMessage.getType()) {

        case CHAT ->
            networkMessage.getMessages().forEach(s -> player.sendMessage(TextUtil.legacyColor(s)));
        case ACTION_BAR ->
            networkMessage.getMessages().forEach(s -> player.sendActionBar(TextUtil.parse(s)));
        case TITLE -> TitleUtil.title(
            player,
            networkMessage.getMessages().get(0),
            networkMessage.getMessages().get(1),
            20,
            60,
            20
        );

      }


    }
  }
}
