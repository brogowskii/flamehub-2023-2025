package io.github.flamehub.mines;

import dev.rollczi.litecommands.annotations.LiteCommandsAnnotations;
import dev.rollczi.litecommands.bukkit.LiteBukkitFactory;
import dev.rollczi.litecommands.bukkit.context.PlayerOnlyContextProvider;
import dev.rollczi.litecommands.message.MessageRegistry;
import dev.rollczi.litecommands.schematic.SchematicFormat;
import io.github.flamehub.commons.bukkit.BukkitModule;
import io.github.flamehub.commons.bukkit.command.argument.PlayerArgument;
import io.github.flamehub.commons.bukkit.command.handler.InvalidUsageHandlerImpl;
import io.github.flamehub.commons.bukkit.command.handler.MissingPermissionHandlerImpl;
import io.github.flamehub.commons.bukkit.message.BukkitMessagesService;
import io.github.flamehub.commons.bukkit.util.ChunkUtil;
import io.github.flamehub.commons.config.FlameConfigService;
import io.github.flamehub.mines.mine.Mine;
import io.github.flamehub.mines.mine.MineArgument;
import io.github.flamehub.mines.mine.MineCommand;
import io.github.flamehub.mines.mine.MineConfig;
import io.github.flamehub.mines.mine.MinePlaceholder;
import io.github.flamehub.mines.mine.MineQueueTask;
import io.github.flamehub.mines.mine.MineTask;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitScheduler;

public class MinesPlugin extends BukkitModule {

  private static MinesPlugin instance;

  private MineConfig mineConfig;
  private MineQueueTask mineQueueTask;

  public static MinesPlugin getInstance() {
    return instance;
  }

  @Override
  public void onEnable() {
    instance = this;
    super.onEnable();

    this.messagesService = getService(BukkitMessagesService.class);
    this.flameConfigService = getService(FlameConfigService.class);
    this.mineConfig = this.flameConfigService.getOrCreate(this.getDataFolder(), MineConfig.class);
    this.mineConfig.getMinesById().values().forEach(Mine::createHolo);

    for (Mine generator : this.mineConfig.getMinesById().values()) {
      Location firstLocation = generator.getFirstLocation();
      Location secondLocation = generator.getSecondLocation();

      int minX = Math.min(firstLocation.getBlockX(), secondLocation.getBlockX());
      int maxX = Math.max(firstLocation.getBlockX(), secondLocation.getBlockX());
      int minY = Math.min(firstLocation.getBlockY(), secondLocation.getBlockY());
      int maxY = Math.max(firstLocation.getBlockY(), secondLocation.getBlockY());
      int minZ = Math.min(firstLocation.getBlockZ(), secondLocation.getBlockZ());
      int maxZ = Math.max(firstLocation.getBlockZ(), secondLocation.getBlockZ());

      for (int x = minX; x <= maxX; x++) {
        for (int y = minY; y <= maxY; y++) {
          for (int z = minZ; z <= maxZ; z++) {
            long l = ChunkUtil.coordinatesToLong(x, y, z);
            this.mineConfig.getMinesByLocation().put(l, generator);
          }
        }
      }
    }

    new MinePlaceholder(this.mineConfig).register();

    setupTasks();
    setupCommands();
  }

  @Override
  public void onDisable() {
    instance = null;
  }

  void setupTasks() {
    BukkitScheduler scheduler = this.getServer().getScheduler();
    this.mineQueueTask = new MineQueueTask();
    scheduler.runTaskTimer(this, this.mineQueueTask, 3L, 3L);
    scheduler.runTaskTimerAsynchronously(this, new MineTask(mineConfig, mineQueueTask), 0L, 20L);

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
            new MineCommand(this.flameConfigService, this.mineConfig, this)
        ))

        .schematicGenerator(SchematicFormat.angleBrackets())
        .build();
  }

  public MineConfig getMineConfig() {
    return mineConfig;
  }

  public MineQueueTask getMineQueueTask() {
    return mineQueueTask;
  }
}