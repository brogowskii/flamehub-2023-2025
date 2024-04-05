package io.github.flamehub.essentials.banitem;

import io.github.flamehub.commons.config.MongoConfig;
import org.bukkit.Material;

import java.util.ArrayList;
import java.util.List;

final class BanItemConfig extends MongoConfig {

    private List<Material> materialsBreak = new ArrayList<>();
    private List<Material> materialsPlace = new ArrayList<>();
    private List<Material> craftings = new ArrayList<>();

    BanItemConfig() {
    }

    BanItemConfig(final String id) {
        super(id);
    }

    List<Material> getMaterialsBreak() {
        return materialsBreak;
    }

    List<Material> getMaterialsPlace() {
        return materialsPlace;
    }

    List<Material> getCraftings() {
        return craftings;
    }
}
