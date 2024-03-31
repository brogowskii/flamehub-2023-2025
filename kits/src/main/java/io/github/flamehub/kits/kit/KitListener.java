package io.github.flamehub.kits.kit;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import io.github.flamehub.commons.bukkit.util.InventoryUtil;
import io.github.flamehub.kits.KitsConfig;

public final class KitListener implements Listener {

    private final KitsConfig kitsConfig;

    public KitListener(KitsConfig kitsConfig) {
        this.kitsConfig = kitsConfig;
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onJoin(PlayerJoinEvent event) {

        Player player = event.getPlayer();
        if (!player.hasPlayedBefore()) {
            Kit kit = this.kitsConfig.findByName(this.kitsConfig.getStarterKit());
            if (kit == null) {
                return;
            }

            InventoryUtil.addItems(player, kit.getItems());
        }

    }

}
