package io.github.flamehub.report;

import org.bukkit.Material;

public enum ReportType {

  KILL_AURA("&c&lKill Aura", Material.DIAMOND_SWORD, 20),
  NUKER("&3&lNuker/FastBreak", Material.DIAMOND_PICKAXE, 21),
  REACH("&6&lReach/Hitboxy", Material.FISHING_ROD, 22),
  CLEAN_CUT("&f&lClean Cut (Bicie przez cobweby)", Material.COBWEB, 23),
  OTHER("&b&lInne", Material.FEATHER, 24),
  FAST_REPORT("Fast Report", Material.AIR, -1),
  ;

  private final String name;
  private final Material material;
  private final int slot;

  ReportType(final String name, final Material material, final int slot) {
    this.name = name;
    this.material = material;
    this.slot = slot;
  }

  public String getName() {
    return name;
  }

  public Material getMaterial() {
    return material;
  }

  public int getSlot() {
    return slot;
  }
}
