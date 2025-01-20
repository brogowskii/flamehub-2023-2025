package io.github.flamehub.player.sync.data;

import io.github.flamehub.commons.network.message.NetworkMessageFilterBuilder;
import io.github.flamehub.commons.network.message.NetworkMessageService;
import io.github.flamehub.commons.network.message.NetworkMessageType;
import io.github.flamehub.commons.server.NetworkServer;
import io.github.flamehub.commons.server.NetworkServerCache;
import java.util.List;
import org.bukkit.Bukkit;

public class PlayerDataSyncSaveTask implements Runnable {

  private final PlayerSyncDataRepository playerSyncDataRepository;
  private final NetworkServerCache networkServerCache;
  private final NetworkMessageService networkMessageService;

  public PlayerDataSyncSaveTask(PlayerSyncDataRepository playerSyncDataRepository,
      NetworkServerCache networkServerCache, NetworkMessageService networkMessageService) {
    this.playerSyncDataRepository = playerSyncDataRepository;
    this.networkServerCache = networkServerCache;
    this.networkMessageService = networkMessageService;
  }

  @Override
  public void run() {
    NetworkServer current = networkServerCache.getCurrent();
    List<PlayerSyncData> collect = Bukkit.getOnlinePlayers().stream()
        .map(PlayerSyncDataFactory::create)
        .toList();
    playerSyncDataRepository.saveMany(collect);

    networkMessageService.send(
        "&3DEBUG-" + current.getName().toUpperCase() + " &bSucessfully saved &3" + collect.size()
            + " &bplayers data!",
        new NetworkMessageFilterBuilder()
            .targetPermission("server.debug")
            .targetServerCategory(current.getCategory())
            .build(),
        NetworkMessageType.CHAT
    );

  }
}
