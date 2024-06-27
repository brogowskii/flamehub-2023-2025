package io.github.flamehub.commons.bukkit.server;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;
import io.github.flamehub.commons.server.NetworkServer;
import io.github.flamehub.commons.server.NetworkServerCache;

import java.util.Optional;

public final class NetworkServerPlaceholder extends PlaceholderExpansion {

    private final NetworkServerCache networkServerCache;

    public NetworkServerPlaceholder(NetworkServerCache networkServerCache) {
        this.networkServerCache = networkServerCache;
    }

    @Override
    public @NotNull String getIdentifier() {
        return "networkserver";
    }

    @Override
    public @NotNull String getAuthor() {
        return "MarcinOpalka";
    }

    @Override
    public @NotNull String getVersion() {
        return "1.0";
    }

    @Override
    public String onRequest(OfflinePlayer player, @NotNull String params) {

        if (params.contains("players:")) {

            String[] split = params.split(":");
            String server = split[1];
            Optional<NetworkServer> networkServer = this.networkServerCache.findByName(server);
            return networkServer.map(value -> String.valueOf(value.getStatistics().getPlayers())).orElse("");

        }

        else if (params.contains("players-by-category:")) {
            String[] split = params.split(":");
            String category = split[1];
            long playersFrom = this.networkServerCache.getPlayersFrom(category);
            return String.valueOf(playersFrom);
        }

        switch (params) {

            case "current-name" -> {
                return this.networkServerCache.getCurrent().getName();
            }
            case "current-name-upper" -> {
                return this.networkServerCache.getCurrent().getName().toUpperCase();
            }
            case "current-category" -> {
                return this.networkServerCache.getCurrent().getCategory();
            }
            case "current-category-upper" -> {
                return this.networkServerCache.getCurrent().getCategory().toUpperCase();
            }
            case "global-players" -> {
                return String.valueOf(this.networkServerCache.getPlayersFrom("proxy"));
            }
            case "current-players" -> {
                return String.valueOf(Bukkit.getOnlinePlayers().size());
            }
        }

        return "";
    }
}
