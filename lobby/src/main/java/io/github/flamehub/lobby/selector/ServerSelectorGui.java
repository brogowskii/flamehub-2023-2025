package io.github.flamehub.lobby.selector;

import dev.triumphteam.gui.guis.Gui;
import io.github.flamehub.commons.bukkit.message.BukkitMessage;
import io.github.flamehub.commons.bukkit.message.BukkitMessagesService;
import io.github.flamehub.commons.bukkit.text.TextUtil;
import io.github.flamehub.commons.bukkit.util.FlameItemBuilder;
import io.github.flamehub.commons.messenger.RedisMessenger;
import io.github.flamehub.commons.queue.QueuePlayerAddPacket;
import io.github.flamehub.commons.server.NetworkServer;
import io.github.flamehub.commons.server.NetworkServerCache;
import io.github.flamehub.commons.util.TimeUtil;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.plugin.Plugin;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

public final class ServerSelectorGui {

    private final Plugin plugin;
    private final RedisMessenger redisMessenger;
    private final ServerSelectorConfig serverSelectorConfig;
    private final NetworkServerCache networkServerCache;
    private final BukkitMessagesService messagesService;

    public ServerSelectorGui(
            Plugin plugin,
            RedisMessenger redisMessenger,
            ServerSelectorConfig serverSelectorConfig,
            NetworkServerCache networkServerCache,
            BukkitMessagesService messagesService
    ) {
        this.plugin = plugin;
        this.redisMessenger = redisMessenger;
        this.serverSelectorConfig = serverSelectorConfig;
        this.networkServerCache = networkServerCache;
        this.messagesService = messagesService;
    }

    public void openLobbies(Player player) {
        Gui gui = Gui.gui()
                .rows(1)
                .title(TextUtil.parse("&8&lWybierz lobby"))
                .disableAllInteractions()
                .create();

        Runnable fill = () -> {

            Instant now = Instant.now();
            for (ServerSelector serverSelector : this.serverSelectorConfig.getServerSelectors()) {

                ServerSelectorItem item = serverSelector.getItem();
                Optional<NetworkServer> networkServerOptional = this.networkServerCache.findByName(serverSelector.getInfoFrom());

                if (networkServerOptional.isEmpty()) {
                    continue;
                }

                NetworkServer networkServer = networkServerOptional.get();
                if (!networkServer.getCategory().equals("lobby")) {
                    continue;
                }

                List<String> lore = BukkitMessage.from(item.getLore())
                        .with("online_players", networkServer.getStatistics().getPlayers())
                        .with("max_players", networkServer.getStatistics().getPlayersLimit())
                        .with("last_update", TimeUtil.formatTimeSimple(Duration.between(networkServer.getStatistics().getLastUpdate(), now)))
                        .apply();

                Material material = Material.GREEN_CONCRETE;
                if (networkServer.isOffline()) {
                    material = Material.RED_CONCRETE;
                }
                else if (networkServer.getStatistics().isFrozen()) {
                    material = Material.LIGHT_BLUE_CONCRETE;
                }

                gui.setItem(item.getSlot(), FlameItemBuilder.of(material)
                        .name(item.getName())
                        .lore(lore)
                        .flag(ItemFlag.HIDE_ATTRIBUTES)
                        .asGuiItem(event -> {

                            player.closeInventory();
                            this.redisMessenger.publish("queue", new QueuePlayerAddPacket(player.getName(), networkServer.getCategory()));

                        }));
            }

            gui.update();
        };

        fill.run();
        this.plugin.getServer().getScheduler().scheduleSyncRepeatingTask(this.plugin, fill, 0L, 20L);
        gui.open(player);

    }

    public void openServers(Player player) {
        Gui gui = Gui.gui()
                .rows(4)
                .title(TextUtil.parse("&8&lWybierz serwer"))
                .disableAllInteractions()
                .create();

        ServerSelectorItem lobbySelectorItem = this.serverSelectorConfig.getLobbySelectorItem();
        if (lobbySelectorItem != null) {
            gui.setItem(lobbySelectorItem.getSlot(), FlameItemBuilder.of(lobbySelectorItem.getIcon())
                    .name(lobbySelectorItem.getName())
                    .lore(lobbySelectorItem.getLore())
                    .asGuiItem(event -> openLobbies(player)));
        }

        ServerSelectorItem serverInfoItem = this.serverSelectorConfig.getServerInfoItem();
        if (serverInfoItem != null) {
            gui.setItem(serverInfoItem.getSlot(), FlameItemBuilder.of(serverInfoItem.getIcon())
                    .name(serverInfoItem.getName())
                    .lore(serverInfoItem.getLore())
                    .asGuiItem());
        }

        Runnable fill = () -> {

            Instant now = Instant.now();
            for (ServerSelector serverSelector : this.serverSelectorConfig.getServerSelectors()) {

                ServerSelectorItem item = serverSelector.getItem();
                Optional<NetworkServer> networkServer = this.networkServerCache.findByName(serverSelector.getInfoFrom());

                List<String> lore;
                if (networkServer.isEmpty()) {
                    lore = List.of(
                            "",
                            " &8Brak danych o tym serwerze...",
                            ""
                    );
                }
                else {

                    if (networkServer.get().getCategory().equals("lobby")) {
                        continue;
                    }

                    lore = BukkitMessage.from(item.getLore())
                            .with("online_players", this.networkServerCache.getPlayersFrom(serverSelector.getCategory()))
                            .with("max_players", this.networkServerCache.getPlayersLimitFrom(serverSelector.getCategory()))
                            .with("last_update", TimeUtil.formatTimeSimple(Duration.between(networkServer.get().getStatistics().getLastUpdate(), now)))
                            .with("start_date", serverSelector.getStartDate())
                            .apply();
                }


                gui.setItem(item.getSlot(), FlameItemBuilder.of(item.getIcon())
                        .name(item.getName())
                        .lore(lore)
                        .flag(ItemFlag.HIDE_ATTRIBUTES)
                        .asGuiItem(event -> {

                            player.closeInventory();
                            if (networkServer.isEmpty()) {
                                return;
                            }

                            this.redisMessenger.publish("queue", new QueuePlayerAddPacket(player.getName(), networkServer.get().getCategory()));

                        }));
            }

            gui.update();
        };

        fill.run();
        this.plugin.getServer().getScheduler().scheduleSyncRepeatingTask(this.plugin, fill, 0L, 20L);
        gui.open(player);
    }


}
