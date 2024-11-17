package io.github.flamehub.tiktok.shop;

import java.util.List;
import org.bukkit.Material;

public final class TikTokShopItem {

  private int price;
  private List<String> commands;
  private Material guiIcon;
  private String guiName;
  private List<String> guiLore;
  private int slot;

  public TikTokShopItem(final int price, final List<String> commands, final Material guiIcon,
      final String guiName,
      final List<String> guiLore, final int slot) {
    this.price = price;
    this.commands = commands;
    this.guiIcon = guiIcon;
    this.guiName = guiName;
    this.guiLore = guiLore;
    this.slot = slot;
  }

  public TikTokShopItem() {
  }

  public int getPrice() {
    return price;
  }

  public List<String> getCommands() {
    return commands;
  }

  public Material getGuiIcon() {
    return guiIcon;
  }

  public String getGuiName() {
    return guiName;
  }

  public List<String> getGuiLore() {
    return guiLore;
  }

  public int getSlot() {
    return slot;
  }
}
