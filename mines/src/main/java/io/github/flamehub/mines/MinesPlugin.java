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

    messagesService = getService(BukkitMessagesService.class);
    flameConfigService = getService(FlameConfigService.class);
    mineConfig = flameConfigService.getOrCreate(MineConfig.class);
    mineConfig.getMinesById().values().forEach(Mine::createHolo);

    for (final Mine generator : mineConfig.getMinesById().values()) {
      final Location firstLocation = generator.getFirstLocation();
      final Location secondLocation = generator.getSecondLocation();

      final int minX = Math.min(firstLocation.getBlockX(), secondLocation.getBlockX());
      final int maxX = Math.max(firstLocation.getBlockX(), secondLocation.getBlockX());
      final int minY = Math.min(firstLocation.getBlockY(), secondLocation.getBlockY());
      final int maxY = Math.max(firstLocation.getBlockY(), secondLocation.getBlockY());
      final int minZ = Math.min(firstLocation.getBlockZ(), secondLocation.getBlockZ());
      final int maxZ = Math.max(firstLocation.getBlockZ(), secondLocation.getBlockZ());

      for (int x = minX; x <= maxX; x++) {
        for (int y = minY; y <= maxY; y++) {
          for (int z = minZ; z <= maxZ; z++) {
            final long l = ChunkUtil.coordinatesToLong(x, y, z);
            mineConfig.getMinesByLocation().put(l, generator);
          }
        }
      }
    }
    flameConfigService.save(MineConfig.class);

    new MinePlaceholder(mineConfig).register();

    setupTasks();
    setupCommands();
  }

  @Override
  public void onDisable() {
    instance = null;
  }

  void setupTasks() {
    final BukkitScheduler scheduler = getServer().getScheduler();
    mineQueueTask = new MineQueueTask();
    scheduler.runTaskTimer(this, mineQueueTask, 3L, 3L);
    scheduler.runTaskTimerAsynchronously(this, new MineTask(mineConfig, mineQueueTask), 0L, 20L);

  }

  void setupCommands() {
    LiteBukkitFactory.builder()
        .settings(settings -> settings
            .fallbackPrefix("flamehub-mines")
            .nativePermissions(false)
        )
        .argument(Player.class, new PlayerArgument(messagesService))
        .argument(Mine.class, new MineArgument(mineConfig))

        .context(Player.class, new PlayerOnlyContextProvider(new MessageRegistry<>()))

        .missingPermission(new MissingPermissionHandlerImpl(messagesService))
        .invalidUsage(new InvalidUsageHandlerImpl(messagesService))

        .commands(LiteCommandsAnnotations.of(
            new MineCommand(flameConfigService, mineConfig, this)
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