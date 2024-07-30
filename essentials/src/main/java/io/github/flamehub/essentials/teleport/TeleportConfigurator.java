package io.github.flamehub.essentials.teleport;

import dev.rollczi.litecommands.LiteCommandsBuilder;
import dev.rollczi.litecommands.annotations.LiteCommandsAnnotations;
import dev.rollczi.litecommands.bukkit.LiteBukkitSettings;
import io.github.flamehub.commons.bukkit.BukkitConfigurator;
import io.github.flamehub.commons.bukkit.dispatcher.FlameDispatcher;
import io.github.flamehub.commons.messenger.RedisMessenger;
import io.github.flamehub.commons.server.NetworkServer;
import io.github.flamehub.commons.server.NetworkServerCache;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;

public final class TeleportConfigurator extends BukkitConfigurator {

  public TeleportFacade teleportFacade(
      final Plugin plugin,
      final LiteCommandsBuilder<CommandSender, LiteBukkitSettings, ?> liteCommandsBuilder,
      final FlameDispatcher flameDispatcher,
      final RedisMessenger redisMessenger,
      final NetworkServerCache networkServerCache
  ) {

    final TeleportFacade teleportFacade = new TeleportFacade();

    final NetworkServer current = networkServerCache.getCurrent();
    redisMessenger.subscribe(current.getName(),
        new TeleportPacketHandler(redisMessenger, teleportFacade));

    registerListeners(plugin, new TeleportListener(flameDispatcher, teleportFacade));

    liteCommandsBuilder.commands(LiteCommandsAnnotations.of(
        new TeleportCommand(plugin, flameDispatcher, redisMessenger, networkServerCache),
        new TeleportHereCommand()
    ));

    return teleportFacade;
  }

}
