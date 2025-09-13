package io.github.flamehub.lobby.selector;

import dev.triumphteam.gui.guis.Gui;
import io.github.flamehub.commons.bukkit.message.BukkitMessage;
import io.github.flamehub.commons.bukkit.message.BukkitMessagesService;
import io.github.flamehub.commons.bukkit.text.TextUtil;
import io.github.flamehub.commons.bukkit.util.FlameItemBuilder;
import io.github.flamehub.commons.bukkit.util.GuiHelper;
import io.github.flamehub.commons.messenger.RedisMessenger;
import io.github.flamehub.commons.queue.QueuePlayerAddPacket;
import io.github.flamehub.commons.server.NetworkServer;
import io.github.flamehub.commons.server.NetworkServerFacade;
import io.github.flamehub.commons.util.TimeUtil;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.plugin.Plugin;

public final class ServerSelectorGui {

  private final Gui gui;
  private final Plugin plugin;
  private final RedisMessenger redisMessenger;
  private final ServerSelectorConfig serverSelectorConfig;
  private final NetworkServerFacade networkServerFacade;
  private final BukkitMessagesService messagesService;
  private final Map<String, String> lastItemState = new ConcurrentHashMap<>();

  public ServerSelectorGui(
      final Plugin plugin,
      final RedisMessenger redisMessenger,
      final ServerSelectorConfig serverSelectorConfig,
      final NetworkServerFacade networkServerFacade,
      final BukkitMessagesService messagesService
  ) {
    this.plugin = plugin;
    this.redisMessenger = redisMessenger;
    this.serverSelectorConfig = serverSelectorConfig;
    this.networkServerFacade = networkServerFacade;
    this.messagesService = messagesService;

    gui = Gui.gui()
        .rows(5)
        .title(TextUtil.parse(
            "&#FF7C00\uD83C\uDFA3 &8| &#FF7C00&lᴡ&#FF820D&lʏ&#FE891A&lʙ&#FE8F27&lɪ&#FD9634&lᴇ&#FD9C41&lʀ&#FCA34E&lᴢ &#FDA24C&ls&#FD9A3D&lᴇ&#FE932E&lʀ&#FE8B1E&lᴡ&#FF840F&lᴇ&#FF7C00&lʀ"))
        .disableAllInteractions()
        .create();

    GuiHelper.fillGui5(gui);

    startAutoUpdate();
  }

  private void startAutoUpdate() {
    plugin.getServer().getScheduler().runTaskTimer(plugin, this::updateGui, 0L, 20L); // co sekundę
  }

  private void updateGui() {
    final Instant now = Instant.now();

    for (final ServerSelector serverSelector : serverSelectorConfig.getServerSelectors()) {
      final ServerSelectorItem item = serverSelector.getItem();
      final Optional<NetworkServer> networkServer = networkServerFacade.findByName(
          serverSelector.getInfoFrom());

      if (networkServer.isPresent() && "lobby".equals(networkServer.get().getCategory())) {
        continue;
      }

      final String stateKey;
      final List<String> lore;
      if (networkServer.isEmpty()) {
        stateKey = "OFFLINE";
        lore = List.of(
            "",
            " &8Brak danych o tym serwerze...",
            ""
        );
      } else {
        final long online = networkServerFacade.getPlayersFrom(serverSelector.getCategory());
        final long maxPlayers = networkServerFacade.getPlayersLimitFrom(
            serverSelector.getCategory());
        final String lastUpdate = TimeUtil.formatTimeSimple(
            Duration.between(networkServer.get().getStatistics().getLastUpdate(), now));
        final String startDate = serverSelector.getStartDate();

        stateKey = online + "|" + maxPlayers + "|" + lastUpdate + "|" + startDate;
        lore = BukkitMessage.from(item.getLore())
            .with("online_players", online)
            .with("max_players", maxPlayers)
            .with("last_update", lastUpdate)
            .with("start_date", startDate)
            .apply();
      }

      final String slotKey = String.valueOf(item.getSlot());
      final String lastState = lastItemState.get(slotKey);
      if (stateKey.equals(lastState)) {
        continue;
      }
      lastItemState.put(slotKey, stateKey);

      gui.updateItem(item.getSlot(), FlameItemBuilder.of(item.getIcon())
          .name(item.getName())
          .lore(lore)
          .flag(ItemFlag.HIDE_ATTRIBUTES)
          .asGuiItem(event -> {
            final Player player = (Player) event.getWhoClicked();
            player.closeInventory();
            if (networkServer.isEmpty()) {
              return;
            }
            redisMessenger.publish("queue",
                new QueuePlayerAddPacket(player.getName(), networkServer.get().getCategory()));
          }));
    }
  }


  public void openServers(final Player player) {
    gui.open(player);
  }

}
