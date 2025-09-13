package io.github.flamehub.player.sync.command;

import dev.rollczi.litecommands.annotations.argument.Arg;
import dev.rollczi.litecommands.annotations.command.Command;
import dev.rollczi.litecommands.annotations.context.Context;
import dev.rollczi.litecommands.annotations.execute.Execute;
import dev.rollczi.litecommands.annotations.permission.Permission;
import io.github.flamehub.commons.bukkit.util.SerializationUtil;
import io.github.flamehub.commons.network.player.NetworkPlayer;
import io.github.flamehub.commons.network.player.NetworkPlayerCache;
import io.github.flamehub.commons.server.NetworkServerFacade;
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
  private final NetworkServerFacade networkServerFacade;

  public EnderChestPreviewCommand(final PlayerSyncDataRepository playerSyncDataRepository,
      final NetworkPlayerCache networkPlayerCache, final NetworkServerFacade networkServerFacade) {
    this.playerSyncDataRepository = playerSyncDataRepository;
    this.networkPlayerCache = networkPlayerCache;
    this.networkServerFacade = networkServerFacade;
  }

  @Execute
  void exec(@Context final Player player, @Arg final String playerName) {

    final NetworkPlayer networkPlayer = networkPlayerCache.findByName(playerName);
    if (networkPlayer == null || !networkPlayer.getServer()
        .contains(networkServerFacade.getCurrent().getCategory())) {

      final PlayerSyncData playerSyncData = playerSyncDataRepository.load("playerName", playerName);
      if (playerSyncData == null) {
        player.sendMessage("playersyncdata==null");
        return;
      }

      final ItemStack[] enderChest;
      try {
        enderChest = (ItemStack[]) SerializationUtil.deserializeBukkitObject(
            playerSyncData.getSerializedEnderchest());
      } catch (final Exception e) {
        player.sendMessage(e.getMessage());
        return;
      }

      if (enderChest == null) {
        player.sendMessage("enderchest==null");
        return;
      }

      final Inventory inventory = Bukkit.createInventory(player, 27,
          "Enderchest gracza:" + playerSyncData.getPlayerName());
      for (final ItemStack itemStack : enderChest) {
        if (itemStack == null) {
          continue;
        }

        inventory.addItem(itemStack.clone());

      }

      player.openInventory(inventory);
      return;
    }

    if (!networkPlayer.getServer().equals(networkServerFacade.getCurrent().getName())) {
      player.sendMessage(
          "ten gracz jest na serwerze, ale znajduje się na: " + networkPlayer.getServer());
      return;
    }

    final Player target = Bukkit.getPlayer(playerName);
    if (target == null) {
      return;
    }

    player.openInventory(target.getEnderChest());


  }

}
