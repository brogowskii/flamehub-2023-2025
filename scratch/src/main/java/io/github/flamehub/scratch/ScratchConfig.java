package io.github.flamehub.scratch;

import eu.okaeri.configs.OkaeriConfig;
import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public final class ScratchConfig extends OkaeriConfig {

    private boolean enabled = true;

    private ItemStack scratchCardItem;

    private Location previewLocation;

    private Map<Integer, ScratchDrop> scratchCardDrops = new HashMap<>();

    public ScratchDrop random() {
        if (scratchCardDrops.isEmpty()) {
            return null;
        }

        List<ScratchDrop> values = new ArrayList<>(values());
        double totalChances = values.stream()
                .mapToDouble(ScratchDrop::getChance)
                .sum();
        double randomValue = Math.random() * totalChances;

        for (ScratchDrop scratchDrop : values) {
            randomValue -= scratchDrop.getChance();
            if (randomValue <= 0) {
                return scratchDrop;
            }
        }

        return values.get(values.size() - 1);
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public void setScratchCardItem(ItemStack scratchCardItem) {
        this.scratchCardItem = scratchCardItem;
    }

    public void setPreviewLocation(Location previewLocation) {
        this.previewLocation = previewLocation;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public ItemStack getScratchCardItem() {
        return scratchCardItem;
    }

    public Location getPreviewLocation() {
        return previewLocation;
    }

    public Collection<ScratchDrop> values() {
        return scratchCardDrops.values();
    }

    public Map<Integer, ScratchDrop> getScratchCardDrops() {
        return scratchCardDrops;
    }
}
