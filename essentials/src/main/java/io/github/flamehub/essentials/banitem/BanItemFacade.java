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
    return banItemConfig.getMaterialsBreak();
  }

  List<Material> getMaterialsPlace() {
    return banItemConfig.getMaterialsPlace();
  }

  List<Material> getCraftings() {
    return banItemConfig.getCraftings();
  }

  void saveConfig(FlameConfigService flameConfigService) {
    flameConfigService.save(BanItemConfig.class);
  }

  void refreshConfig(final FlameConfigService flameConfigService) throws IllegalAccessException {
    flameConfigService.refresh(BanItemConfig.class);
  }

}
