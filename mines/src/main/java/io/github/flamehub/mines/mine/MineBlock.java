package io.github.flamehub.mines.mine;

import org.bukkit.Material;

import java.io.Serializable;

public final class MineBlock implements Serializable {

    private double chance;
    private Material material;

    public MineBlock() {
    }

    public MineBlock(double chance, Material material) {
        this.chance = chance;
        this.material = material;
    }

    public double getChance() {
        return chance;
    }

    public Material getMaterial() {
        return material;
    }
}