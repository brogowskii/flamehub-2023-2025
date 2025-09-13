package io.github.flamehub.armor.upgrade;

import static io.github.flamehub.armor.upgrade.ArmorUpgradePlugin.CUSTOM_ARMOR_KEY;

import io.github.flamehub.commons.config.FlameConfigService;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

public final class ArmorUpgradeService {

  private final ArmorUpgradeConfig armorUpgradeConfig;
  private final FlameConfigService flameConfigService;

  public ArmorUpgradeService(
      final ArmorUpgradeConfig armorUpgradeConfig,
      final FlameConfigService flameConfigService
  ) {
    this.armorUpgradeConfig = armorUpgradeConfig;
    this.flameConfigService = flameConfigService;
  }

  public void removeLevel(final int level) {
    for (final Map.Entry<String, List<ArmorUpgrade>> entry : armorUpgradeConfig.getArmorUpgradeTypeListMap()
        .entrySet()) {
      final List<ArmorUpgrade> armorUpgrades = entry.getValue();
      armorUpgrades.removeIf(armorUpgrade -> armorUpgrade.getLevel() == level);
    }
  }

  public void addArmorUpgrade(final ArmorUpgrade armorUpgrade) {
    final ArmorUpgradeType type = armorUpgrade.getType();
    List<ArmorUpgrade> armorUpgrades = getArmorUpgrades(type);
    if (armorUpgrades == null) {
      armorUpgrades = new ArrayList<>();
    }

    final ItemStack itemStack = armorUpgrade.getItemStack();
    final ItemMeta itemMeta = itemStack.getItemMeta();
    itemMeta.getPersistentDataContainer()
        .set(CUSTOM_ARMOR_KEY, PersistentDataType.INTEGER, armorUpgrade.getLevel());
    itemStack.setItemMeta(itemMeta);

    armorUpgrades.add(armorUpgrade);
    armorUpgradeConfig.getArmorUpgradeTypeListMap().put(type.getId().toUpperCase(), armorUpgrades);
    flameConfigService.save(ArmorUpgradeConfig.class);

  }

  public List<ArmorUpgrade> getArmorUpgrades(final ArmorUpgradeType armorUpgradeType) {
    return getArmorUpgrades(armorUpgradeType.getId().toUpperCase());
  }

  public List<ArmorUpgrade> getArmorUpgrades(final String type) {
    return armorUpgradeConfig.getArmorUpgradeTypeListMap().get(type.toUpperCase());
  }

  public int findHighestLevelInType(final ArmorUpgradeType armorUpgradeType) {
    final List<ArmorUpgrade> armorUpgrades = getArmorUpgrades(armorUpgradeType);
    if (armorUpgrades == null) {
      return 0;
    }

    armorUpgrades.sort(Comparator.comparingInt(ArmorUpgrade::getLevel));
    return armorUpgrades.getLast().getLevel();
  }

  public int findLowestLevelInType(final ArmorUpgradeType armorUpgradeType) {
    final List<ArmorUpgrade> armorUpgrades = getArmorUpgrades(armorUpgradeType);
    if (armorUpgrades == null) {
      return 0;
    }

    armorUpgrades.sort(Comparator.comparingInt(ArmorUpgrade::getLevel));
    return armorUpgrades.getFirst().getLevel();
  }

  public List<ArmorUpgrade> findByLevel(final int level) {
    final List<ArmorUpgrade> armorUpgrades = new ArrayList<>();
    for (final Map.Entry<String, List<ArmorUpgrade>> entry : armorUpgradeConfig.getArmorUpgradeTypeListMap()
        .entrySet()) {
      final List<ArmorUpgrade> value = entry.getValue();
      for (final ArmorUpgrade armorUpgrade : value) {
        if (armorUpgrade.getLevel() == level) {
          armorUpgrades.add(armorUpgrade);
        }
      }
    }

    return armorUpgrades;
  }

  public ArmorUpgrade findByTypeAndLevel(final ArmorUpgradeType armorUpgradeType, final int level) {
    final List<ArmorUpgrade> armorUpgrades = getArmorUpgrades(armorUpgradeType);
    if (armorUpgrades == null) {
      return null;
    }

    for (final ArmorUpgrade armorUpgrade : armorUpgrades) {
      if (armorUpgrade.getLevel() == level) {
        return armorUpgrade;
      }
    }

    return null;

  }

  public List<ItemStack> itemStacksByType(final ArmorUpgradeType armorUpgradeType) {
    final List<ItemStack> itemStacks = new ArrayList<>();
    final List<ArmorUpgrade> armorUpgrades = getArmorUpgrades(armorUpgradeType);
    if (armorUpgrades == null) {
      return itemStacks;
    }

    for (final ArmorUpgrade armorUpgrade : armorUpgrades) {
      itemStacks.add(armorUpgrade.getItemStack().clone());
    }

    return itemStacks;
  }

