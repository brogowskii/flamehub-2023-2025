package io.github.flamehub.lobby.selector;

import io.github.flamehub.commons.bukkit.message.BukkitMessagesService;
import io.github.flamehub.commons.bukkit.util.FlameItemBuilder;
import io.github.flamehub.commons.messenger.RedisMessenger;
import io.github.flamehub.commons.server.NetworkServerFacade;
import io.papermc.paper.event.player.AsyncChatEvent;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
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

public final class ServerSelectorListener implements Listener {

  private final static List<Integer> CANCELLED_SLOTS = List.of(
      0, 8, 4
  );

  private final static Set<UUID> DISABLED_VISIBILITY_MAP = new HashSet<>();
  private final static Map<UUID, Long> CHANGE_COOLDOWN = new HashMap<>();

  private final Plugin plugin;
  private final RedisMessenger redisMessenger;
  private final ServerSelectorConfig serverSelectorConfig;
  private final NetworkServerFacade networkServerFacade;
  private final BukkitMessagesService messagesService;
  private final ServerSelectorGui serverSelectorGui;

  public ServerSelectorListener(
      final Plugin plugin,
      final RedisMessenger redisMessenger, final ServerSelectorConfig serverSelectorConfig,
      final NetworkServerFacade networkServerFacade,
      final BukkitMessagesService messagesService
  ) {
    this.plugin = plugin;
    this.redisMessenger = redisMessenger;
    this.serverSelectorConfig = serverSelectorConfig;
    this.networkServerFacade = networkServerFacade;
    this.messagesService = messagesService;

    serverSelectorGui = new ServerSelectorGui(plugin, redisMessenger,
        serverSelectorConfig, networkServerFacade, messagesService);
  }

  @EventHandler
  public void onChat(final AsyncChatEvent event) {
    final Player player = event.getPlayer();
    if (!player.hasPermission("lobby.chat.use")) {
      messagesService.sendMessage(player, "deny.chat.use");
      event.setCancelled(true);
    }

  }

  @EventHandler
  public void onMove(final PlayerMoveEvent event) {
    final Player player = event.getPlayer();
    if (player.getLocation().getY() <= 0) {
      player.teleport(player.getWorld().getSpawnLocation());
    }
  }

  @EventHandler
  public void onCompassClick(final PlayerInteractEvent event) {
    final Player player = event.getPlayer();
    if (event.getAction() == Action.RIGHT_CLICK_AIR
        || event.getAction() == Action.RIGHT_CLICK_BLOCK) {
      if (player.getInventory().getItemInMainHand().getType() == Material.COMPASS) {
        player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 3f, 1f);
        serverSelectorGui.openServers(player);
      }
    }
  }

  @EventHandler
  public void onJoin(final PlayerJoinEvent event) {
    final Player player = event.getPlayer();
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
  public void onThrow(final PlayerDropItemEvent event) {
    messagesService.sendMessage(event.getPlayer(), "throw.item.deny");
    event.setCancelled(true);
  }

  @EventHandler
  public void onItemMove(final InventoryClickEvent event) {
    final int slot = event.getSlot();
    if (CANCELLED_SLOTS.contains(slot)) {
      event.setCancelled(true);
    }

  }

  @EventHandler
  public void visibilityChange(final PlayerInteractEvent event) {
    final ItemStack item = event.getItem();
    if (item == null || item.getType() == Material.AIR) {
      return;
    }

    if (item.getType() == Material.LIME_DYE || item.getType() == Material.GRAY_DYE) {

      final Action action = event.getAction();
      if (action == Action.RIGHT_CLICK_AIR || action == Action.RIGHT_CLICK_BLOCK) {
        final Player player = event.getPlayer();

        if (CHANGE_COOLDOWN.containsKey(player.getUniqueId())) {
          final long time = CHANGE_COOLDOWN.get(player.getUniqueId());
          if (time > System.currentTimeMillis()) {
            messagesService.sendMessage(player, "visibility.change.cooldown");
            return;
          }
        }

        CHANGE_COOLDOWN.put(player.getUniqueId(),
            System.currentTimeMillis() + TimeUnit.SECONDS.toMillis(5));
        if (!DISABLED_VISIBILITY_MAP.contains(player.getUniqueId())) {
          player.getInventory().setItem(8, FlameItemBuilder.of(Material.GRAY_DYE)
              .name("&aPokaz graczy &7(Kliknij prawym)")
              .asItemStack());
          DISABLED_VISIBILITY_MAP.add(player.getUniqueId());
          for (final Player it : Bukkit.getOnlinePlayers()) {
            player.hidePlayer(plugin, it);
          }

          return;
        }

        for (final Player it : Bukkit.getOnlinePlayers()) {
          player.showPlayer(plugin, it);
        }

        player.getInventory().setItem(8, FlameItemBuilder.of(Material.LIME_DYE)
            .name("&cUkryj graczy &7(Kliknij prawym)")
            .asItemStack());

        DISABLED_VISIBILITY_MAP.remove(player.getUniqueId());

      }
    }
  }

}
