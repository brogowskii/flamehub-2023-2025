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

  public Kit(
      final String name,
      final List<String> lore,
      final String guiName,
      final String permission,
      final String title,
      final int slot,
      final ItemStack icon,
      final List<ItemStack> items,
      final String cooldown,
      final boolean enable
  ) {
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
    return TimeUtil.parseTime(cooldown);
  }

  public String getCooldown() {
    return cooldown;
  }

  public void setCooldown(final String cooldown) {
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

  public void setPermission(final String permission) {
    this.permission = permission;
  }

  public String getTitle() {
    return title;
  }

  public void setTitle(final String title) {
    this.title = title;
  }

  public int getSlot() {
    return slot;
  }

  public void setSlot(final int slot) {
    this.slot = slot;
  }

  public ItemStack getIcon() {
    return icon;
  }

  public void setIcon(final ItemStack icon) {
    this.icon = icon;
  }

  public List<ItemStack> getItems() {
    return items;
  }

  public boolean isEnable() {
    return enable;
  }

  public void setEnable(final boolean enable) {
    this.enable = enable;
  }
}
