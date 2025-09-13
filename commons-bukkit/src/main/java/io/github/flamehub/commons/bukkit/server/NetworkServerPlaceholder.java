package io.github.flamehub.commons.bukkit.server;

import io.github.flamehub.commons.network.player.NetworkPlayerCache;
import io.github.flamehub.commons.server.NetworkServer;
import io.github.flamehub.commons.server.NetworkServerFacade;
import java.util.Optional;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;

public final class NetworkServerPlaceholder extends PlaceholderExpansion {

  private final NetworkServerFacade networkServerFacade;
  private final NetworkPlayerCache networkPlayerCache;

  public NetworkServerPlaceholder(final NetworkServerFacade networkServerFacade,
      final NetworkPlayerCache networkPlayerCache) {
    this.networkServerFacade = networkServerFacade;
    this.networkPlayerCache = networkPlayerCache;
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
      final Optional<NetworkServer> networkServer = networkServerFacade.findByName(server);
      return networkServer.map(value -> String.valueOf(value.getStatistics().getPlayers()))
          .orElse("");

    }
    if (params.contains("players-by-category:")) {
      final String[] split = params.split(":");
      final String category = split[1];
      final long playersFrom = networkServerFacade.getPlayersFrom(category);
      return String.valueOf(playersFrom);
    }

    switch (params) {

      case "current-name" -> {
        return networkServerFacade.getCurrent().getName();
      }
      case "current-name-upper" -> {
        return networkServerFacade.getCurrent().getName().toUpperCase();
      }
      case "current-category" -> {
        return networkServerFacade.getCurrent().getCategory();
      }
      case "current-category-upper" -> {
        return networkServerFacade.getCurrent().getCategory().toUpperCase();
      }
      case "global-players" -> {
        return String.valueOf(networkPlayerCache.values().size());
      }
      case "current-players" -> {
        return String.valueOf(Bukkit.getOnlinePlayers().size());
      }
    }

    return "";
  }
}
