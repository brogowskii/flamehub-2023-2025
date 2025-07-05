package io.github.flamehub.player.sync.command;

import dev.rollczi.litecommands.annotations.command.Command;
import dev.rollczi.litecommands.annotations.context.Context;
import dev.rollczi.litecommands.annotations.execute.Execute;
import dev.rollczi.litecommands.annotations.permission.Permission;
import io.github.flamehub.player.sync.data.PlayerSyncDataRepository;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@Command(name = "stop")
@Permission("server.commands.stop")
public final class StopCommand {

  private final PlayerSyncDataRepository playerSyncDataRepository;

  public StopCommand(final PlayerSyncDataRepository playerSyncDataRepository) {
    this.playerSyncDataRepository = playerSyncDataRepository;
  }

  @Execute
  void exec(@Context final CommandSender sender) {
    sender.sendMessage("Wyłączanie serwera...");
    for (final Player onlinePlayer : Bukkit.getOnlinePlayers()) {
      onlinePlayer.closeInventory();
    }

    Bukkit.shutdown();
  }

}
