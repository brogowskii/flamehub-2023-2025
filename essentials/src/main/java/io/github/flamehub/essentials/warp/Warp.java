package io.github.flamehub.essentials.warp;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;
import org.bukkit.Location;
import org.bukkit.Material;

final class Warp implements Serializable {

  private String name;
  private String guiName;
  private Material guiIcon;
  private List<String> guiLore;
  private int guiSlot;
  private int teleportSeconds;
  private Location location;

  Warp() {
  }

  Warp(String name, final String guiName, final Material guiIcon, final int guiSlot,
      final Location location) {
    this.name = name;
    this.guiName = guiName;
    this.guiIcon = guiIcon;
    this.guiLore = Arrays.asList("line 1", "line 2");
    this.guiSlot = guiSlot;
    this.teleportSeconds = 5;
    this.location = location;
  }

  public String getName() {
    return name;
  }

  public String getGuiName() {
    return guiName;
  }

  public Material getGuiIcon() {
    return guiIcon;
  }

  public List<String> getGuiLore() {
    return guiLore;
  }

  public int getGuiSlot() {
    return guiSlot;
  }

  public int getTeleportSeconds() {
    return teleportSeconds;
  }

  public Location getLocation() {
    return location;
  }

  public void setLocation(Location location) {
    this.location = location;
  }

}
