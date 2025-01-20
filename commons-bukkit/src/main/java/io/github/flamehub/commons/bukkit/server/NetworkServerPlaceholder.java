package io.github.flamehub.commons.bukkit.server;

import io.github.flamehub.commons.server.NetworkServer;
import io.github.flamehub.commons.server.NetworkServerCache;
import java.util.Optional;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;

public final class NetworkServerPlaceholder extends PlaceholderExpansion {

  private final NetworkServerCache networkServerCache;

  public NetworkServerPlaceholder(final NetworkServerCache networkServerCache) {
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
  public String onRequest(final OfflinePlayer player, final @NotNull String params) {

    if (params.contains("players:")) {

      final String[] split = params.split(":");
      final String server = split[1];
      final Optional<NetworkServer> networkServer = networkServerCache.findByName(server);
      return networkServer.map(value -> String.valueOf(value.getStatistics().getPlayers()))
          .orElse("");

    } else if (params.contains("players-by-category:")) {
      final String[] split = params.split(":");
      final String category = split[1];
      final long playersFrom = networkServerCache.getPlayersFrom(category);
      return String.valueOf(playersFrom);
    }

    switch (params) {

      case "current-name" -> {
        return networkServerCache.getCurrent().getName();
      }
      case "current-name-upper" -> {
        return networkServerCache.getCurrent().getName().toUpperCase();
      }
      case "current-category" -> {
        return networkServerCache.getCurrent().getCategory();
      }
      case "current-category-upper" -> {
        return networkServerCache.getCurrent().getCategory().toUpperCase();
      }
      case "global-players" -> {
        return String.valueOf(networkServerCache.getPlayersFrom("proxy"));
      }
      case "current-players" -> {
        return String.valueOf(Bukkit.getOnlinePlayers().size());
      }
    }

    return "";
  }
}
