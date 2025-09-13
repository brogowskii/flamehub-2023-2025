package io.github.flamehub.flamebox;

import io.github.flamehub.commons.bukkit.CommonsPlugin;
import io.github.flamehub.commons.bukkit.message.BukkitMessage;
import io.github.flamehub.commons.bukkit.text.TextUtil;
import io.github.flamehub.commons.bukkit.util.InventoryUtil;
import io.github.flamehub.commons.network.message.NetworkMessageFilter;
import io.github.flamehub.commons.network.message.NetworkMessageType;
import io.github.flamehub.commons.server.NetworkServerContext;
import java.util.Set;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public final class FlameBoxListener implements Listener {

  private final FlameBoxConfig flameBoxConfig;

  public FlameBoxListener(final FlameBoxConfig flameBoxConfig) {
    this.flameBoxConfig = flameBoxConfig;
  }

  @EventHandler
  public void onInteract(final PlayerInteractEvent event) {

    final Player player = event.getPlayer();
    final Action action = event.getAction();
    if (action == Action.RIGHT_CLICK_BLOCK || action == Action.LEFT_CLICK_BLOCK) {

      final Block clickedBlock = event.getClickedBlock();
      if (clickedBlock == null) {
        return;
      }

      final Set<Location> location = flameBoxConfig.getPreviewLocation();
      if (location == null || location.isEmpty()) {
        return;
      }

      if (location.contains(clickedBlock.getLocation())) {
        event.setCancelled(true);
        new FlameBoxGui(flameBoxConfig).open(player);
      }

    }

  }

  @EventHandler
  public void onPlace(final BlockPlaceEvent event) {

    final Player player = event.getPlayer();
    if (player.getInventory().getItemInOffHand().getType() == Material.NOTE_BLOCK) {
      return;
    }

    final ItemStack itemInMainHand = player.getInventory().getItemInMainHand();
    if (!itemInMainHand.isSimilar(flameBoxConfig.getFlameBoxItem())) {
      return;
    }

    event.setCancelled(true);
    if (!flameBoxConfig.isEnabled()) {
      BukkitMessage.from("&cOtwieranie zostało tymczasowo wyłączone!").deliver(player);
      return;
    }

    final ItemStack clone = itemInMainHand.clone();
    clone.setAmount(1);
    player.getInventory().removeItem(clone);

    final FlameBoxDrop random = flameBoxConfig.random();
    final ItemStack itemStack = random.getItemStack();
    InventoryUtil.addItem(player, itemStack);

    CommonsPlugin.getInstance().getFlameDispatcher().dispatchAsync(() -> {

      final String serialize;
      if (!itemStack.getItemMeta().hasDisplayName()) {
        serialize = itemStack.getType().toString().toUpperCase();
      } else {
        serialize = TextUtil.serialize(itemStack.clone().getItemMeta().displayName());
      }

      CommonsPlugin.getInstance().getNetworkMessageService().send(
          "&#EE0A0A✪ &8| &fGracz &#EE0A0A" + player.getName()
              + " &fotworzył &#C40505&lғ&#D20707&lʟ&#E00909&lᴀ&#EE0A0A&lᴍ&#FC0C0C&lᴇ&#EE0A0A&lʙ&#E00909&lᴏ&#D20707&lx&#C40505&lᴀ &fi wylosował: "
              + serialize + " &f&lx" + itemStack.getAmount(),
          NetworkMessageFilter.builder()
              .idForHide("flamebox")
              .targetServerCategory(NetworkServerContext.CURRENT_CATEGORY)
              .build(),
          NetworkMessageType.CHAT
      );

    });

  }

}
