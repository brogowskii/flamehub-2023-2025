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
import io.github.flamehub.tiktok.shop.TikTokShopConfig;
import io.github.flamehub.tiktok.user.TikTokUser;
import io.github.flamehub.tiktok.user.TikTokUserArgument;
import io.github.flamehub.tiktok.user.TikTokUserCache;
import io.github.flamehub.tiktok.user.TikTokUserContextual;
import io.github.flamehub.tiktok.user.TikTokUserFactory;
import io.github.flamehub.tiktok.user.TikTokUserListener;
import io.github.flamehub.tiktok.user.TikTokUserRepository;
import io.github.flamehub.tiktok.user.TikTokUserSaver;
import io.github.flamehub.tiktok.video.verify.TikTokVideoVerify;
import io.github.flamehub.tiktok.video.verify.TikTokVideoVerifyCache;
import io.github.flamehub.tiktok.video.verify.TikTokVideoVerifyCommand;
import io.github.flamehub.tiktok.video.verify.TikTokVideoVerifyHandler;
import io.github.flamehub.tiktok.video.verify.TikTokVideoVerifyRepository;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.scheduler.BukkitScheduler;

public final class TikTokPlugin extends BukkitModule {

  private TikTokUserSaver tikTokUserSaver;
  private TikTokUserCache tikTokUserCache;
  private TikTokUserFactory tikTokUserFactory;
  private TikTokUserRepository tikTokUserRepository;

  private TikTokService tikTokService;
  private TikTokVideoVerifyCache tikTokVideoVerifyCache;
  private TikTokVideoVerifyRepository tikTokVideoVerifyRepository;

  private TikTokShopConfig tikTokShopConfig;

  @Override
  public void onEnable() {
    super.onEnable();

    tikTokShopConfig = flameConfigService.getOrCreate(getDataFolder(), TikTokShopConfig.class);

    tikTokUserRepository = new TikTokUserRepository(
        DatastoreFactory.create(super.databaseConnector.getMongoClient(), "global",
            TikTokUser.class));
    tikTokUserFactory = new TikTokUserFactory();
    tikTokUserCache = new TikTokUserCache(tikTokUserRepository);
    tikTokService = new TikTokService();
    tikTokUserSaver = new TikTokUserSaver(tikTokUserRepository, tikTokUserCache);

    tikTokVideoVerifyCache = new TikTokVideoVerifyCache();
    tikTokVideoVerifyRepository = new TikTokVideoVerifyRepository(
        DatastoreFactory.create(super.databaseConnector.getMongoClient(), "global",
            TikTokVideoVerify.class));

    tikTokVideoVerifyRepository.loadAll()
        .forEach(tikTokVideoVerify -> tikTokVideoVerifyCache.add(tikTokVideoVerify.getId(),
            tikTokVideoVerify));

    redisMessenger.subscribe("tiktok-verify", new TikTokVideoVerifyHandler(tikTokVideoVerifyCache));

    final BukkitScheduler scheduler = getServer().getScheduler();
    scheduler.runTaskTimerAsynchronously(this, tikTokUserSaver, 0L, 20 * 120L);

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
            new TikTokCommand(this, redisMessenger, tikTokService, tikTokUserRepository,
                tikTokShopConfig, flameDispatcher, tikTokVideoVerifyRepository,
                tikTokVideoVerifyCache),
            new TikTokVideoVerifyCommand(redisMessenger, tikTokVideoVerifyCache,
                tikTokVideoVerifyRepository),
            new TikTokCommandAdmin(flameConfigService)
        ))

        .schematicGenerator(SchematicFormat.angleBrackets())
        .build();


  }

  @Override
  public void onDisable() {
    tikTokUserSaver.run();
  }
}
