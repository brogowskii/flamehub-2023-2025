package io.github.flamehub.worldloader;

import io.github.flamehub.commons.bukkit.BukkitModule;
import org.bukkit.GameRule;
import org.bukkit.World;
import org.bukkit.WorldCreator;

public final class WorldLoaderPlugin extends BukkitModule {

  @Override
  public void onEnable() {
    super.onEnable();

    final WorldLoaderConfig worldLoaderConfig = flameConfigService.getOrCreate(WorldLoaderConfig.class);
    for (final WorldLoader worldLoader : worldLoaderConfig.getWorldLoaders()) {
      final WorldCreator worldCreator = new WorldCreator(worldLoader.getName());
      if (worldLoader.getGenerator() != null && !worldLoader.getGenerator().isEmpty()) {
        worldCreator.generator(worldLoader.getGenerator());
      }

      if (worldLoader.getEnvironment() != null) {
        worldCreator.environment(worldLoader.getEnvironment());
      }

      final World world = worldCreator.createWorld();
      world.setGameRule(GameRule.ANNOUNCE_ADVANCEMENTS, Boolean.FALSE);
      world.setGameRule(GameRule.DISABLE_RAIDS, Boolean.TRUE);
      world.setGameRule(GameRule.DO_WEATHER_CYCLE, Boolean.FALSE);
      world.setGameRule(GameRule.DO_DAYLIGHT_CYCLE, Boolean.TRUE);
    }
  }
}