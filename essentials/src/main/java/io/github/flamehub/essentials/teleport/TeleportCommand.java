package io.github.flamehub.essentials.teleport;

import dev.rollczi.litecommands.annotations.argument.Arg;
import dev.rollczi.litecommands.annotations.command.Command;
import dev.rollczi.litecommands.annotations.context.Context;
import dev.rollczi.litecommands.annotations.execute.Execute;
import dev.rollczi.litecommands.annotations.optional.OptionalArg;
import dev.rollczi.litecommands.annotations.permission.Permission;
import io.github.flamehub.commons.bukkit.dispatcher.FlameDispatcher;
import io.github.flamehub.commons.bukkit.message.BukkitMessage;
import io.github.flamehub.commons.messenger.RedisMessenger;
import io.github.flamehub.commons.network.player.NetworkPlayer;
import io.github.flamehub.commons.redirect.RedirectPacket;
import io.github.flamehub.commons.server.NetworkServer;
import io.github.flamehub.commons.server.NetworkServerCache;
import io.github.flamehub.player.sync.data.PlayerSyncDataFactory;
import io.github.flamehub.player.sync.data.PlayerSyncDataRepository;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.ServicesManager;

@Command(name = "teleport", aliases = "tp")
@Permission("server.commands.teleport")
final class TeleportCommand {

  private final FlameDispatcher flameDispatcher;
  private final RedisMessenger redisMessenger;
  private final NetworkServerCache networkServerCache;

  private final PlayerSyncDataRepository playerSyncDataRepository;

  TeleportCommand(
      final Plugin plugin,
      final FlameDispatcher flameDispatcher,
      final RedisMessenger redisMessenger,
      final NetworkServerCache networkServerCache
  ) {
    this.flameDispatcher = flameDispatcher;
    this.redisMessenger = redisMessenger;
    this.networkServerCache = networkServerCache;
    final ServicesManager servicesManager = plugin.getServer().getServicesManager();
    final RegisteredServiceProvider<PlayerSyncDataRepository> registration = servicesManager.getRegistration(
        PlayerSyncDataRepository.class);
    this.playerSyncDataRepository = registration != null ? registration.getProvider() : null;
  }


  @Execute
  void teleportSelf(@Context Player sender, @Arg NetworkPlayer to) {

    NetworkServer current = networkServerCache.getCurrent();
    if (current.getName().equalsIgnoreCase(to.getServer())) {
      Player target = Bukkit.getPlayer(to.getName());
      if (target == null) {
        return;
      }

      sender.teleport(target);
      return;
    }

    if (playerSyncDataRepository == null) {
      return;
    }

    CompletableFuture.supplyAsync(
            () -> playerSyncDataRepository.save(PlayerSyncDataFactory.create(sender)))
        .thenAccept(playerSyncData -> {

          BukkitMessage.from("&aPomyślnie zapisano twoje dane, teleportuje do: &2" + to.getName())
              .deliver(sender);
          redisMessenger.<TeleportPacketResponse>publishFuture(to.getServer(),
                  new TeleportPacketRequest(sender.getUniqueId(), to.getName()))
              .thenAcceptAsync(response -> {

                redisMessenger.publish("redirect",
                    new RedirectPacket(sender.getName(), to.getServer()));

              });

        })
        .exceptionally(throwable -> {
          BukkitMessage.from(
                  "&cWystąpił błąd podczas zapisywania danych lub teleportacji do tego gracza!")
              .deliver(sender);
          throwable.printStackTrace();
          return null;
        });
  }

  @Execute
  @Permission("server.commands.teleport.others")
  void teleportSelfToPosition(@Context Player sender,
      @Arg Location location, @OptionalArg Player target, @OptionalArg World world) {
    location.setWorld(Objects.requireNonNullElseGet(world, sender::getWorld));

    if (target != null) {
      target.teleport(location);
      return;
    }

    sender.teleport(location);
  }


}
