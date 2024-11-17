package io.github.flamehub.lobby.selector;

import java.io.Serializable;
import java.util.List;
import org.bukkit.Material;

public final class ServerSelectorItem implements Serializable {

  private Material icon;
  private String name;
  private List<String> lore;
  private int slot;

  public ServerSelectorItem(Material icon, String name, List<String> lore, int slot) {
    this.icon = icon;
    this.name = name;
    this.lore = lore;
    this.slot = slot;
  }

  public ServerSelectorItem() {
  }

  public Material getIcon() {
    return icon;
  }

  public List<String> getLore() {
    return lore;
  }

  public String getName() {
    return name;
  }

  public int getSlot() {
    return slot;
  }
}
