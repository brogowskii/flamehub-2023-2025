package io.github.flamehub.ranking;

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
import io.github.flamehub.ranking.gui.RankingGui;
import io.github.flamehub.ranking.gui.RankingGuiCache;
import io.github.flamehub.ranking.info.RankingInfoCache;
import java.util.stream.Collectors;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitScheduler;

public final class RankingPlugin extends BukkitModule {

  private RankingConfig rankingConfig;
  private RankingInfoCache rankingInfoCache;
  private RankingRepository rankingRepository;
  private RankingCache rankingCache;
  private RankingGuiCache rankingGuiCache;
  private RankingRefresher rankingRefresher;

  @Override
  public void onEnable() {
    super.onEnable();

    this.rankingConfig = flameConfigService.getOrCreate(getDataFolder(),
        RankingConfig.class);

    this.rankingRepository = new RankingRepository(databaseConnector);
    this.rankingInfoCache = new RankingInfoCache();
    this.rankingCache = new RankingCache(rankingRepository, rankingInfoCache);
    this.rankingGuiCache = new RankingGuiCache();
    this.rankingRefresher = new RankingRefresher(rankingInfoCache, rankingCache);
    loadRankings();

    BukkitScheduler scheduler = getServer().getScheduler();
    scheduler.runTaskTimerAsynchronously(this, rankingRefresher, 0L, 20 * 30L);

    LiteBukkitFactory.builder()
        .settings(settings -> settings
            .fallbackPrefix("flamehub-ranking")
            .nativePermissions(false)
        )
        .argument(Location.class, new LocationArgument())
        .argument(World.class, new WorldArgument())

        .context(Player.class, new PlayerOnlyContextProvider(new MessageRegistry<>()))

        .missingPermission(new MissingPermissionHandlerImpl(messagesService))
        .invalidUsage(new InvalidUsageHandlerImpl(messagesService))

        .commands(LiteCommandsAnnotations.of(
            new RankingCommand(rankingGuiCache, this)
        ))

        .schematicGenerator(SchematicFormat.angleBrackets())
        .build();

    new RankingPlaceholder(rankingCache).register();

  }

  public void loadRankings() {
    try {
      flameConfigService.refreshLocally(RankingConfig.class);
    } catch (IllegalAccessException e) {
      throw new RuntimeException(e);
    }

    rankingConfig.getRankingInfoList().forEach(rankingInfo -> {
      rankingInfoCache.addType(rankingInfo);
      rankingInfoCache.addType(rankingConfig.getPlayedTime());
    });
    rankingCache.setup();
    rankingConfig.getRankingGuiList().forEach(wrapper -> {
      RankingGui rankingGui = new RankingGui(wrapper, rankingCache.values().stream()
          .filter(rankingWrapper -> rankingWrapper.getInfo().getGuiId().equals(wrapper.getId()))
          .collect(Collectors.toList()));
      rankingGuiCache.add(rankingGui);
    });
    rankingRefresher.run();

  }
}
