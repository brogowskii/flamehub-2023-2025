package io.github.flamehub.essentials.privatemessage;

import dev.rollczi.litecommands.annotations.command.Command;
import dev.rollczi.litecommands.annotations.context.Context;
import dev.rollczi.litecommands.annotations.execute.Execute;
import dev.rollczi.litecommands.annotations.permission.Permission;
import io.github.flamehub.commons.bukkit.dispatcher.FlameDispatcher;
import io.github.flamehub.commons.bukkit.message.BukkitMessagesService;
import io.github.flamehub.essentials.user.EssentialsUser;
import io.github.flamehub.essentials.user.EssentialsUserFacade;
import java.util.concurrent.CompletableFuture;
import org.bukkit.entity.Player;

@Command(name = "socialspy")
@Permission("server.skypvp.commands.socialspy")
final class SocialSpyCommand {

  private final FlameDispatcher flameDispatcher;
  private final BukkitMessagesService messagesService;
  private final EssentialsUserFacade essentialsUserFacade;

  SocialSpyCommand(
      final FlameDispatcher flameDispatcher,
      final BukkitMessagesService messagesService,
      final EssentialsUserFacade essentialsUserFacade
  ) {
    this.flameDispatcher = flameDispatcher;
    this.messagesService = messagesService;
    this.essentialsUserFacade = essentialsUserFacade;
  }

  @Execute
  void execute(@Context final Player player) {

    final EssentialsUser essentialsUser = essentialsUserFacade.findByUniqueId(
        player.getUniqueId());
    essentialsUser.setSocialSpy(!essentialsUser.isSocialSpy());
    CompletableFuture.supplyAsync(() -> essentialsUserFacade.save(essentialsUser))
        .thenAccept(essUser -> {
          messagesService.sendMessage(player,
              essentialsUser.isSocialSpy() ? "socialspy.on" : "socialspy.off");
        });


  }

}
