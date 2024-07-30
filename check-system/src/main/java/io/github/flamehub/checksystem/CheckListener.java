package io.github.flamehub.checksystem;

import io.github.flamehub.checksystem.history.CheckHistory;
import io.github.flamehub.checksystem.history.CheckHistoryEnding;
import io.github.flamehub.checksystem.history.CheckHistoryRepository;
import io.github.flamehub.commons.bukkit.dispatcher.FlameDispatcher;
import io.github.flamehub.commons.bukkit.message.BukkitMessage;
import io.github.flamehub.commons.util.DiscordWebhook;
import io.github.flamehub.commons.util.TimeUtil;
import java.awt.Color;
import java.time.Instant;
import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public final class CheckListener implements Listener {

  private final static List<String> ALLOWED_COMMANDS = List.of(
      "/przyznajesie",
      "/msg",
      "/tell",
      "/message",
      "/r",
      "/reply",
      "/tell",
      "/helpop"
  );

  private final FlameDispatcher flameDispatcher;
  private final CheckService checkService;
  private final CheckConfig checkConfig;
  private final CheckHistoryRepository checkHistoryRepository;

  public CheckListener(FlameDispatcher flameDispatcher, CheckService checkService,
      CheckConfig checkConfig, CheckHistoryRepository checkHistoryRepository) {
    this.flameDispatcher = flameDispatcher;
    this.checkService = checkService;
    this.checkConfig = checkConfig;
    this.checkHistoryRepository = checkHistoryRepository;
  }

  @EventHandler(ignoreCancelled = true)
  public void onChat(AsyncPlayerChatEvent event) {

    Player player = event.getPlayer();
    Check check = this.checkService.getCheck(player.getUniqueId());
    if (check == null) {
      return;
    }

    event.setCancelled(true);
    Player admin = Bukkit.getPlayer(check.getAdmin());
    if (admin == null) {
      BukkitMessage.from(
              "&cAdmin który cie sprawdzał, prawdopodobnie się wylogował lub zmienił kanał, jeżeli chcesz sie z nim skontaktować spróbuj użyć komendy /msg")
          .send(player);
      return;
    }

    this.flameDispatcher.dispatchAsync(() -> {
      CheckHistory history = this.checkHistoryRepository.load("checkedPlayerNickname",
          player.getName());
      history.getCheckedPlayerChatHistory().add(event.getMessage());
      this.checkHistoryRepository.save(history);
    });

    BukkitMessage.from("&8[&c&lSPRAWDZANY&8] &7" + player.getName() + ": &f" + event.getMessage())
        .send(List.of(player, admin));

  }

  @EventHandler(ignoreCancelled = true)
  public void onCommand(PlayerCommandPreprocessEvent event) {
    String message = event.getMessage().toLowerCase();
    Player player = event.getPlayer();

    if (event.isCancelled()) {
      event.setCancelled(true);
    }

    if (!this.checkService.contains(player.getUniqueId())) {
      return;
    }

    if (player.hasPermission("server.check.bypass")) {
      return;
    }

    boolean isAllowed = false;
    for (String allowedCommand : ALLOWED_COMMANDS) {
      if (message.startsWith(allowedCommand.toLowerCase())) {
        isAllowed = true;
        break;
      }
    }

    if (!isAllowed) {
      event.setCancelled(true);
      BukkitMessage.from("&cNie możesz użyć tej komendy będąc sprawdzanym.").send(player);
    }

  }

  @EventHandler(priority = EventPriority.HIGHEST)
  public void onQuit(PlayerQuitEvent event) {
    Player player = event.getPlayer();
    Check check = this.checkService.getCheck(player.getUniqueId());
    if (check == null) {
      return;
    }

    Player admin = Bukkit.getPlayer(check.getAdmin());
    Bukkit.dispatchCommand(Bukkit.getConsoleSender(), this.checkConfig.getLogoutPunishment()
        .replace("{PLAYER}", player.getName())
        .replace("{ADMIN}", admin == null ? "Brak" : admin.getName()));
    this.checkService.remove(player.getUniqueId());

    DiscordWebhook discordWebhook = new DiscordWebhook(CheckConstants.DISCORD_WEBHOOK_URL);
    DiscordWebhook.EmbedObject embed = new DiscordWebhook.EmbedObject();
    embed.setAuthor("Sprawdzanie || Flamehub.pl", null,
        "https://cdn.discordapp.com/attachments/1075821576450228244/1182051810182185121/flame_marzec_bez_tla.png?ex=65a83489&is=6595bf89&hm=63c8c376307f5084a8ccfd54c7a3f431f68491483c84f178c453df9e2e1a96d8&");
    embed.setColor(Color.RED);
    embed.addField("**Administrator:**", Bukkit.getPlayer(check.getAdmin()) == null ? "null"
        : Bukkit.getPlayer(check.getAdmin()).getName(), true);
    embed.addField("**Osoba Sprawdzana:**", player.getName(), true);
    embed.addField("**Wynik Sprawdzania:**", "Wylogowanie się podczas sprawdzania", true);
    embed.setImage("https://minotar.net/helm/" + player.getName() + "/100.png");
    embed.setTimestamp(Instant.now().toString());
    embed.setFooter("FlameHub.pl • " + TimeUtil.formatDate(Instant.now()),
        "https://cdn.discordapp.com/attachments/1075821576450228244/1182051810182185121/flame_marzec_bez_tla.png?ex=65a83489&is=6595bf89&hm=63c8c376307f5084a8ccfd54c7a3f431f68491483c84f178c453df9e2e1a96d8&");
    discordWebhook.addEmbed(embed);

    this.flameDispatcher.dispatchAsync(() -> {
      discordWebhook.execute();

      CheckHistory history = this.checkHistoryRepository.load(check.getId());
      history.setType(CheckHistoryEnding.LOGOUT);
      history.setEndTime(Instant.now());
      this.checkHistoryRepository.save(history);
    });


  }

  @EventHandler
  public void onKick(PlayerKickEvent event) {
    if (event.getCause() == PlayerKickEvent.Cause.SPAM) {
      return;
    }

    Player player = event.getPlayer();
    if (this.checkService.contains(player.getUniqueId())) {
      this.checkService.remove(player.getUniqueId());
    }
  }
}
