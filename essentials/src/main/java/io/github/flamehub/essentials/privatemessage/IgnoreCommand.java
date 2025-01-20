package io.github.flamehub.essentials.privatemessage;

import dev.rollczi.litecommands.annotations.argument.Arg;
import dev.rollczi.litecommands.annotations.command.Command;
import dev.rollczi.litecommands.annotations.context.Context;
import dev.rollczi.litecommands.annotations.execute.Execute;
import io.github.flamehub.commons.bukkit.message.BukkitMessagesService;
import io.github.flamehub.commons.network.player.NetworkPlayer;
import io.github.flamehub.essentials.user.EssentialsUser;
import io.github.flamehub.essentials.user.EssentialsUserFacade;
import org.bukkit.entity.Player;

@Command(name = "ignore", aliases = "ignoruj")
final class IgnoreCommand {

  private final BukkitMessagesService messagesService;
  private final EssentialsUserFacade essentialsUserFacade;

  IgnoreCommand(final BukkitMessagesService messagesService,
      final EssentialsUserFacade essentialsUserFacade) {
    this.messagesService = messagesService;
    this.essentialsUserFacade = essentialsUserFacade;
  }

  @Execute(name = "all", aliases = "*")
  void executeAll(@Context final Player player) {
    final EssentialsUser essentialsUser = essentialsUserFacade.findByUniqueId(
        player.getUniqueId());
    essentialsUser.setIgnoreAll(!essentialsUser.isIgnoreAll());
    essentialsUser.setNeedUpdate(true);
    messagesService.message("ignore.all.status.changed")
        .with("status", essentialsUser.isIgnoreAll() ? "&awłączony" : "&cwyłączony")
        .deliver(player);
  }

  @Execute
  void execute(@Context final Player player, @Arg("gracz") final NetworkPlayer target) {
    final EssentialsUser essentialsUser = essentialsUserFacade.findByUniqueId(
        player.getUniqueId());
    if (essentialsUser.isIgnore(target.getUniqueId())) {
      essentialsUser.removeIgnore(target.getUniqueId());
      messagesService.message("ignore.remove")
          .with("target", target.getName())
          .deliver(player);
    } else {

      essentialsUser.addIgnore(target.getUniqueId());
      messagesService.message("ignore.add")
          .with("target", target.getName())
          .deliver(player);
    }

    essentialsUser.setNeedUpdate(true);
  }


}
