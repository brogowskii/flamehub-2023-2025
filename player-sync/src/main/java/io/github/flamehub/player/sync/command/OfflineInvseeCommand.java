package io.github.flamehub.player.sync.command;


import dev.rollczi.litecommands.annotations.argument.Arg;
import dev.rollczi.litecommands.annotations.command.Command;
import dev.rollczi.litecommands.annotations.context.Context;
import dev.rollczi.litecommands.annotations.execute.Execute;
import dev.rollczi.litecommands.annotations.permission.Permission;
import io.github.flamehub.commons.bukkit.util.SerializationUtil;
import io.github.flamehub.commons.network.player.NetworkPlayer;
import io.github.flamehub.commons.network.player.NetworkPlayerCache;
import io.github.flamehub.commons.server.NetworkServerCache;
import io.github.flamehub.player.sync.data.PlayerSyncData;
import io.github.flamehub.player.sync.data.PlayerSyncDataRepository;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

@Command(name = "offlineinv")
@Permission("server.commands.offlineinv")
public final class OfflineInvseeCommand {

  private final PlayerSyncDataRepository playerSyncDataRepository;
  private final NetworkPlayerCache networkPlayerCache;
  private final NetworkServerCache networkServerCache;

  public OfflineInvseeCommand(PlayerSyncDataRepository playerSyncDataRepository,
      NetworkPlayerCache networkPlayerCache, NetworkServerCache networkServerCache) {
    this.playerSyncDataRepository = playerSyncDataRepository;
    this.networkPlayerCache = networkPlayerCache;
    this.networkServerCache = networkServerCache;
  }

  @Execute
  void exec(@Context Player player, @Arg String playerName) {

    NetworkPlayer networkPlayer = this.networkPlayerCache.findByName(playerName);
    if (networkPlayer == null || !networkPlayer.getServer()
        .contains(this.networkServerCache.getCurrent().getCategory())) {

      PlayerSyncData playerSyncData = this.playerSyncDataRepository.load("playerName", playerName);
      if (playerSyncData == null) {
        player.sendMessage("playersyncdata==null");
        return;
      }

      ItemStack[] contents;
      try {
        contents = (ItemStack[]) SerializationUtil.deserializeBukkitObject(
            playerSyncData.getSerializedInventory());
      } catch (Exception e) {
        player.sendMessage(e.getMessage());
        return;
      }

      if (contents == null) {
        player.sendMessage("contents==null");
        return;
      }

      Inventory inventory = Bukkit.createInventory(player, 36,
          "Inventory gracza:" + playerSyncData.getPlayerName());
      for (ItemStack itemStack : contents) {
        if (itemStack == null) {
          continue;
        }

        inventory.addItem(itemStack.clone());

      }

      player.openInventory(inventory);
    }
  }
}
