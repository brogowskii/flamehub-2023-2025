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

@Command(name = "ecpreview")
@Permission("server.commands.ecpreview")
public final class EnderChestPreviewCommand {

  private final PlayerSyncDataRepository playerSyncDataRepository;
  private final NetworkPlayerCache networkPlayerCache;
  private final NetworkServerCache networkServerCache;

  public EnderChestPreviewCommand(PlayerSyncDataRepository playerSyncDataRepository,
      NetworkPlayerCache networkPlayerCache, NetworkServerCache networkServerCache) {
    this.playerSyncDataRepository = playerSyncDataRepository;
    this.networkPlayerCache = networkPlayerCache;
    this.networkServerCache = networkServerCache;
  }

  @Execute
  void exec(@Context Player player, @Arg String playerName) {

    NetworkPlayer networkPlayer = networkPlayerCache.findByName(playerName);
    if (networkPlayer == null || !networkPlayer.getServer()
        .contains(networkServerCache.getCurrent().getCategory())) {

      PlayerSyncData playerSyncData = playerSyncDataRepository.load("playerName", playerName);
      if (playerSyncData == null) {
        player.sendMessage("playersyncdata==null");
        return;
      }

      ItemStack[] enderChest;
      try {
        enderChest = (ItemStack[]) SerializationUtil.deserializeBukkitObject(
            playerSyncData.getSerializedEnderchest());
      } catch (Exception e) {
        player.sendMessage(e.getMessage());
        return;
      }

      if (enderChest == null) {
        player.sendMessage("enderchest==null");
        return;
      }

      Inventory inventory = Bukkit.createInventory(player, 27,
          "Enderchest gracza:" + playerSyncData.getPlayerName());
      for (ItemStack itemStack : enderChest) {
        if (itemStack == null) {
          continue;
        }

        inventory.addItem(itemStack.clone());

      }

      player.openInventory(inventory);
      return;
    }

    if (!networkPlayer.getServer().equals(networkServerCache.getCurrent().getName())) {
      player.sendMessage(
          "ten gracz jest na serwerze, ale znajduje się na: " + networkPlayer.getServer());
      return;
    }

    Player target = Bukkit.getPlayer(playerName);
    if (target == null) {
      return;
    }

    player.openInventory(target.getEnderChest());


  }

}
