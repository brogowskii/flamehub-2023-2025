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
import io.github.flamehub.commons.server.NetworkServer;
import io.github.flamehub.commons.server.NetworkServerCache;
import io.github.flamehub.commons.server.NetworkServerLoader;
import io.github.flamehub.commons.server.NetworkServerRepository;
import io.github.flamehub.commons.server.NetworkServerStatistics;
import io.github.flamehub.commons.server.NetworkServerUpdate;
import java.util.List;
import java.util.Optional;
import org.bukkit.command.CommandSender;

@Command(name = "networkservers", aliases = {"ns", "servers", "networkserver"})
@Permission("server.commands.networkservers")
public final class NetworkServersCommand {

  private final FlameConfigService flameConfigService;
  private final RedisMessenger redisMessenger;
  private final NetworkServerLoader networkServerLoader;
  private final NetworkServerCache networkServerCache;
  private final NetworkServerRepository networkServerRepository;

  public NetworkServersCommand(FlameConfigService flameConfigService, RedisMessenger redisMessenger,
      NetworkServerLoader networkServerLoader, NetworkServerCache networkServerCache,
      NetworkServerRepository networkServerRepository) {
    this.flameConfigService = flameConfigService;
    this.redisMessenger = redisMessenger;
    this.networkServerLoader = networkServerLoader;
    this.networkServerCache = networkServerCache;
    this.networkServerRepository = networkServerRepository;
  }

  public static String tpsWithFormat(double tps) {
    return (tps > 20D ? "*" : "") + Math.min(Math.round(tps * 100D) / 100D, 20D);
  }

  @Async
  @Execute(name = "reload")
  public void reload(@Context CommandSender sender) throws IllegalAccessException {
    this.networkServerLoader.load();

    BukkitMessage.from("&aSuccessfully reloaded network servers.").send(sender);
  }

  @Async
  public void whitelist(@Context CommandSender sender, @Arg String serverCategory) {

  }

  @Async
  @Execute(name = "playersLimit")
  public void playersLimit(@Context CommandSender sender, @Arg String serverOrCategory,
      @Arg int limit) {

    Optional<NetworkServer> optionalNetworkServer = this.networkServerCache.findByName(
        serverOrCategory);
    if (optionalNetworkServer.isEmpty()) {

      List<NetworkServer> serversByCategory = this.networkServerCache.findServersByCategory(
          serverOrCategory);
      if (serversByCategory.isEmpty()) {
        return;
      }

      serversByCategory.forEach(networkServer -> {
        networkServer.getStatistics().setPlayersLimit(limit / serversByCategory.size());
        this.networkServerRepository.save(networkServer);
        NetworkServerUpdate networkServerUpdate = new NetworkServerUpdate(
            networkServer.getName(),
            networkServer.getStatistics().getPlayers(),
            networkServer.getStatistics().getPlayersLimit(),
            networkServer.getStatistics().isFrozen(),
            networkServer.getStatistics().getTps()
        );
        this.redisMessenger.publish("network_servers", networkServerUpdate);
      });

      BukkitMessage
          .from("&aSuccessfully changed for all servers in category &7" + serverOrCategory
              + " &aplayer limit to: &7" + limit / serversByCategory.size() + " &aper server.")
          .send(sender);
      return;
    }

    NetworkServer networkServer = optionalNetworkServer.get();
    networkServer.getStatistics().setPlayersLimit(limit);
    this.networkServerRepository.save(networkServer);
    NetworkServerUpdate networkServerUpdate = new NetworkServerUpdate(
        networkServer.getName(),
        networkServer.getStatistics().getPlayers(),
        networkServer.getStatistics().getPlayersLimit(),
        networkServer.getStatistics().isFrozen(),
        networkServer.getStatistics().getTps()
    );
    this.redisMessenger.publish("network_servers", networkServerUpdate);
    BukkitMessage
        .from("&aSuccessfully changed players limit for server &7" + serverOrCategory + " &ato: &7"
            + limit)
        .send(sender);

  }

  @Async
  @Execute(name = "frozen")
  public void frozen(@Context CommandSender sender, @Arg String serverOrCategory,
      @Arg boolean status) {

    Optional<NetworkServer> optionalNetworkServer = this.networkServerCache.findByName(
        serverOrCategory);
    if (optionalNetworkServer.isEmpty()) {

      List<NetworkServer> serversByCategory = this.networkServerCache.findServersByCategory(
          serverOrCategory);
      if (serversByCategory.isEmpty()) {
        return;
      }

      serversByCategory.forEach(networkServer -> {
        networkServer.getStatistics().setFrozen(status);
        this.networkServerRepository.save(networkServer);
        NetworkServerUpdate networkServerUpdate = new NetworkServerUpdate(
            networkServer.getName(),
            networkServer.getStatistics().getPlayers(),
            networkServer.getStatistics().getPlayersLimit(),
            networkServer.getStatistics().isFrozen(),
            networkServer.getStatistics().getTps()
        );
        this.redisMessenger.publish("network_servers", networkServerUpdate);
      });

      BukkitMessage
          .from("&aSuccessfully changed for all servers in category &7" + serverOrCategory
              + " &afrozen status to: &7" + status)
          .send(sender);
      return;
    }

    NetworkServer networkServer = optionalNetworkServer.get();
    networkServer.getStatistics().setFrozen(status);
    this.networkServerRepository.save(networkServer);
    NetworkServerUpdate networkServerUpdate = new NetworkServerUpdate(
        networkServer.getName(),
        networkServer.getStatistics().getPlayers(),
        networkServer.getStatistics().getPlayersLimit(),
        networkServer.getStatistics().isFrozen(),
        networkServer.getStatistics().getTps()
    );
    this.redisMessenger.publish("network_servers", networkServerUpdate);
    BukkitMessage
        .from(
            "&aSuccessfully changed for &7" + serverOrCategory + " &afrozen status to: &7" + status)
        .send(sender);
  }

  @Execute(name = "servers")
  public void servers(@Context CommandSender sender) {

    for (String category : this.networkServerCache.allCategories()) {
      BukkitMessage.from("&8* &f{category} &7(&f{players}&7)")
          .with("category", category)
          .with("players", this.networkServerCache.getPlayersFrom(category))
          .send(sender);

      this.networkServerCache.sortedValues(this.networkServerCache.findServersByCategory(category))
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
                .send(sender);
          });
    }

    BukkitMessage.from("&8* &7Network: &f{players} online players")
        .with("players", this.networkServerCache.getPlayersFrom("proxy"))
        .send(sender);
  }

}
