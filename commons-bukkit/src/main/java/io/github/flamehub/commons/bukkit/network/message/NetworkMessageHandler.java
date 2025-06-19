package io.github.flamehub.commons.bukkit.network.message;

import io.github.flamehub.commons.bukkit.text.TextUtil;
import io.github.flamehub.commons.bukkit.user.impl.CommonUser;
import io.github.flamehub.commons.bukkit.user.impl.CommonUserCache;
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

  private final CommonUserCache commonUserCache;
  private final NetworkServerCache networkServerCache;

  public NetworkMessageHandler(
      final CommonUserCache commonUserCache,
      final NetworkServerCache networkServerCache
  ) {
    this.commonUserCache = commonUserCache;
    this.networkServerCache = networkServerCache;
  }

  @PacketHandler
  public void handle(final NetworkMessage networkMessage) {
    final NetworkMessageFilter filter = networkMessage.getFilter();
    final NetworkServer current = networkServerCache.getCurrent();
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

    for (final Player player : targetPlayers) {

      if (player == null) {
        continue;
      }

      if (filter.getTargetPermission() != null) {
        if (!player.hasPermission(filter.getTargetPermission())) {
          continue;
        }
      }

      final CommonUser commonUser = commonUserCache.findByKey(player.getUniqueId());
      if (commonUser != null) {
        if (filter.getIdForHide() != null) {
          if (commonUser.isDisabledNetworkMessage(filter.getIdForHide())) {
            continue;
          }
        }
      }

      switch (networkMessage.getType()) {

        case CHAT ->
            networkMessage.getMessages().forEach(s -> player.sendMessage(TextUtil.parse(s)));
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
