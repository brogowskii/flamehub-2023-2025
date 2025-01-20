package io.github.flamehub.economy;

import dev.rollczi.litecommands.LiteCommandsBuilder;
import dev.rollczi.litecommands.argument.ArgumentKey;
import dev.rollczi.litecommands.bukkit.LiteBukkitFactory;
import dev.rollczi.litecommands.bukkit.LiteBukkitSettings;
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
import io.github.flamehub.commons.network.message.NetworkMessageService;
import io.github.flamehub.commons.network.player.NetworkPlayer;
import io.github.flamehub.economy.user.EconomyUserConfigurator;
import io.github.flamehub.economy.user.EconomyUserFacade;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.ServicePriority;
import org.bukkit.plugin.ServicesManager;

public final class EconomyModule extends BukkitModule {

  private NetworkMessageService networkMessageService;
  private EconomyUserFacade economyUserFacade;

  @Override
  public void onEnable() {
    super.onEnable();

    final LiteCommandsBuilder<CommandSender, LiteBukkitSettings, ?> liteCommandsBuilder = LiteBukkitFactory.builder()
        .settings(settings -> settings
            .fallbackPrefix("economy")
            .nativePermissions(false)
        )
        .argument(Location.class, new LocationArgument())
        .argument(Player.class, new PlayerArgument(messagesService))
        .argument(NetworkPlayer.class,
            new NetworkPlayerArgument(messagesService, networkPlayerCache,
                networkServerCache))
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
        .schematicGenerator(SchematicFormat.angleBrackets());

    this.networkMessageService = new NetworkMessageService(redisMessenger, "network_messages");

    final EconomyUserConfigurator economyUserConfigurator = new EconomyUserConfigurator();
    this.economyUserFacade = economyUserConfigurator.economyUserFacade(
        liteCommandsBuilder,
        messagesService,
        this,
        flameDispatcher,
        redisMessenger,
        networkServerCache,
        networkPlayerCache,
        databaseConnector.getMongoClient(),
        networkServerCache.getCurrent().getCategory(),
        networkServerCache.getCurrent().getName()
    );

    final EconomyFacade economyFacade = new EconomyConfigurator().economyFacade(
        liteCommandsBuilder,
        this,
        flameDispatcher,
        economyUserFacade,
        messagesService,
        networkMessageService
    );

    final ServicesManager servicesManager = getServer().getServicesManager();
    servicesManager.register(EconomyFacade.class, economyFacade, this, ServicePriority.Normal);

    liteCommandsBuilder.build();
  }

  @Override
  public void onDisable() {
    economyUserFacade.runSaveAll();
  }


}