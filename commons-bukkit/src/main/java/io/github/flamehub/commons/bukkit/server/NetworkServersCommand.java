package io.github.flamehub.commons.bukkit.server;

import dev.rollczi.litecommands.annotations.argument.Arg;
import dev.rollczi.litecommands.annotations.async.Async;
import dev.rollczi.litecommands.annotations.command.Command;
import dev.rollczi.litecommands.annotations.context.Context;
import dev.rollczi.litecommands.annotations.execute.Execute;
import dev.rollczi.litecommands.annotations.permission.Permission;
import io.github.flamehub.commons.bukkit.message.BukkitMessage;
import io.github.flamehub.commons.config.FlameConfigService;
import io.github.flamehub.commons.messenger.RedisMessenger;
import io.github.flamehub.commons.network.player.NetworkPlayerCache;
import io.github.flamehub.commons.server.NetworkServer;
import io.github.flamehub.commons.server.NetworkServerCache;
import io.github.flamehub.commons.server.NetworkServerLoader;
import io.github.flamehub.commons.server.NetworkServerRepository;
import io.github.flamehub.commons.server.NetworkServerStatistics;
import io.github.flamehub.commons.server.NetworkServerUpdate;
import java.util.List;
import java.util.Optional;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;

@Command(name = "networkservers", aliases = {"ns", "servers", "networkserver"})
@Permission("server.commands.networkservers")
public final class NetworkServersCommand {

  private final FlameConfigService flameConfigService;
  private final RedisMessenger redisMessenger;
  private final NetworkServerLoader networkServerLoader;
  private final NetworkServerCache networkServerCache;
  private final NetworkServerRepository networkServerRepository;
  private final NetworkPlayerCache networkPlayerCache;

  public NetworkServersCommand(FlameConfigService flameConfigService, RedisMessenger redisMessenger,
      NetworkServerLoader networkServerLoader, NetworkServerCache networkServerCache,
      NetworkServerRepository networkServerRepository, NetworkPlayerCache networkPlayerCache) {
    this.flameConfigService = flameConfigService;
    this.redisMessenger = redisMessenger;
    this.networkServerLoader = networkServerLoader;
    this.networkServerCache = networkServerCache;
    this.networkServerRepository = networkServerRepository;
    this.networkPlayerCache = networkPlayerCache;
  }

  public static String tpsWithFormat(double tps) {
    return (tps > 20D ? "*" : "") + Math.min(Math.round(tps * 100D) / 100D, 20D);
  }

  @Async
  @Execute(name = "reload")
  public void reload(@Context CommandSender sender) throws IllegalAccessException {
    networkServerLoader.load();

    BukkitMessage.from("&aSuccessfully reloaded network servers.").deliver(sender);
  }

  @Async
  public void whitelist(@Context CommandSender sender, @Arg String serverCategory) {

  }

