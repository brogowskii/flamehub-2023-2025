package io.github.flamehub.essentials.vanish;

import io.github.flamehub.commons.bukkit.dispatcher.FlameDispatcher;
import io.github.flamehub.commons.bukkit.message.BukkitMessagesService;
import io.github.flamehub.essentials.EssentialsConstants;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.Plugin;

final class VanishListener implements Listener {

    private final Plugin plugin;
    private final FlameDispatcher flameDispatcher;
    private final VanishedEntryCache vanishedEntryCache;
    private final VanishedEntryRepository vanishedEntryRepository;
    private final BukkitMessagesService messagesService;

    VanishListener(
            final Plugin plugin,
            final FlameDispatcher flameDispatcher,
            final VanishedEntryCache vanishedEntryCache,
            final VanishedEntryRepository vanishedEntryRepository,
            final BukkitMessagesService messagesService
    ) {
        this.plugin = plugin;
        this.flameDispatcher = flameDispatcher;
        this.vanishedEntryCache = vanishedEntryCache;
        this.vanishedEntryRepository = vanishedEntryRepository;
        this.messagesService = messagesService;
    }

    @EventHandler
    public void onJoin(final PlayerJoinEvent event) {

        final Player player = event.getPlayer();
        this.flameDispatcher.dispatchAsync(() -> {
            VanishedEntry load = vanishedEntryRepository.load(player.getUniqueId());
            if (load != null) {
                if (player.hasPermission(EssentialsConstants.VANISH_PERMISSION)) {
                    vanishedEntryCache.addVanished(player.getUniqueId());
                }

            }

            this.flameDispatcher.dispatch(() -> {

                if (vanishedEntryCache.isVanished(player.getUniqueId())) {
                    for (final Player it : Bukkit.getOnlinePlayers()) {
                        if (!it.hasPermission(EssentialsConstants.VANISH_PERMISSION)) {
                            it.hidePlayer(plugin, player);
                        }
                    }
                }

                for (final Player it : Bukkit.getOnlinePlayers()) {
                    if (vanishedEntryCache.isVanished(it.getUniqueId())) {
                        if (!player.hasPermission(EssentialsConstants.VANISH_PERMISSION)) {
                            player.hidePlayer(plugin, it);
                        }
                    }
                }

            });


        });


    }

    @EventHandler
    public void onQuit(final PlayerQuitEvent event) {

        final Player player = event.getPlayer();
        this.flameDispatcher.dispatchAsync(() -> {
            if (vanishedEntryCache.isVanished(player.getUniqueId())) {
                vanishedEntryRepository.save(new VanishedEntry(player.getUniqueId(), player.getName()));
                vanishedEntryCache.removeVanished(player.getUniqueId());
            }
        });


    }

    @EventHandler(ignoreCancelled = true)
    public void onItemDrop(final PlayerDropItemEvent event) {
        final Player player = event.getPlayer();
        if (vanishedEntryCache.isVanished(player.getUniqueId())) {
            this.messagesService.sendMessage(player, "vanish.cant.execute.this.action");
            event.setCancelled(true);
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void itemPickup(final EntityPickupItemEvent event) {
        if (vanishedEntryCache.isVanished(event.getEntity().getUniqueId())) {
            event.setCancelled(true);
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onEntityDamage(final EntityDamageByEntityEvent event) {
        final Entity entity = event.getDamager();
        if (!(entity instanceof Player player)) {
            return;
        }

        if (vanishedEntryCache.isVanished(player.getUniqueId())) {
            this.messagesService.sendMessage(player, "vanish.cant.execute.this.action");
            event.setCancelled(true);
        }
    }


}
