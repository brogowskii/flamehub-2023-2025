package io.github.flamehub.missions;

import dev.rollczi.litecommands.annotations.LiteCommandsAnnotations;
import dev.rollczi.litecommands.bukkit.LiteBukkitFactory;
import dev.rollczi.litecommands.bukkit.context.PlayerOnlyContextProvider;
import dev.rollczi.litecommands.message.MessageRegistry;
import dev.rollczi.litecommands.schematic.SchematicFormat;
import io.github.flamehub.commons.bukkit.BukkitModule;
import io.github.flamehub.commons.bukkit.command.argument.LocationArgument;
import io.github.flamehub.commons.bukkit.command.argument.WorldArgument;
import io.github.flamehub.commons.bukkit.command.handler.InvalidUsageHandlerImpl;
import io.github.flamehub.commons.bukkit.command.handler.MissingPermissionHandlerImpl;
import io.github.flamehub.commons.database.DatastoreFactory;
import io.github.flamehub.missions.user.MissionUser;
import io.github.flamehub.missions.user.MissionUserCache;
import io.github.flamehub.missions.user.MissionUserFactory;
import io.github.flamehub.missions.user.MissionUserListener;
import io.github.flamehub.missions.user.MissionUserRepository;
import io.github.flamehub.missions.user.MissionUserSaver;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.scheduler.BukkitScheduler;

public final class MissionsPlugin extends BukkitModule {

  private MissionUserCache missionUserCache;
  private MissionUserRepository missionUserRepository;
  private MissionUserFactory missionUserFactory;
  private MissionUserSaver missionUserSaver;

  @Override
  public void onEnable() {
    super.onEnable();

    this.missionUserFactory = new MissionUserFactory();
    this.missionUserRepository = new MissionUserRepository(
        DatastoreFactory.create(databaseConnector.getMongoClient(),
            networkServerCache.getCurrent().getCategory(), MissionUser.class, Mission.class));
    this.missionUserCache = new MissionUserCache(missionUserRepository);
    this.missionUserSaver = new MissionUserSaver(missionUserRepository, missionUserCache);

    PluginManager pluginManager = getServer().getPluginManager();
    pluginManager.registerEvents(
        new MissionUserListener(flameDispatcher, pluginManager, missionUserCache,
            missionUserRepository, missionUserFactory), this);
    pluginManager.registerEvents(new MissionListener(missionUserCache), this);

    BukkitScheduler scheduler = getServer().getScheduler();
    scheduler.runTaskTimerAsynchronously(this, missionUserSaver, 0, 20 * 120);

    LiteBukkitFactory.builder()
        .settings(settings -> settings
            .fallbackPrefix("flamehub-missions")
            .nativePermissions(false)
        )
        .argument(Location.class, new LocationArgument())
        .argument(World.class, new WorldArgument())

        .context(Player.class, new PlayerOnlyContextProvider(new MessageRegistry<>()))

        .missingPermission(new MissingPermissionHandlerImpl(messagesService))
        .invalidUsage(new InvalidUsageHandlerImpl(messagesService))

        .commands(LiteCommandsAnnotations.of(
            new MissionCommand(flameDispatcher, missionUserCache,
                missionUserRepository)
        ))

        .schematicGenerator(SchematicFormat.angleBrackets())
        .build();

  }

  @Override
  public void onDisable() {
    missionUserSaver.run();
  }

  public MissionUserCache getMissionUserCache() {
    return missionUserCache;
  }
}