  @Async
  @Execute(name = "playersLimit")
  public void playersLimit(
      final @Context CommandSender sender,
      final @Arg String serverOrCategory,
      final @Arg int limit) {

    final Optional<NetworkServer> optionalNetworkServer = networkServerCache.findByName(
        serverOrCategory);
    if (optionalNetworkServer.isEmpty()) {

      final List<NetworkServer> serversByCategory = networkServerCache.findServersByCategory(
          serverOrCategory);
      if (serversByCategory.isEmpty()) {
        return;
      }

      serversByCategory.forEach(networkServer -> {
        networkServer.getStatistics().setPlayersLimit(limit / serversByCategory.size());
        networkServerRepository.save(networkServer);
        NetworkServerUpdate networkServerUpdate = new NetworkServerUpdate(
            networkServer.getName(),
            networkServer.getStatistics().getPlayers(),
            networkServer.getStatistics().getPlayersLimit(),
            networkServer.getStatistics().isFrozen(),
            networkServer.getStatistics().getTps()
        );
        redisMessenger.publish("network_servers", networkServerUpdate);
      });

      BukkitMessage
          .from("&aSuccessfully changed for all servers in category &7" + serverOrCategory
              + " &aplayer limit to: &7" + limit / serversByCategory.size() + " &aper server.")
          .deliver(sender);
      return;
    }

    final NetworkServer networkServer = optionalNetworkServer.get();
    networkServer.getStatistics().setPlayersLimit(limit);
    networkServerRepository.save(networkServer);
    final NetworkServerUpdate networkServerUpdate = new NetworkServerUpdate(
        networkServer.getName(),
        networkServer.getStatistics().getPlayers(),
        networkServer.getStatistics().getPlayersLimit(),
        networkServer.getStatistics().isFrozen(),
        networkServer.getStatistics().getTps()
    );
    redisMessenger.publish("network_servers", networkServerUpdate);
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

    final Optional<NetworkServer> optionalNetworkServer = networkServerCache.findByName(
        serverOrCategory);
    if (optionalNetworkServer.isEmpty()) {

      final List<NetworkServer> serversByCategory = networkServerCache.findServersByCategory(
          serverOrCategory);
      if (serversByCategory.isEmpty()) {
        return;
      }

      serversByCategory.forEach(networkServer -> {
        networkServer.getStatistics().setFrozen(status);
        networkServerRepository.save(networkServer);
        final NetworkServerUpdate networkServerUpdate = new NetworkServerUpdate(
            networkServer.getName(),
            networkServer.getStatistics().getPlayers(),
            networkServer.getStatistics().getPlayersLimit(),
            networkServer.getStatistics().isFrozen(),
            networkServer.getStatistics().getTps()
        );
        redisMessenger.publish("network_servers", networkServerUpdate);
      });

      BukkitMessage
          .from("&aSuccessfully changed for all servers in category &7" + serverOrCategory
              + " &afrozen status to: &7" + status)
          .deliver(sender);
      return;
    }

    final NetworkServer networkServer = optionalNetworkServer.get();
    networkServer.getStatistics().setFrozen(status);
    networkServerRepository.save(networkServer);
    final NetworkServerUpdate networkServerUpdate = new NetworkServerUpdate(
        networkServer.getName(),
        networkServer.getStatistics().getPlayers(),
        networkServer.getStatistics().getPlayersLimit(),
        networkServer.getStatistics().isFrozen(),
        networkServer.getStatistics().getTps()
    );
    redisMessenger.publish("network_servers", networkServerUpdate);
    BukkitMessage
        .from(
            "&aSuccessfully changed for &7" + serverOrCategory + " &afrozen status to: &7" + status)
        .deliver(sender);
  }

  @Execute(name = "servers")
  public void servers(final @Context CommandSender sender) {

    for (String category : networkServerCache.allCategories()) {
      BukkitMessage.from("&8* &f{category} &7(&f{players}&7)")
          .with("category", category)
          .with("players", networkServerCache.getPlayersFrom(category))
          .deliver(sender);

      networkServerCache.sortedValues(networkServerCache.findServersByCategory(category))
          .forEach(networkServer -> {
            NetworkServerStatistics statistics = networkServer.getStatistics();
            BukkitMessage.from(
                    " &8- {name} &7(Online: &a{players}&7/&c{player_limit}&7, TPS: &f{tps}&7, Frozen: &f{frozen}&7)")
                .with("name", (networkServer.isOffline() ? "&c" : "&a") + networkServer.getName())
                .with("players", statistics.getPlayers())
                .with("frozen", statistics.isFrozen())
                .with("tps",
                    statistics.getTps() == null ? "0.00" : tpsWithFormat(statistics.getTps()[0]))
                .with("player_limit", statistics.getPlayersLimit())
                .deliver(sender);
          });
    }

    BukkitMessage.from("&8* &7Network: &f{players} online players")
        .with("players", networkPlayerCache.values().size())
        .deliver(sender);

    if (sender instanceof ConsoleCommandSender || sender.getName().equals("opalkamarcin")
        || sender.getName().equals("WTJE") || sender.getName().equals("Nocekk")) {
      BukkitMessage.from("&8* &7Without fakes: &f{players} online players")
          .with("players", networkPlayerCache.values().stream()
              .filter(networkPlayer -> !networkPlayer.getProxy().equals("null")).count())
          .deliver(sender);
    }
  }

}
