package io.github.flamehub.commons.bukkit.util;

import dev.triumphteam.gui.components.GuiAction;
import dev.triumphteam.gui.guis.GuiItem;
import io.github.flamehub.commons.bukkit.text.TextUtil;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public final class FlameItemBuilder {

  private final ItemStack itemStack;
  private final ItemMeta itemMeta;

  private FlameItemBuilder(Material material, int amount) {
    this.itemStack = new ItemStack(material, amount);
    this.itemMeta = itemStack.getItemMeta();
  }

  private FlameItemBuilder(ItemStack itemStack) {
    this.itemStack = itemStack;
    this.itemMeta = itemStack.getItemMeta();
  }

  public static FlameItemBuilder of(Material material) {
    return new FlameItemBuilder(material, 1);
  }

  public static FlameItemBuilder of(Material material, int amount) {
    return new FlameItemBuilder(material, amount);
  }

  public static FlameItemBuilder of(ItemStack item) {
    return new FlameItemBuilder(item);
  }

  public void refreshMeta() {
    itemStack.setItemMeta(itemMeta);
  }

  public FlameItemBuilder name(String name) {
    itemMeta.displayName(TextUtil.parse(name));
    refreshMeta();

    return this;
  }

  public FlameItemBuilder lore(List<String> lore) {
    itemMeta.lore(TextUtil.parse(lore));
    refreshMeta();

    return this;
  }

  public FlameItemBuilder lore(String... lore) {
    return lore(Arrays.asList(lore));
  }

  public FlameItemBuilder appendLore(List<String> lore) {
    ItemMeta itemMeta = this.itemMeta;
    if (!itemMeta.hasLore()) {
      itemMeta.lore(TextUtil.parse(lore));
    } else {
      List<Component> newLore = itemMeta.lore();
      newLore.addAll(TextUtil.parse(lore));
      itemMeta.lore(newLore);
    }

    refreshMeta();
    return this;
  }

  public FlameItemBuilder appendLore(String lore) {
    return appendLore(Collections.singletonList(lore));
  }

  public FlameItemBuilder appendLore(String... lore) {
    return appendLore(Arrays.asList(lore));
  }

  public FlameItemBuilder enchantment(Enchantment enchant, int level) {
    itemMeta.addEnchant(enchant, level, true);
    refreshMeta();

    return this;
  }

  public FlameItemBuilder flag(ItemFlag flag) {
    itemMeta.addItemFlags(flag);
    refreshMeta();

    return this;
  }

  public FlameItemBuilder amount(int amount) {
    itemStack.setAmount(amount);
    return this;
  }

  public FlameItemBuilder glow() {
    return glow(true);
  }

  public FlameItemBuilder glow(boolean glow) {
    if (glow) {
      itemMeta.addEnchant(Enchantment.LURE, 1, false);
      itemMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
    } else {

      for (Enchantment enchantment : itemMeta.getEnchants().keySet()) {
        itemMeta.removeEnchant(enchantment);
      }

    }
    refreshMeta();
    return this;
  }

  public ItemMeta getMeta() {
    return itemMeta;
  }

  public FlameItemBuilder customModelData(int data) {
    itemMeta.setCustomModelData(data);
    refreshMeta();
    return this;
  }

  public ItemStack asItemStack() {
    return itemStack;
  }

  public GuiItem asGuiItem() {
    return new GuiItem(itemStack);
  }

  public GuiItem asGuiItem(GuiAction<InventoryClickEvent> event) {
    return new GuiItem(itemStack, event);
  }
}