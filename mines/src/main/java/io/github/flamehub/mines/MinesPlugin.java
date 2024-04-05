package io.github.flamehub.mines;

import dev.rollczi.litecommands.annotations.LiteCommandsAnnotations;
import dev.rollczi.litecommands.bukkit.LiteBukkitFactory;
import dev.rollczi.litecommands.bukkit.context.PlayerOnlyContextProvider;
import dev.rollczi.litecommands.message.MessageRegistry;
import dev.rollczi.litecommands.schematic.SchematicFormat;
import io.github.flamehub.commons.bukkit.BukkitPlugin;
import io.github.flamehub.commons.bukkit.command.argument.PlayerArgument;
import io.github.flamehub.commons.bukkit.command.handler.InvalidUsageHandlerImpl;
import io.github.flamehub.commons.bukkit.command.handler.MissingPermissionHandlerImpl;
import io.github.flamehub.commons.bukkit.message.BukkitMessagesService;
import io.github.flamehub.commons.config.MongoConfigService;
import io.github.flamehub.mines.mine.*;
import org.bukkit.entity.Player;

public class MinesPlugin extends BukkitPlugin {

    private BukkitMessagesService messagesService;
    private MongoConfigService mongoConfigService;
    private MineConfig mineConfig;

    private MineQueueRunnable mineQueueRunnable;

    @Override
    public void onEnable() {
        this.messagesService = getService(BukkitMessagesService.class);
        this.mongoConfigService = getService(MongoConfigService.class);
        this.mineConfig = this.mongoConfigService.findOrCreate(MineConfig.class, "mines", MineConfig::new);

        this.mineQueueRunnable = new MineQueueRunnable();

        new MinePlaceholder(this.mineConfig).register();

        setupTasks();
        setupCommands();
    }

    void setupTasks() {
        this.getServer().getScheduler().runTaskTimer(this, new MineTask(this.mineConfig, mineQueueRunnable), 0L, 40L);
    }

    void setupCommands() {
        LiteBukkitFactory.builder()
                .settings(settings -> settings
                        .fallbackPrefix("flamehub-mines")
                        .nativePermissions(false)
                )
                .argument(Player.class, new PlayerArgument(this.messagesService))
                .argument(Mine.class, new MineArgument(this.mineConfig))

                .context(Player.class, new PlayerOnlyContextProvider(new MessageRegistry<>()))

                .missingPermission(new MissingPermissionHandlerImpl(this.messagesService))
                .invalidUsage(new InvalidUsageHandlerImpl(this.messagesService))

                .commands(LiteCommandsAnnotations.of(
                        new MineCommand(this.mongoConfigService, this.mineConfig, this)
                ))

                .schematicGenerator(SchematicFormat.angleBrackets())
                .build();
    }



}