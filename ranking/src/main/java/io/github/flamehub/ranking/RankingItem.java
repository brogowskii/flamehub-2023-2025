package io.github.flamehub.ranking;

import org.bukkit.Material;

import java.io.Serializable;
import java.util.List;

public final class RankingItem implements Serializable {

    private final String name;
    private final String template;
    private final List<String> additionalLore;
    private final int slot;
    private final Material material;

    public RankingItem(String name, String template, List<String> additionalLore, int slot, Material material) {
        this.name = name;
        this.template = template;
        this.additionalLore = additionalLore;
        this.slot = slot;
        this.material = material;
    }

    public String getName() {
        return name;
    }

    public String getTemplate() {
        return template;
    }

    public List<String> getAdditionalLore() {
        return additionalLore;
    }

    public int getSlot() {
        return slot;
    }

    public Material getMaterial() {
        return material;
    }
}
