package io.github.flamehub.essentials.banitem;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.inventory.CraftItemEvent;

final class BanItemListener implements Listener {

  private final BanItemFacade banItemFacade;

  BanItemListener(final BanItemFacade banItemFacade) {
    this.banItemFacade = banItemFacade;
  }

  @EventHandler
  public void onBreak(final BlockBreakEvent event) {

    final Material type = event.getBlock().getType();
    final Player player = event.getPlayer();
    if (this.banItemFacade.getMaterialsBreak().contains(type) && !player.hasPermission(
        "server.blockedblocks.bypass")) {
      event.setCancelled(true);
    }

  }

  @EventHandler
  public void onPlace(final BlockPlaceEvent event) {

    final Material type = event.getBlock().getType();
    final Player player = event.getPlayer();
    if (this.banItemFacade.getMaterialsBreak().contains(type) && !player.hasPermission(
        "server.blockedblocks.bypass")) {
      event.setCancelled(true);
      return;
    }

    if (this.banItemFacade.getMaterialsPlace().contains(type)) {
      event.setCancelled(true);
    }

  }

  @EventHandler
  public void onCraft(final CraftItemEvent event) {

    if (this.banItemFacade.getCraftings().contains(event.getRecipe().getResult().getType())) {
      event.setCancelled(true);
    }

  }

}
