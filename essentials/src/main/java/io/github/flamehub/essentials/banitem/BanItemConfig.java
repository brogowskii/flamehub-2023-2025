package io.github.flamehub.essentials.banitem;

import io.github.flamehub.commons.legacy.config.MongoConfig;
import org.bukkit.Material;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public final class BanItemConfig extends MongoConfig {

    private List<Material> materialsBreak = new ArrayList<>(Arrays.asList(Material.BEDROCK));
    private List<Material> materialsPlace = new ArrayList<>(Arrays.asList(Material.BEDROCK));
    private List<Material> craftings = new ArrayList<>(Arrays.asList(Material.BEDROCK));

    public BanItemConfig() {
    }

    public BanItemConfig(final String id) {
        super(id);
    }

    public List<Material> getMaterialsBreak() {
        return materialsBreak;
    }

    public List<Material> getMaterialsPlace() {
        return materialsPlace;
    }

    public List<Material> getCraftings() {
        return craftings;
    }
}
