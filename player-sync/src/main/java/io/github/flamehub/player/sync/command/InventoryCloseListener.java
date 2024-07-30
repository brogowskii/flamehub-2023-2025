package io.github.flamehub.player.sync.command;

import io.github.flamehub.commons.bukkit.util.SerializationUtil;
import io.github.flamehub.player.sync.data.PlayerSyncData;
import io.github.flamehub.player.sync.data.PlayerSyncDataRepository;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;

public final class InventoryCloseListener implements Listener {

  private final PlayerSyncDataRepository playerSyncDataRepository;

  public InventoryCloseListener(PlayerSyncDataRepository playerSyncDataRepository) {
    this.playerSyncDataRepository = playerSyncDataRepository;
  }

  @EventHandler
  void onClose(InventoryCloseEvent event) {

    String title = event.getView().getTitle();
    if (title.isEmpty()) {
      return;
    }
    Player player = (Player) event.getPlayer();
    if (title.startsWith("Enderchest gracza:")) {

      if (player.hasPermission("server.invsee")) {

        String[] split = title.split(":");
        String target = split[1];
        PlayerSyncData playerSyncData = this.playerSyncDataRepository.load("playerName", target);
        playerSyncData.setSerializedEnderchest(
            SerializationUtil.serializeBukkitObject(event.getInventory().getContents()));
        this.playerSyncDataRepository.save(playerSyncData);
        player.sendMessage("zapisano dane uzytkownika " + playerSyncData.getPlayerName());

      }

    } else if (title.startsWith("Inventory gracza:")) {
      if (player.hasPermission("server.invsee")) {

        String[] split = title.split(":");
        String target = split[1];
        PlayerSyncData playerSyncData = this.playerSyncDataRepository.load("playerName", target);
        playerSyncData.setSerializedInventory(
            SerializationUtil.serializeBukkitObject(event.getInventory().getContents()));
        this.playerSyncDataRepository.save(playerSyncData);
        player.sendMessage("zapisano dane uzytkownika " + playerSyncData.getPlayerName());

      }
    }

  }

}
