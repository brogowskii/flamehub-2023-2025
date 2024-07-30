package io.github.flamehub.essentials.spawn;

import dev.rollczi.litecommands.annotations.argument.Arg;
import dev.rollczi.litecommands.annotations.command.Command;
import dev.rollczi.litecommands.annotations.context.Context;
import dev.rollczi.litecommands.annotations.execute.Execute;
import dev.rollczi.litecommands.annotations.permission.Permission;
import io.github.flamehub.commons.bukkit.teleport.TeleporterService;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@Command(name = "spawn")
final class SpawnCommand {

  private final SpawnFacade spawnFacade;
  private final TeleporterService teleporterService;

  SpawnCommand(final SpawnFacade spawnFacade, final TeleporterService teleporterService) {
    this.spawnFacade = spawnFacade;
    this.teleporterService = teleporterService;
  }

  @Execute
  void execute(@Context final Player player) {

    final Location spawnLocation = this.spawnFacade.getSpawnLocation().clone();
    this.teleporterService.teleport(player, spawnLocation, 5);

  }

  @Execute
  @Permission("server.essentials.commands.spawn.other")
  void other(@Context final CommandSender sender, @Arg final Player target) {

    final Location spawnLocation = this.spawnFacade.getSpawnLocation().clone();
    target.teleport(spawnLocation);


  }

}
