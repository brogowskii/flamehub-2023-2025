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
import io.github.flamehub.missions.user.*;
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
        this.missionUserRepository = new MissionUserRepository(DatastoreFactory.create(this.databaseConnector.getMongoClient(), this.networkServerCache.getCurrent().getCategory(), MissionUser.class));
        this.missionUserCache = new MissionUserCache(this.missionUserRepository);
        this.missionUserSaver = new MissionUserSaver(this.missionUserRepository, this.missionUserCache);

        PluginManager pluginManager = this.getServer().getPluginManager();
        pluginManager.registerEvents(new MissionUserListener(this.flameDispatcher, pluginManager, this.missionUserCache, this.missionUserRepository, this.missionUserFactory), this);
        pluginManager.registerEvents(new MissionListener(this.missionUserCache), this);

        BukkitScheduler scheduler = this.getServer().getScheduler();
        scheduler.runTaskTimerAsynchronously(this, this.missionUserSaver, 0, 20 * 120);

        LiteBukkitFactory.builder()
                .settings(settings -> settings
                        .fallbackPrefix("flamehub-missions")
                        .nativePermissions(false)
                )
                .argument(Location.class, new LocationArgument())
                .argument(World.class, new WorldArgument())

                .context(Player.class, new PlayerOnlyContextProvider(new MessageRegistry<>()))

                .missingPermission(new MissingPermissionHandlerImpl(this.messagesService))
                .invalidUsage(new InvalidUsageHandlerImpl(this.messagesService))

                .commands(LiteCommandsAnnotations.of(
                        new MissionCommand(this.flameDispatcher, this.missionUserCache, this.missionUserRepository)
                ))

                .schematicGenerator(SchematicFormat.angleBrackets())
                .build();

    }

    @Override
    public void onDisable() {
        this.missionUserSaver.run();
    }

    public MissionUserCache getMissionUserCache() {
        return missionUserCache;
    }
}