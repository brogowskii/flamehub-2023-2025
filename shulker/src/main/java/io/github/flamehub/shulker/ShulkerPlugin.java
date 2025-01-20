package io.github.flamehub.shulker;

import java.util.HashMap;
import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.Tag;
import org.bukkit.block.ShulkerBox;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BlockStateMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;

public final class ShulkerPlugin extends JavaPlugin implements Listener {

  private final HashMap<UUID, ShulkerUser> shulkers = new HashMap<>();
  private NamespacedKey key;

  @Override
  public void onEnable() {
    Bukkit.getPluginManager().registerEvents(this, this);
    this.key = new NamespacedKey(this, "shulker-modified");
  }

  @Override
  public void onDisable() {
    for (UUID uuid : shulkers.keySet()) {
      Player player = Bukkit.getPlayer(uuid);
      if (player != null) {
        player.closeInventory();
      }
    }
  }

  @EventHandler(ignoreCancelled = true)
  public void handle(PlayerInteractEntityEvent event) {
    if (!shulkers.containsKey(event.getPlayer().getUniqueId())) {
      return;
    }
    event.setCancelled(true);
  }

  @EventHandler(ignoreCancelled = true)
  public void handle(PlayerCommandPreprocessEvent event) {
    if (!shulkers.containsKey(event.getPlayer().getUniqueId())) {
      return;
    }
    event.setCancelled(true);
  }

  @EventHandler
  public void handle(PlayerInteractEvent event) {
    if (!event.getAction().equals(Action.RIGHT_CLICK_BLOCK) && !event.getAction()
        .equals(Action.RIGHT_CLICK_AIR)) {
      return;
    }

    ItemStack item = event.getItem();
    if (item == null) {
      return;
    }
    if (!Tag.SHULKER_BOXES.isTagged(item.getType())) {
      return;
    }

    event.setCancelled(true);
    if (event.getHand() == null) {
      return;
    }

    Player player = event.getPlayer();
    if (shulkers.containsKey(player.getUniqueId())) {
      return;
    }

    shulkers.put(
        player.getUniqueId(),
        new ShulkerUser(item, event.getHand().equals(EquipmentSlot.OFF_HAND) ? -1
            : event.getPlayer().getInventory().getHeldItemSlot())
    );
    ItemMeta itemMeta = item.getItemMeta();
    itemMeta.getPersistentDataContainer().set(key, PersistentDataType.BYTE, (byte) 1);
    item.setItemMeta(itemMeta);
    BlockStateMeta meta = (BlockStateMeta) itemMeta;
    ShulkerBox shulker = (ShulkerBox) meta.getBlockState();
    player.updateInventory();
    player.openInventory(shulker.getInventory());
  }

  @EventHandler(priority = EventPriority.LOWEST)
  public void handle(PlayerDeathEvent event) {
    if (!shulkers.containsKey(event.getEntity().getUniqueId())) {
      return;
    }
    event.getEntity().closeInventory();
  }

  @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
  public void handle(InventoryClickEvent event) {
    if (!shulkers.containsKey(event.getWhoClicked().getUniqueId())) {
      return;
    }

    if (event.getCurrentItem() != null && Tag.SHULKER_BOXES.isTagged(
        event.getCurrentItem().getType()) || Tag.SHULKER_BOXES.isTagged(
        event.getCursor().getType())) {
      event.setCancelled(true);
      return;
    }

    if (event.getAction() == InventoryAction.HOTBAR_SWAP
        || event.getAction() == InventoryAction.HOTBAR_MOVE_AND_READD) {
      ItemStack hotbarItem =
          (event.getHotbarButton() == -1) ? event.getWhoClicked().getInventory().getItemInOffHand()
              : event.getWhoClicked().getInventory().getItem(event.getHotbarButton());
      if (hotbarItem != null && Tag.SHULKER_BOXES.isTagged(hotbarItem.getType())) {
        event.setCancelled(true);
      }
    }
  }

  @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
  public void handle(PlayerSwapHandItemsEvent event) {
    if (!shulkers.containsKey(event.getPlayer().getUniqueId())) {
      return;
    }

    event.setCancelled(true);
  }

  @EventHandler(ignoreCancelled = true)
  public void handle(BlockPlaceEvent event) {
    if (!shulkers.containsKey(event.getPlayer().getUniqueId())) {
      return;
    }

    event.setCancelled(true);
  }

  @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
  public void handle(EntityDamageEvent event) {
    if (!(event.getEntity() instanceof Player player)) {
      return;
    }

    if (!shulkers.containsKey(player.getUniqueId())) {
      return;
    }

    player.closeInventory();
  }

  @EventHandler(priority = EventPriority.MONITOR)
  public void handle(PlayerDropItemEvent event) {
    Player player = event.getPlayer();
    ShulkerUser shulkerUser = shulkers.get(player.getUniqueId());
    if (shulkerUser == null) {
      return;
    }
    ItemStack item = event.getItemDrop().getItemStack();
    if (!Tag.SHULKER_BOXES.isTagged(item.getType())) {
      return;
    }
    if (!item.getItemMeta().getPersistentDataContainer().has(key, PersistentDataType.BYTE)) {
      return;
    }

    event.setCancelled(false);
    event.getItemDrop().remove();
    player.closeInventory();
  }

  @EventHandler
  public void handle(InventoryCloseEvent event) {
    Inventory inventory = event.getInventory();
    if (!inventory.getType().equals(InventoryType.SHULKER_BOX)) {
      return;
    }
    if (!(event.getPlayer() instanceof Player player)) {
      return;
    }

    ShulkerUser shulkerUser = shulkers.get(player.getUniqueId());
    if (shulkerUser == null) {
      return;
    }

    ItemStack itemStack = shulkerUser.getItemStack();
    if (itemStack == null) {
      shulkers.remove(player.getUniqueId());
      return;
    }

    try {
      boolean give = false;
      if (!Tag.SHULKER_BOXES.isTagged(itemStack.getType())) {
        itemStack = new ItemStack(shulkerUser.getType());
        give = true;
      }
      ItemMeta itemMeta = itemStack.getItemMeta();
      itemMeta.getPersistentDataContainer().remove(key);
      BlockStateMeta meta = (BlockStateMeta) itemMeta;
      ShulkerBox shulker = (ShulkerBox) meta.getBlockState();
      shulker.getInventory().setContents(inventory.getContents());
      meta.setBlockState(shulker);
      itemStack.setItemMeta(meta);
      if (give) {
        event.getPlayer().getInventory().setItem(shulkerUser.getSlot(), itemStack);
      }
      Bukkit.getScheduler()
          .runTaskLater(this, () -> shulkers.remove(player.getUniqueId()), 10L);
      Bukkit.getScheduler().runTaskLater(this, player::updateInventory, 1L);
    } catch (Exception ex) {
      ex.printStackTrace();

      shulkers.remove(player.getUniqueId());
      inventory.getContents();
      player.getInventory().addItem(inventory.getContents());

    }
  }
}
