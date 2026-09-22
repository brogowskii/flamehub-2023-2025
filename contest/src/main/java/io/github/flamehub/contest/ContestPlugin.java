package io.github.flamehub.contest;

import dev.morphia.Datastore;
import dev.rollczi.litecommands.annotations.LiteCommandsAnnotations;
import dev.rollczi.litecommands.argument.ArgumentKey;
import dev.rollczi.litecommands.bukkit.LiteBukkitFactory;
import dev.rollczi.litecommands.bukkit.context.PlayerOnlyContextProvider;
import dev.rollczi.litecommands.cooldown.CooldownState;
import dev.rollczi.litecommands.message.MessageRegistry;
import dev.rollczi.litecommands.schematic.SchematicFormat;
import dev.rollczi.litecommands.suggestion.SuggestionResult;
import io.github.flamehub.commons.bukkit.BukkitModule;
import io.github.flamehub.commons.bukkit.command.argument.LocationArgument;
import io.github.flamehub.commons.bukkit.command.argument.PlayerArgument;
import io.github.flamehub.commons.bukkit.command.handler.CooldownStateResultHandlerImpl;
import io.github.flamehub.commons.bukkit.command.handler.InvalidUsageHandlerImpl;
import io.github.flamehub.commons.bukkit.command.handler.MissingPermissionHandlerImpl;
import io.github.flamehub.commons.bukkit.network.player.NetworkPlayerArgument;
import io.github.flamehub.commons.database.DatastoreFactory;
import io.github.flamehub.commons.network.player.NetworkPlayer;
import io.github.flamehub.contest.ticket.ContestTicket;
import io.github.flamehub.contest.ticket.ContestTicketCommand;
import io.github.flamehub.contest.ticket.ContestTicketConfigurator;
import io.github.flamehub.contest.ticket.ContestTicketFacade;
import io.github.flamehub.contest.user.ContestUser;
import io.github.flamehub.contest.user.ContestUserConfigurator;
import io.github.flamehub.contest.user.ContestUserFacade;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public final class ContestPlugin extends BukkitModule {

  public static ContestPlugin INSTANCE;

  private ContestUserFacade contestUserFacade;
  private ContestTicketFacade contestTicketFacade;
  private ContestFacade contestFacade;

  @Override
  public void onEnable() {
    INSTANCE = this;
    super.onEnable();

    final Datastore datastore = DatastoreFactory.create(
        databaseConnector.getMongoClient(),
        "global",
        ContestTicket.class,
        ContestUser.class
    );

    contestUserFacade = ContestUserConfigurator.create(
        this,
        flameDispatcher,
        networkServerFacade,
        networkPlayerCache,
        redisMessenger,
        datastore
    );

    contestTicketFacade = ContestTicketConfigurator.create(datastore);
    contestFacade = new ContestFacade(contestUserFacade);

    new ContestPlaceholder(contestUserFacade).register();

    LiteBukkitFactory.builder()
        .settings(settings -> settings
            .fallbackPrefix("contest")
            .nativePermissions(false)
        )
        .argument(Location.class, new LocationArgument())
        .argument(Player.class, new PlayerArgument(messagesService))
        .argument(NetworkPlayer.class,
            new NetworkPlayerArgument(messagesService, networkPlayerCache, networkServerFacade))
        .context(Player.class, new PlayerOnlyContextProvider(new MessageRegistry<>()))
        .result(CooldownState.class, new CooldownStateResultHandlerImpl(new MessageRegistry<>()))

        .missingPermission(new MissingPermissionHandlerImpl(messagesService))
        .invalidUsage(new InvalidUsageHandlerImpl(messagesService))
        .argumentSuggester(String.class, ArgumentKey.of("playerName"),
            (invocation, argument, context) -> Bukkit.getOnlinePlayers()
                .stream()
                .map(Player::getName)
                .collect(SuggestionResult.collector())
        )
        .schematicGenerator(SchematicFormat.angleBrackets())
        .commands(LiteCommandsAnnotations.of(
            new ContestCommand(flameDispatcher, contestFacade, contestTicketFacade),
            new ContestAdminCommand(contestFacade, contestTicketFacade)
        ))
        .build();

  }

  @Override
  public void onDisable() {
    INSTANCE = null;
  }

  public ContestUserFacade getContestUserFacade() {
    return contestUserFacade;
  }

  public ContestTicketFacade getContestTicketFacade() {
    return contestTicketFacade;
  }

  public ContestFacade getContestFacade() {
    return contestFacade;
  }
}