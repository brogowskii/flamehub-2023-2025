package io.github.flamehub.essentials.command;

import dev.rollczi.litecommands.annotations.command.Command;
import dev.rollczi.litecommands.annotations.context.Context;
import dev.rollczi.litecommands.annotations.execute.Execute;
import io.github.flamehub.commons.bukkit.message.BukkitMessage;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;

@Command(name = "gamma")
final class GammaCommand {

  @Execute
  void exec(@Context Player player) {

    if (player.hasPotionEffect(PotionEffectType.NIGHT_VISION)) {
      player.removePotionEffect(PotionEffectType.NIGHT_VISION);
      BukkitMessage.from("&cWyłaczono widzenie w ciemności.").send(player);
      return;
    }

    player.addPotionEffect(PotionEffectType.NIGHT_VISION.createEffect(999999999, 0));
    BukkitMessage.from("&aWłączono widzenie w ciemności.").send(player);

  }

}
