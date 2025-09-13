package io.github.flamehub.commons.bukkit.listener;

import io.github.flamehub.commons.bukkit.message.BukkitMessagesService;
import java.util.List;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerCommandSendEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public final class ProtectorListener implements Listener {

  private final static List<String> DISALLOWED_COMMANDS = List.of(
      "plugins",
      "pl",
      "version",
      "ver",
      "about",
      "help",
      "?",
      "icanhasbukkit",
      "me",
      "tellraw",
      "teammsg"
  );

  private final BukkitMessagesService bukkitmessagesService;

  public ProtectorListener(final BukkitMessagesService bukkitmessagesService) {
    this.bukkitmessagesService = bukkitmessagesService;
  }

  @EventHandler(priority = EventPriority.LOWEST)
  public void tabProtect(final PlayerCommandSendEvent event) {

    event.getCommands().removeIf(command -> command.startsWith("minecraft:"));
    event.getCommands().removeIf(command -> command.startsWith("ess"));

    for (final String s : DISALLOWED_COMMANDS) {
      event.getCommands().remove(s);
      event.getCommands().remove("bukkit:" + s);
    }

  }

  @EventHandler(ignoreCancelled = true, priority = EventPriority.LOWEST)
  public void onCommandPreprocess(final PlayerCommandPreprocessEvent event) {
    final String[] splitMessage = event.getMessage().toLowerCase().split(" ");
    final String replace = splitMessage[0].replace("/", "");
    final Player player = event.getPlayer();

    if (replace.startsWith("minecraft:") && !player.hasPermission("protector.bypass")) {
      event.setCancelled(true);
      return;
    }

    if (DISALLOWED_COMMANDS.contains(replace) || replace.startsWith("bukkit:")) {
      event.setCancelled(true);
      bukkitmessagesService.sendMessage(player, "cmd.disallowed");
    }

  }

  @EventHandler
  public void onJoin(final PlayerJoinEvent event) {
    event.joinMessage(Component.empty());
  }

  @EventHandler
  public void onQuit(final PlayerQuitEvent event) {
    event.quitMessage(Component.empty());
  }

}
