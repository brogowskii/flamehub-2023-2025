package io.github.flamehub.lobby.selector;

import io.github.flamehub.commons.bukkit.message.BukkitMessagesService;
import io.github.flamehub.commons.bukkit.util.FlameItemBuilder;
import io.github.flamehub.commons.messenger.RedisMessenger;
import io.github.flamehub.commons.server.NetworkServerCache;
import io.papermc.paper.event.player.AsyncChatEvent;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import java.util.*;
import java.util.concurrent.TimeUnit;

public final class ServerSelectorListener implements Listener {

    private final static List<Integer> CANCELLED_SLOTS = List.of(
            0, 8, 4
    );

    private final static Set<UUID> DISABLED_VISIBILITY_MAP = new HashSet<>();
    private final static Map<UUID, Long> CHANGE_COOLDOWN = new HashMap<>();

    private final Plugin plugin;
    private final RedisMessenger redisMessenger;
    private final ServerSelectorConfig serverSelectorConfig;
    private final NetworkServerCache networkServerCache;
    private final BukkitMessagesService messagesService;

    public ServerSelectorListener(
            Plugin plugin,
            RedisMessenger redisMessenger, ServerSelectorConfig serverSelectorConfig,
            NetworkServerCache networkServerCache,
            BukkitMessagesService messagesService
    ) {
        this.plugin = plugin;
        this.redisMessenger = redisMessenger;
        this.serverSelectorConfig = serverSelectorConfig;
        this.networkServerCache = networkServerCache;
        this.messagesService = messagesService;
    }

    @EventHandler
    public void onChat(AsyncChatEvent event) {
        Player player = event.getPlayer();
        if (!player.hasPermission("lobby.chat.use")) {
            this.messagesService.sendMessage(player, "deny.chat.use");
            event.setCancelled(true);
        }

    }

    @EventHandler
    public void onMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        if (player.getLocation().getY() <= 0) {
            player.teleport(player.getWorld().getSpawnLocation());
        }
    }

    @EventHandler
    public void onCompassClick(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        if (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK) {
            if (player.getInventory().getItemInMainHand().getType() == Material.COMPASS) {
                player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 3f, 1f);
                ServerSelectorGui serverSelectorGui = new ServerSelectorGui(this.plugin, redisMessenger, this.serverSelectorConfig, this.networkServerCache, this.messagesService);
                serverSelectorGui.openServers(player);
            }
        }
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        player.teleport(player.getWorld().getSpawnLocation().toCenterLocation());
        player.getInventory().clear();
        player.getInventory().setHeldItemSlot(0);
        player.getInventory().setItem(0, FlameItemBuilder.of(Material.COMPASS)
                .name("&aWybierz serwer")
                .asItemStack());
        player.getInventory().setItem(8, FlameItemBuilder.of(Material.LIME_DYE)
                .name("&cUkryj graczy &7(Kliknij prawym)")
                .asItemStack());
    }

    @EventHandler
    public void onThrow(PlayerDropItemEvent event) {
        this.messagesService.sendMessage(event.getPlayer(), "throw.item.deny");
        event.setCancelled(true);
    }

    @EventHandler
    public void onItemMove(InventoryClickEvent event) {
        int slot = event.getSlot();
        if (CANCELLED_SLOTS.contains(slot)) {
            event.setCancelled(true);
        }

    }

    @EventHandler
    public void visibilityChange(PlayerInteractEvent event) {
        ItemStack item = event.getItem();
        if (item == null || item.getType() == Material.AIR) {
            return;
        }

        if (item.getType() == Material.LIME_DYE || item.getType() == Material.GRAY_DYE) {

            Action action = event.getAction();
            if (action == Action.RIGHT_CLICK_AIR || action == Action.RIGHT_CLICK_BLOCK) {
                Player player = event.getPlayer();

                if (CHANGE_COOLDOWN.containsKey(player.getUniqueId())) {
                    long time = CHANGE_COOLDOWN.get(player.getUniqueId());
                    if (time > System.currentTimeMillis()) {
                        this.messagesService.sendMessage(player, "visibility.change.cooldown");
                        return;
                    }
                }

                CHANGE_COOLDOWN.put(player.getUniqueId(), System.currentTimeMillis() + TimeUnit.SECONDS.toMillis(5));
                if (!DISABLED_VISIBILITY_MAP.contains(player.getUniqueId())) {
                    player.getInventory().setItem(8, FlameItemBuilder.of(Material.GRAY_DYE)
                            .name("&aPokaz graczy &7(Kliknij prawym)")
                            .asItemStack());
                    DISABLED_VISIBILITY_MAP.add(player.getUniqueId());
                    for (Player it : Bukkit.getOnlinePlayers()) {
                        player.hidePlayer(this.plugin, it);
                    }

                    return;
                }

                for (Player it : Bukkit.getOnlinePlayers()) {
                    player.showPlayer(this.plugin, it);
                }

                player.getInventory().setItem(8, FlameItemBuilder.of(Material.LIME_DYE)
                        .name("&cUkryj graczy &7(Kliknij prawym)")
                        .asItemStack());

                DISABLED_VISIBILITY_MAP.remove(player.getUniqueId());

            }
        }
    }

}
