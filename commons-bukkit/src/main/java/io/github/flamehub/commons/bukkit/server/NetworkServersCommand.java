package io.github.flamehub.commons.bukkit.server;

import dev.rollczi.litecommands.annotations.argument.Arg;
import dev.rollczi.litecommands.annotations.async.Async;
import dev.rollczi.litecommands.annotations.command.Command;
import dev.rollczi.litecommands.annotations.context.Context;
import dev.rollczi.litecommands.annotations.execute.Execute;
import dev.rollczi.litecommands.annotations.permission.Permission;
import io.github.flamehub.commons.bukkit.message.BukkitMessage;
import io.github.flamehub.commons.network.player.NetworkPlayerCache;
import io.github.flamehub.commons.server.NetworkServer;
import io.github.flamehub.commons.server.NetworkServerFacade;
import io.github.flamehub.commons.server.NetworkServerSettings;
import io.github.flamehub.commons.server.NetworkServerStatistics;
import java.util.List;
import java.util.Optional;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;

@Command(name = "networkservers", aliases = {"ns", "servers", "networkserver"})
@Permission("server.commands.networkservers")
public final class NetworkServersCommand {

  private final NetworkServerFacade networkServerFacade;
  private final NetworkPlayerCache networkPlayerCache;

  public NetworkServersCommand(
      final NetworkServerFacade networkServerFacade,
      final NetworkPlayerCache networkPlayerCache
  ) {
    this.networkServerFacade = networkServerFacade;
    this.networkPlayerCache = networkPlayerCache;
  }

  public static String tpsWithFormat(final double tps) {
    return (tps > 20D ? "*" : "") + Math.min(Math.round(tps * 100D) / 100D, 20D);
  }

  @Async
  @Execute(name = "playersLimit")
  public void playersLimit(
      final @Context CommandSender sender,
      final @Arg String serverOrCategory,
      final @Arg int limit) {

    final Optional<NetworkServer> optionalNetworkServer = networkServerFacade
        .findByName(serverOrCategory);
    if (optionalNetworkServer.isEmpty()) {

      final List<NetworkServer> serversByCategory = networkServerFacade.findServersByCategory(
          serverOrCategory);
      if (serversByCategory.isEmpty()) {
        return;
      }

      serversByCategory.forEach(networkServer -> {
        final NetworkServerSettings settings = networkServerFacade.getSetting(networkServer.getName());
        settings.setPlayersLimit(limit / serversByCategory.size());
        networkServerFacade.putSetting(networkServer.getName(), settings);
      });

      BukkitMessage
          .from("&aSuccessfully changed for all servers in category &7" + serverOrCategory
              + " &aplayer limit to: &7" + limit / serversByCategory.size() + " &aper server.")
          .deliver(sender);
      return;
    }

    final NetworkServer networkServer = optionalNetworkServer.get();
    final NetworkServerSettings settings = networkServerFacade.getSetting(networkServer.getName());
    settings.setPlayersLimit(limit);
    networkServerFacade.putSetting(networkServer.getName(), settings);

    BukkitMessage
        .from("&aSuccessfully changed players limit for server &7" + serverOrCategory + " &ato: &7"
            + limit)
        .deliver(sender);

  }

  @Async
  @Execute(name = "frozen")
  public void frozen(
      final @Context CommandSender sender,
      final @Arg String serverOrCategory,
      final @Arg boolean status) {

    final Optional<NetworkServer> optionalNetworkServer = networkServerFacade.findByName(
        serverOrCategory);
    if (optionalNetworkServer.isEmpty()) {

      final List<NetworkServer> serversByCategory = networkServerFacade.findServersByCategory(
          serverOrCategory);
      if (serversByCategory.isEmpty()) {
        return;
      }

      serversByCategory.forEach(networkServer -> {
        final NetworkServerSettings settings = networkServerFacade.getSetting(networkServer.getName());
        settings.setFrozen(status);
        networkServerFacade.putSetting(networkServer.getName(), settings);
      });

      BukkitMessage
          .from("&aSuccessfully changed for all servers in category &7" + serverOrCategory
              + " &afrozen status to: &7" + status)
          .deliver(sender);
      return;
    }

    final NetworkServer networkServer = optionalNetworkServer.get();
    final NetworkServerSettings settings = networkServerFacade.getSetting(networkServer.getName());
    settings.setFrozen(status);
    networkServerFacade.putSetting(networkServer.getName(), settings);

    BukkitMessage
        .from(
            "&aSuccessfully changed for &7" + serverOrCategory + " &afrozen status to: &7" + status)
        .deliver(sender);
  }

  @Execute(name = "servers")
  public void servers(final @Context CommandSender sender) {

    for (final String category : networkServerFacade.allCategories()) {
      BukkitMessage.from("&8* &f{category} &7(&f{players}&7)")
          .with("category", category)
          .with("players", networkServerFacade.getPlayersFrom(category))
          .deliver(sender);

      networkServerFacade.sortedValues(networkServerFacade.findServersByCategory(category))
          .forEach(networkServer -> {
            final NetworkServerStatistics statistics = networkServer.getStatistics();
            final NetworkServerSettings networkServerSettings = networkServerFacade.getSetting(networkServer.getName());
            BukkitMessage.from(
                    " &8- {name} &7(Online: &a{players}&7/&c{player_limit}&7, TPS: &f{tps}&7, Frozen: &f{frozen}&7)")
                .with("name", (networkServer.isOffline() ? "&c" : "&a") + networkServer.getName())
                .with("players", statistics.getPlayers())
                .with("frozen", networkServerSettings.isFrozen())
                .with("tps",
                    statistics.getTps() == null ? "0.00" : tpsWithFormat(statistics.getTps()[0]))
                .with("player_limit", networkServerSettings.getPlayersLimit())
                .deliver(sender);
          });
    }

    BukkitMessage.from("&8* &7Network: &f{players} online players")
        .with("players", networkPlayerCache.values().size())
        .deliver(sender);

    if (sender instanceof ConsoleCommandSender || "opalkamarcin".equals(sender.getName())
        || "WTJE".equals(sender.getName()) || "Nocekk".equals(sender.getName())) {
      BukkitMessage.from("&8* &7Without fakes: &f{players} online players")
          .with("players", networkPlayerCache.values().stream()
              .filter(networkPlayer -> !"null".equals(networkPlayer.getProxy())).count())
          .deliver(sender);
    }
  }

}
