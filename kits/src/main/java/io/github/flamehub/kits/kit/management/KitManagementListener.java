package io.github.flamehub.kits.kit.management;

import io.github.flamehub.commons.config.MongoConfigService;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import io.github.flamehub.kits.KitsConfig;
import io.github.flamehub.kits.kit.Kit;

public final class KitManagementListener implements Listener {

    private final MongoConfigService mongoConfigService;
    private final KitsConfig kitsConfig;

    public KitManagementListener(MongoConfigService mongoConfigService, KitsConfig kitsConfig) {
        this.mongoConfigService = mongoConfigService;
        this.kitsConfig = kitsConfig;
    }

    @EventHandler
    public void onClose(InventoryCloseEvent event) {

        InventoryView view = event.getView();
        String titleString = ChatColor.stripColor(view.getTitle());
        if (titleString.startsWith("Edytor zestawu")) {

            String[] split = titleString.split(": ");
            String kit = split[1];
            Kit byName = this.kitsConfig.findByName(kit);
            byName.getItems().clear();
            for (ItemStack itemStack : event.getInventory().getContents()) {
                if (itemStack == null) {
                    continue;
                }

                byName.getItems().add(itemStack);
            }

            this.mongoConfigService.save(this.kitsConfig);
        }

    }

}
