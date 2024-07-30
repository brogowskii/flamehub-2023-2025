package io.github.flamehub.kits.kit;


import com.fasterxml.jackson.annotation.JsonIgnore;
import io.github.flamehub.commons.util.TimeUtil;
import java.io.Serializable;
import java.time.Duration;
import java.util.List;
import org.bukkit.inventory.ItemStack;

public final class Kit implements Serializable {

  private String name;
  private List<String> lore;
  private String guiName;
  private String permission;

  private String title;
  private int slot;
  private ItemStack icon;
  private List<ItemStack> items;

  private String cooldown;
  private boolean enable;

  public Kit() {

  }

  public Kit(String name, List<String> lore, String guiName, String permission, String title,
      int slot, ItemStack icon, List<ItemStack> items, String cooldown, boolean enable) {
    this.name = name;
    this.lore = lore;
    this.guiName = guiName;
    this.permission = permission;
    this.title = title;
    this.slot = slot;
    this.icon = icon;
    this.items = items;
    this.cooldown = cooldown;
    this.enable = enable;
  }

  @JsonIgnore
  public Duration getCooldownDuration() {
    return TimeUtil.parseTime(this.cooldown);
  }

  public String getCooldown() {
    return cooldown;
  }

  public void setCooldown(String cooldown) {
    this.cooldown = cooldown;
  }

  public List<String> getLore() {
    return lore;
  }

  public String getName() {
    return name;
  }

  public String getGuiName() {
    return guiName;
  }

  public String getPermission() {
    return permission;
  }

  public void setPermission(String permission) {
    this.permission = permission;
  }

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public int getSlot() {
    return slot;
  }

  public void setSlot(int slot) {
    this.slot = slot;
  }

  public ItemStack getIcon() {
    return icon;
  }

  public void setIcon(ItemStack icon) {
    this.icon = icon;
  }

  public List<ItemStack> getItems() {
    return items;
  }

  public boolean isEnable() {
    return enable;
  }

  public void setEnable(boolean enable) {
    this.enable = enable;
  }
}
