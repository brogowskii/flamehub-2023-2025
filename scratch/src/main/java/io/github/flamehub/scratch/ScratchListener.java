package io.github.flamehub.scratch;

import io.github.flamehub.commons.bukkit.text.TextBuilder;
import io.github.flamehub.commons.network.message.NetworkMessageService;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public final class ScratchListener implements Listener {

    private final NetworkMessageService networkMessageService;
    private final ScratchConfig scratchConfig;

    public ScratchListener(NetworkMessageService networkMessageService, ScratchConfig scratchConfig) {
        this.networkMessageService = networkMessageService;
        this.scratchConfig = scratchConfig;
    }


    @EventHandler
    public void onInteract(PlayerInteractEvent event) {

        Player player = event.getPlayer();
        Action action = event.getAction();
        if (action == Action.RIGHT_CLICK_BLOCK || action == Action.LEFT_CLICK_BLOCK) {

            Block clickedBlock = event.getClickedBlock();
            if (clickedBlock == null) {
                return;
            }

            Location location = this.scratchConfig.getPreviewLocation();
            if (location == null) {
                return;
            }

            if (clickedBlock.getLocation().equals(location)) {
                event.setCancelled(true);
                new ScratchDropGui(player, this.scratchConfig).openPreview();
            }

        }

    }

    @EventHandler
    public void onOpen(PlayerInteractEvent event) {

        Player player = event.getPlayer();
        Action action = event.getAction();
        if (action == Action.RIGHT_CLICK_BLOCK || action == Action.RIGHT_CLICK_AIR) {

            ItemStack itemInMainHand = player.getInventory().getItemInMainHand();
            if (itemInMainHand.getType().isAir()) {
                return;
            }

            ItemStack scratchCardItem = this.scratchConfig.getScratchCardItem().clone();
            if (itemInMainHand.isSimilar(scratchCardItem)) {

                if (!this.scratchConfig.isEnabled()) {
                    TextBuilder.builder()
                            .text("&cZdrapki tymczasowo wyłączone!")
                            .send(player);
                    event.setCancelled(true);
                    return;
                }

                event.setCancelled(true);
                new ScratchDrawGui(player, this.scratchConfig, networkMessageService).random();

            }

        }

    }
}