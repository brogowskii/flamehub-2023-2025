package io.github.flamehub.player.sync.data;

import io.github.flamehub.commons.network.message.NetworkMessageFilterBuilder;
import io.github.flamehub.commons.network.message.NetworkMessageService;
import io.github.flamehub.commons.network.message.NetworkMessageType;
import io.github.flamehub.commons.server.NetworkServerContext;
import java.util.ArrayList;
import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public final class PlayerDataSyncSaveTask implements Runnable {

  private final PlayerSyncDataRepository playerSyncDataRepository;
  private final NetworkMessageService networkMessageService;

  public PlayerDataSyncSaveTask(
      final PlayerSyncDataRepository playerSyncDataRepository,
      final NetworkMessageService networkMessageService
  ) {
    this.playerSyncDataRepository = playerSyncDataRepository;
    this.networkMessageService = networkMessageService;
  }

  @Override
  public void run() {
    final List<Player> onlinePlayers = new ArrayList<>(Bukkit.getOnlinePlayers());

    final List<PlayerSyncData> collect = onlinePlayers.stream()
        .filter(Player::isOnline)
        .map(player -> {
          try {
            return PlayerSyncDataFactory.create(player);
          } catch (final Exception e) {
            System.err.println(
                "Błąd przy tworzeniu PlayerSyncData dla gracza: " + player.getName());
            e.printStackTrace();
            return null;
          }
        })
        .filter(java.util.Objects::nonNull)
        .toList();

    if (!collect.isEmpty()) {
      try {
        playerSyncDataRepository.saveMany(collect);
      } catch (final Exception e) {
        System.err.println("Błąd przy zapisywaniu danych graczy:");
        e.printStackTrace();
        return;
      }
    }

    networkMessageService.send(
        "&3DEBUG-" + NetworkServerContext.CURRENT_NAME.toUpperCase() + " &bSucessfully saved &3"
            + collect.size()
            + " &bplayers data!",
        new NetworkMessageFilterBuilder()
            .targetPermission("server.debug")
            .targetServerCategory(NetworkServerContext.CURRENT_CATEGORY)
            .build(),
        NetworkMessageType.CHAT
    );
  }
}