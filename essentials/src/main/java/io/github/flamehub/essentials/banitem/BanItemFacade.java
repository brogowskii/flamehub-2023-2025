package io.github.flamehub.essentials.banitem;

import io.github.flamehub.commons.legacy.config.MongoConfigService;
import org.bukkit.Material;

import java.util.List;

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

    void saveConfig(MongoConfigService mongoConfigService) {
        mongoConfigService.save(this.banItemConfig);
    }

    void refreshConfig(final MongoConfigService mongoConfigService) throws IllegalAccessException {
        mongoConfigService.refresh(BanItemConfig.class, this.banItemConfig);
    }

}
