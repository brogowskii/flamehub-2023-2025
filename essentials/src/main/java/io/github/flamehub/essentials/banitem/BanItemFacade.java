package io.github.flamehub.essentials.banitem;

import io.github.flamehub.commons.config.FlameConfigService;
import java.util.List;
import org.bukkit.Material;

public final class BanItemFacade {

  private final BanItemConfig banItemConfig;

  public BanItemFacade(final BanItemConfig banItemConfig) {
    this.banItemConfig = banItemConfig;
  }

  List<Material> getMaterialsBreak() {
    return this.banItemConfig.getMaterialsBreak();
  }

  List<Material> getMaterialsPlace() {
    return this.banItemConfig.getMaterialsPlace();
  }

  List<Material> getCraftings() {
    return this.banItemConfig.getCraftings();
  }

  void saveConfig(FlameConfigService flameConfigService) {
    flameConfigService.saveLocally(BanItemConfig.class);
  }

  void refreshConfig(final FlameConfigService flameConfigService) throws IllegalAccessException {
    flameConfigService.refreshLocally(BanItemConfig.class);
  }

}
