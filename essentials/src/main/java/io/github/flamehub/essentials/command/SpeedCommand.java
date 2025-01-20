package io.github.flamehub.essentials.command;

import dev.rollczi.litecommands.annotations.argument.Arg;
import dev.rollczi.litecommands.annotations.command.Command;
import dev.rollczi.litecommands.annotations.context.Context;
import dev.rollczi.litecommands.annotations.execute.Execute;
import dev.rollczi.litecommands.annotations.permission.Permission;
import io.github.flamehub.commons.bukkit.message.BukkitMessagesService;
import org.bukkit.entity.Player;

@Command(name = "speed")
@Permission("server.essentials.commands.speed")
final class SpeedCommand {

  private final BukkitMessagesService messagesService;

  public SpeedCommand(final BukkitMessagesService messagesService) {
    this.messagesService = messagesService;
  }

  @Execute
  void execute(@Context final Player player, @Arg final int speedValue) {

    if (speedValue < 1 || speedValue > 10) {
      return;
    }

    final float speed = speedValue / 10f;
    if (player.isFlying()) {
      player.setFlySpeed(speed);
      messagesService.message("speed.flying.changed")
          .with("amount", speed)
          .deliver(player);
      return;
    }

    player.setWalkSpeed(speed);
    messagesService.message("speed.walk.changed")
        .with("amount", speed)
        .deliver(player);

  }

  @Execute
  @Permission("server.essentials.commands.speed.other")
  void executeOther(@Context final Player player, @Arg final Player target,
      @Arg final int speedValue) {

    if (speedValue < 1 || speedValue > 10) {
      return;
    }

    final float speed = speedValue / 10f;
    if (target.isFlying()) {
      target.setFlySpeed(speed);
      messagesService.message("speed.flying.changed.other")
          .with("amount", speed)
          .with("target", target.getName())
          .deliver(player);
      return;
    }

    target.setWalkSpeed(speed);
    messagesService.message("speed.walk.changed.other")
        .with("amount", speed)
        .with("target", target.getName())
        .deliver(player);
  }

}
