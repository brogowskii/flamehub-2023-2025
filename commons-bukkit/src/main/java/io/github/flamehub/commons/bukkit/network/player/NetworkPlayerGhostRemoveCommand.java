package io.github.flamehub.commons.bukkit.network.player;

import dev.rollczi.litecommands.annotations.command.Command;
import dev.rollczi.litecommands.annotations.context.Context;
import dev.rollczi.litecommands.annotations.execute.Execute;
import dev.rollczi.litecommands.annotations.permission.Permission;
import io.github.flamehub.commons.network.player.NetworkPlayer;
import io.github.flamehub.commons.network.player.NetworkPlayerCache;
import org.bukkit.command.CommandSender;

@Command(name = "networkplayerghostremove")
@Permission("server.commands.networkplayerghostremove")
public final class NetworkPlayerGhostRemoveCommand {

  private final NetworkPlayerCache networkPlayerCache;

  public NetworkPlayerGhostRemoveCommand(final NetworkPlayerCache networkPlayerCache) {
    this.networkPlayerCache = networkPlayerCache;
  }

  @Execute(name = "remove")
  void remove(@Context final CommandSender sender) {
    int i = 0;
    for (final NetworkPlayer value : networkPlayerCache.values()) {
      if ("null".equals(value.getProxy())) {
        i++;
        networkPlayerCache.delete(value);
      }
    }

    sender.sendMessage("Removed " + i + " ghost players.");
  }

}
