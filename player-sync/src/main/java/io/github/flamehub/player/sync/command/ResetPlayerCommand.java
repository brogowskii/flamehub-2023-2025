package io.github.flamehub.player.sync.command;

import dev.rollczi.litecommands.annotations.argument.Arg;
import dev.rollczi.litecommands.annotations.command.Command;
import dev.rollczi.litecommands.annotations.context.Context;
import dev.rollczi.litecommands.annotations.execute.Execute;
import dev.rollczi.litecommands.annotations.permission.Permission;
import io.github.flamehub.player.sync.data.PlayerSyncData;
import io.github.flamehub.player.sync.data.PlayerSyncDataRepository;
import java.util.concurrent.CompletableFuture;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

@Command(name = "resetplayer")
@Permission("server.commands.resetplayer")
public final class ResetPlayerCommand {

  private final PlayerSyncDataRepository playerSyncDataRepository;

  public ResetPlayerCommand(final PlayerSyncDataRepository playerSyncDataRepository) {
    this.playerSyncDataRepository = playerSyncDataRepository;
  }

  @Execute
  void exec(@Context Player player, @Arg String target) {

    Player targetPlayer = Bukkit.getPlayerExact(target);
    if (targetPlayer != null) {
      targetPlayer.kick();
    }

    CompletableFuture.supplyAsync(() -> this.playerSyncDataRepository.load("playerName", target))
        .thenAccept(this.playerSyncDataRepository::delete);





  }



}
