package io.github.flamehub.tiktok;

import dev.rollczi.litecommands.annotations.LiteCommandsAnnotations;
import dev.rollczi.litecommands.bukkit.LiteBukkitFactory;
import dev.rollczi.litecommands.bukkit.context.PlayerOnlyContextProvider;
import dev.rollczi.litecommands.message.MessageRegistry;
import dev.rollczi.litecommands.schematic.SchematicFormat;
import io.github.flamehub.commons.bukkit.BukkitModule;
import io.github.flamehub.commons.bukkit.command.argument.LocationArgument;
import io.github.flamehub.commons.bukkit.command.argument.PlayerArgument;
import io.github.flamehub.commons.bukkit.command.handler.InvalidUsageHandlerImpl;
import io.github.flamehub.commons.bukkit.command.handler.MissingPermissionHandlerImpl;
import io.github.flamehub.commons.database.DatastoreFactory;
import io.github.flamehub.tiktok.user.TikTokUser;
import io.github.flamehub.tiktok.user.TikTokUserArgument;
import io.github.flamehub.tiktok.user.TikTokUserCache;
import io.github.flamehub.tiktok.user.TikTokUserContextual;
import io.github.flamehub.tiktok.user.TikTokUserFactory;
import io.github.flamehub.tiktok.user.TikTokUserListener;
import io.github.flamehub.tiktok.user.TikTokUserRepository;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;

public final class TikTokPlugin extends BukkitModule {

  private TikTokUserCache tikTokUserCache;
  private TikTokUserFactory tikTokUserFactory;
  private TikTokUserRepository tikTokUserRepository;

  private TikTokService tikTokService;

  @Override
  public void onEnable() {
    super.onEnable();

    tikTokUserRepository = new TikTokUserRepository(
        DatastoreFactory.create(super.databaseConnector.getMongoClient(), "global",
            TikTokUser.class));
    tikTokUserFactory = new TikTokUserFactory();
    tikTokUserCache = new TikTokUserCache(tikTokUserRepository);
    tikTokService = new TikTokService();

    final PluginManager pluginManager = getServer().getPluginManager();
    pluginManager.registerEvents(new TikTokUserListener(
        flameDispatcher, pluginManager, tikTokUserCache, tikTokUserRepository, tikTokUserFactory
    ), this);

    LiteBukkitFactory.builder()
        .settings(settings -> settings
            .fallbackPrefix("tiktok")
            .nativePermissions(false)
        )
        .argument(Location.class, new LocationArgument())
        .argument(Player.class, new PlayerArgument(messagesService))
        .argument(TikTokUser.class, new TikTokUserArgument(tikTokUserCache, messagesService))
        .context(Player.class, new PlayerOnlyContextProvider(new MessageRegistry<>()))
        .context(TikTokUser.class, new TikTokUserContextual(tikTokUserCache))

        .missingPermission(new MissingPermissionHandlerImpl(messagesService))
        .invalidUsage(new InvalidUsageHandlerImpl(messagesService))

        .commands(LiteCommandsAnnotations.of(
            new TikTokCommand(tikTokService, flameDispatcher)
        ))

        .schematicGenerator(SchematicFormat.angleBrackets())
        .build();


  }
}