  public Map.Entry<ItemStack, Integer> findHighestLevel(final Player player,
      final ArmorUpgradeType armorUpgradeType) {

    final List<ItemStack> itemStacks = new ArrayList<>();
    for (final ItemStack content : player.getInventory().getContents()) {

      if (content == null) {
        continue;
      }

      final ItemMeta itemMeta = content.getItemMeta();
      if (itemMeta == null || itemMeta.displayName() == null) {
        continue;
      }

      final List<ItemStack> itemStacksByType = itemStacksByType(armorUpgradeType);
      for (final ItemStack itemStack : itemStacksByType) {
        if (itemStack == null || itemStack.getItemMeta() == null
            || itemStack.getItemMeta().displayName() == null) {
          continue;
        }

        if (itemStack.getType() == content.getType() && Objects.equals(
            itemStack.getItemMeta().getEnchants(), itemMeta.getEnchants())) {
          itemStacks.add(content);
        }
      }

    }

    if (itemStacks.isEmpty()) {
      return Map.entry(new ItemStack(Material.AIR), 0);
    }

    itemStacks.sort(Comparator.comparingInt(o -> o.getEnchantmentLevel(Enchantment.DURABILITY)));
    final ItemStack itemStack = itemStacks.get(itemStacks.size() - 1);
    if (itemStack == null) {
      return Map.entry(new ItemStack(Material.AIR), 0);
    }

    final ArmorUpgrade armorUpgrade = findByItem(itemStack, armorUpgradeType);
    if (armorUpgrade == null) {
      return Map.entry(new ItemStack(Material.AIR), 0);
    }

    return Map.entry(itemStack, armorUpgrade.getLevel());
  }

  public ArmorUpgrade findByItem(final ItemStack itemStack) {
    for (final Map.Entry<String, List<ArmorUpgrade>> entry :
        armorUpgradeConfig.getArmorUpgradeTypeListMap().entrySet()) {
      final List<ArmorUpgrade> value = entry.getValue();
      for (final ArmorUpgrade armorUpgrade : value) {
        if (armorUpgrade.getItemStack().getItemMeta().displayName() == null) {
          continue;
        }

        if (Objects.equals(
            armorUpgrade.getItemStack().getItemMeta().displayName(),
            itemStack.getItemMeta().displayName())) {
          return armorUpgrade;
        }
      }
    }

    return null;
  }

