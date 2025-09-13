package io.github.flamehub.crates;

import io.github.flamehub.commons.bukkit.message.BukkitMessagesService;
import io.github.flamehub.commons.network.message.NetworkMessageService;
import io.github.flamehub.commons.server.NetworkServerFacade;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;

public final class CrateListener implements Listener {

  private final CratesConfig cratesConfig;

  private final NetworkServerFacade networkServerFacade;
  private final NetworkMessageService networkMessageService;
  private final BukkitMessagesService messagesService;

  public CrateListener(final CratesConfig cratesConfig,
      final NetworkServerFacade networkServerFacade,
      final NetworkMessageService networkMessageService,
      final BukkitMessagesService messagesService) {
    this.cratesConfig = cratesConfig;
    this.networkServerFacade = networkServerFacade;
    this.networkMessageService = networkMessageService;
    this.messagesService = messagesService;
  }

  @EventHandler
  public void onInteract(final PlayerInteractEvent event) {

    final Block block = event.getClickedBlock();
    if (block == null || block.getType() == Material.AIR) {
      return;
    }

    final Crate crate = cratesConfig.findByLocation(block.getLocation());
    if (crate == null) {
      return;
    }

    final Player player = event.getPlayer();
    final CrateGui crateGui = new CrateGui(
        cratesConfig,
        networkServerFacade,
        networkMessageService,
        messagesService
    );
    crateGui.preview(player, crate);
    event.setCancelled(true);

  }


}
