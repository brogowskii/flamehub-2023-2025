package io.github.flamehub.crates;

import io.github.flamehub.commons.bukkit.message.BukkitMessagesService;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.plugin.Plugin;

public final class CrateListener implements Listener {

  private final Plugin plugin;
  private final CratesConfig cratesConfig;
  private final BukkitMessagesService messagesService;

  public CrateListener(Plugin plugin, CratesConfig cratesConfig,
      BukkitMessagesService messagesService) {
    this.plugin = plugin;
    this.cratesConfig = cratesConfig;
    this.messagesService = messagesService;
  }

  @EventHandler
  public void onInteract(PlayerInteractEvent event) {

    Block block = event.getClickedBlock();
    if (block == null || block.getType() == Material.AIR) {
      return;
    }

    Crate crate = cratesConfig.findByLocation(block.getLocation());
    if (crate == null) {
      return;
    }

    event.setCancelled(true);
    Player player = event.getPlayer();
    CrateGui crateGui = new CrateGui(plugin, messagesService, cratesConfig);
    crateGui.preview(player, crate);

  }

  @EventHandler
  public void onClick(InventoryClickEvent event) {
    Inventory inventory = event.getInventory();
    if (inventory.getHolder() instanceof CrateSpinGuiHolder) {
      event.setCancelled(true);
    }
  }


}