  public ArmorUpgrade findByItem(final ItemStack itemStack, final ArmorUpgradeType armorUpgradeType) {
    final List<ArmorUpgrade> armorUpgrades = getArmorUpgrades(armorUpgradeType);
    if (armorUpgrades == null) {
      return null;
    }

    for (final ArmorUpgrade armorUpgrade : armorUpgrades) {

      final ItemStack armorItemStack = armorUpgrade.getItemStack();
      if (armorItemStack.getItemMeta() == null) {
        continue;
      }

      if (armorItemStack.getItemMeta().displayName() == null) {
        continue;
      }

      if (Objects.equals(armorItemStack.getItemMeta().displayName(),
          itemStack.getItemMeta().displayName())) {
        return armorUpgrade;
      }
    }

    return null;
  }

//    public NamespacedKey getDragonBoneKey() {
//        return dragonBoneKey;
//    }

//    public ItemStack getDragonBone(int highestLevel) {
//
//        ArmorUpgrade armorUpgrade = findByTypeAndLevel(ArmorUpgradeType.SWORD, highestLevel);
//        ItemStack clone = armorUpgrade.getItemStack().clone();
//        int durability = clone.getEnchantmentLevel(Enchantment.DURABILITY);
//        int sharpness = clone.getEnchantmentLevel(Enchantment.DAMAGE_ALL);
//        int fireAspect = clone.getEnchantmentLevel(Enchantment.FIRE_ASPECT);
//
//        ItemStack itemStack = FlameItemBuilder.of(Material.BONE)
//                .name("&x&F&1&5&5&5&5&lꜱ&x&F&1&6&2&5&5&lᴍ&x&F&1&6&E&5&5&lᴏ&x&F&1&7&B&5&5&lᴄ&x&F&1&8&8&5&5&lᴢ&x&F&1&8&8&5&5&lᴀ &x&F&1&7&B&5&5&lᴋ&x&F&1&6&E&5&5&lᴏ&x&F&1&6&2&5&5&lꜱ&x&F&1&5&5&5&5&lᴄ &8(&x&F&1&8&E&5&5★&x&F&1&8&E&5&5∞&8)")
//                .asItemStack();
//
//        ItemMeta itemMeta = itemStack.getItemMeta();
//        itemMeta.addEnchant(Enchantment.DURABILITY, durability, true);
//        itemMeta.addEnchant(Enchantment.DAMAGE_ALL, sharpness, true);
//        itemMeta.addEnchant(Enchantment.FIRE_ASPECT, fireAspect, true);
//        itemMeta.addEnchant(Enchantment.WATER_WORKER, 100, true);
//        itemMeta.addEnchant(Enchantment.LUCK, 33, true);
//
//        if (itemMeta.getAttributeModifiers() == null || itemMeta.getAttributeModifiers(Attribute.GENERIC_ATTACK_SPEED) == null) {
//            itemMeta.addAttributeModifier(Attribute.GENERIC_ATTACK_SPEED, new AttributeModifier(UUID.randomUUID(), "attack_speed", -2.4, AttributeModifier.Operation.ADD_NUMBER, EquipmentSlot.HAND));
//        }
//
//        PersistentDataContainer persistentDataContainer = itemMeta.getPersistentDataContainer();
//        persistentDataContainer.set(dragonBoneKey, PersistentDataType.INTEGER, highestLevel);
//        itemStack.setItemMeta(itemMeta);
//
//        ItemStack make = EnchantLoreAdder.make(itemStack);
//        return FlameItemBuilder.of(make)
//                .appendLore(
//                        "",
//                        " &cᴛᴇɴ ɪᴛᴇᴍ ᴜʟᴇᴘsᴢʏ sɪᴇ ᴀᴜᴛᴏᴍᴀᴛʏᴄᴢɴɪᴇ",
//                        " &cᴅᴏ ɴᴀᴊᴡɪᴇᴋsᴢᴇɢᴏ ᴍᴏᴢʟɪᴡᴇɢᴏ ᴘᴏᴢɪᴏᴍᴜ",
//                        " &cɢᴅʏ ᴜᴅᴇʀᴢʏsᴢ ɴɪᴍ ᴊᴀᴋɪᴇɢᴏs ɢʀᴀᴄᴢᴀ",
//                        "",
//                        " &fᴘʀᴢᴇᴅᴍɪᴏᴛ ᴍᴏᴢᴇꜱᴢ ᴡʏᴋᴜᴘɪᴄ",
//                        " &fᴜ &x&F&1&5&5&5&5ꜱᴍᴏᴄᴢᴇɢᴏ ᴋᴏᴡᴀʟᴀ",
//                        ""
//                )
//                .flag(ItemFlag.HIDE_ATTRIBUTES)
//                .asItemStack();
//
//    }

//  public ItemStack getDragonShovel(final int highestLevel) {
//
//    final ArmorUpgrade armorUpgrade = findByTypeAndLevel(ArmorUpgradeType.SHOVEL, highestLevel);
//    final ItemStack clone = armorUpgrade.getItemStack().clone();
//    final int durability = clone.getEnchantmentLevel(Enchantment.DURABILITY);
//    final int digSpeed = clone.getEnchantmentLevel(Enchantment.DIG_SPEED);
//    final int lootBonusBlocks = clone.getEnchantmentLevel(Enchantment.LOOT_BONUS_BLOCKS);
//
//    final ItemStack itemStack = FlameItemBuilder.of(Material.NETHERITE_SHOVEL)
//        .name(
//            "&#EA3838&ls&#EC3F33&lᴍ&#EE472F&lᴏ&#EF4E2A&lᴄ&#F15625&lᴢ&#F35D21&lᴀ &#F66C17&lʟ&#F87313&lᴏ&#FA7B0E&lᴘ&#FC8209&lᴀ&#FD8A05&lᴛ&#FF9100&lᴀ &8(&#FF6700★∞&8)")
//        .asItemStack();
//
//    final ItemMeta itemMeta = itemStack.getItemMeta();
//    itemMeta.addEnchant(Enchantment.DURABILITY, durability, true);
//    itemMeta.addEnchant(Enchantment.DIG_SPEED, digSpeed, true);
//    itemMeta.addEnchant(Enchantment.LOOT_BONUS_BLOCKS, lootBonusBlocks, true);
//    itemMeta.addEnchant(Enchantment.WATER_WORKER, 100, true);
//
//    final PersistentDataContainer persistentDataContainer = itemMeta.getPersistentDataContainer();
//    persistentDataContainer.set(dragonShovelKey, PersistentDataType.INTEGER, highestLevel);
//    itemStack.setItemMeta(itemMeta);
//
//    final ItemStack make = EnchantLoreAdder.make(itemStack);
//    return FlameItemBuilder.of(make)
//        .flag(ItemFlag.HIDE_ATTRIBUTES)
//        .flag(ItemFlag.HIDE_ITEM_SPECIFICS)
//        .appendLore(
//            "",
//            " &cᴛᴇɴ ɪᴛᴇᴍ ᴜʟᴇᴘsᴢʏ sɪᴇ ᴀᴜᴛᴏᴍᴀᴛʏᴄᴢɴɪᴇ",
//            " &cᴅᴏ ɴᴀᴊᴡɪᴇᴋsᴢᴇɢᴏ ᴍᴏᴢʟɪᴡᴇɢᴏ ᴘᴏᴢɪᴏᴍᴜ",
//            " &cɢᴅʏ ʀᴏᴢᴋᴏᴘɪᴇsᴢ ɴɪᴍ ᴊᴀᴋɪs ʙʟᴏᴋ",
//            "",
//            " &fᴋᴏᴘɪᴀᴄ ᴛᴀ ʟᴏᴘᴀᴛᴀ ᴍᴀꜱᴢ ꜱᴢᴀɴꜱᴇ ɴᴀ",
//            " &fᴡʏʟᴜᴘɪᴇɴɪᴇ &x&F&1&5&5&5&5ꜰʀᴀɢᴍᴇɴᴛᴏᴡ ᴋᴏsᴍᴏsᴜ",
//            ""
//        )
//        .asItemStack();
//
//  }

}
