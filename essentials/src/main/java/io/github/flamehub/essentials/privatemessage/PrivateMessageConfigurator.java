package io.github.flamehub.essentials.privatemessage;

import dev.rollczi.litecommands.LiteCommandsBuilder;
import dev.rollczi.litecommands.annotations.LiteCommandsAnnotations;
import dev.rollczi.litecommands.bukkit.LiteBukkitSettings;
import io.github.flamehub.commons.bukkit.dispatcher.FlameDispatcher;
import io.github.flamehub.commons.bukkit.message.BukkitMessagesService;
import io.github.flamehub.commons.messenger.RedisMessenger;
import io.github.flamehub.commons.network.player.NetworkPlayerCache;
import io.github.flamehub.essentials.user.EssentialsUserFacade;
import org.bukkit.command.CommandSender;

public final class PrivateMessageConfigurator {

  public PrivateMessageConfigurator(
      final LiteCommandsBuilder<CommandSender, LiteBukkitSettings, ?> liteCommandsBuilder,
      final FlameDispatcher flameDispatcher,
      final RedisMessenger redisMessenger,
      final BukkitMessagesService messagesService,
      final NetworkPlayerCache networkPlayerCache,
      final EssentialsUserFacade essentialsUserFacade,
      final String currentServer
  ) {

    redisMessenger.subscribe("private_messages",
        new PrivateMessageHandler(messagesService, essentialsUserFacade));
    redisMessenger.subscribe(currentServer, new ReplyHandler(essentialsUserFacade));

    liteCommandsBuilder.commands(LiteCommandsAnnotations.of(
        new PrivateMessageCommand(
            flameDispatcher,
            messagesService,
            redisMessenger,
            essentialsUserFacade
        ),
        new ReplyCommand(
            flameDispatcher,
            networkPlayerCache,
            redisMessenger,
            messagesService,
            essentialsUserFacade
        ),
        new SocialSpyCommand(
            flameDispatcher,
            messagesService,
            essentialsUserFacade
        ),
        new IgnoreCommand(
            messagesService,
            essentialsUserFacade
        )
    ));


  }

}
