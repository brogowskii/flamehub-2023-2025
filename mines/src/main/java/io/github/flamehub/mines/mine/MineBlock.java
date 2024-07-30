package io.github.flamehub.mines.mine;

import java.io.Serializable;
import org.bukkit.Material;

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