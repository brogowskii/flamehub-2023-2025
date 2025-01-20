package io.github.flamehub.checksystem;

import dev.rollczi.litecommands.annotations.command.Command;
import dev.rollczi.litecommands.annotations.context.Context;
import dev.rollczi.litecommands.annotations.execute.Execute;
import io.github.flamehub.checksystem.history.CheckHistory;
import io.github.flamehub.checksystem.history.CheckHistoryEnding;
import io.github.flamehub.checksystem.history.CheckHistoryRepository;
import io.github.flamehub.commons.bukkit.dispatcher.FlameDispatcher;
import io.github.flamehub.commons.bukkit.message.BukkitMessage;
import io.github.flamehub.commons.util.DiscordWebhook;
import io.github.flamehub.commons.util.TimeUtil;
import java.awt.Color;
import java.time.Instant;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

@Command(name = "przyznajesie")
public final class CheckAdmissionCommand {

  private final FlameDispatcher flameDispatcher;
  private final CheckService checkService;
  private final CheckConfig checkConfig;
  private final CheckHistoryRepository checkHistoryRepository;

  public CheckAdmissionCommand(FlameDispatcher flameDispatcher, CheckService checkService,
      CheckConfig checkConfig, CheckHistoryRepository checkHistoryRepository) {
    this.flameDispatcher = flameDispatcher;
    this.checkService = checkService;
    this.checkConfig = checkConfig;
    this.checkHistoryRepository = checkHistoryRepository;
  }

  @Execute
  void execute(@Context Player player) {
    Check check = checkService.getCheck(player.getUniqueId());
    if (check == null) {
      BukkitMessage.from(
              "&cAha, aha, aha przyznajesz sie do cheatow nie będac sprawdzanym? Nieźle masz na bani ziomek.")
          .deliver(player);
      return;
    }

    checkService.remove(player.getUniqueId());
    Player admin = Bukkit.getPlayer(check.getAdmin());
    Bukkit.dispatchCommand(Bukkit.getConsoleSender(), checkConfig.getAdmitPunishment()
        .replace("{PLAYER}", player.getName())
        .replace("{ADMIN}", admin == null ? "Brak" : admin.getName())
    );

    DiscordWebhook discordWebhook = new DiscordWebhook(CheckConstants.DISCORD_WEBHOOK_URL);
    DiscordWebhook.EmbedObject embed = new DiscordWebhook.EmbedObject();
    embed.setAuthor("Sprawdzanie || Flamehub.pl", null,
        "https://cdn.discordapp.com/attachments/1075821576450228244/1182051810182185121/flame_marzec_bez_tla.png?ex=65a83489&is=6595bf89&hm=63c8c376307f5084a8ccfd54c7a3f431f68491483c84f178c453df9e2e1a96d8&");
    embed.setColor(Color.RED);
    embed.addField("**Administrator:**", Bukkit.getPlayer(check.getAdmin()) == null ? "null"
        : Bukkit.getPlayer(check.getAdmin()).getName(), true);
    embed.addField("**Osoba Sprawdzana:**", player.getName(), true);
    embed.addField("**Wynik Sprawdzania:**", "Przyznanie się do cheatów", true);
    embed.setImage("https://minotar.net/helm/" + player.getName() + "/100.png");
    embed.setTimestamp(Instant.now().toString());
    embed.setFooter("FlameHub.pl • " + TimeUtil.formatDate(Instant.now()),
        "https://cdn.discordapp.com/attachments/1075821576450228244/1182051810182185121/flame_marzec_bez_tla.png?ex=65a83489&is=6595bf89&hm=63c8c376307f5084a8ccfd54c7a3f431f68491483c84f178c453df9e2e1a96d8&");
    discordWebhook.addEmbed(embed);

    flameDispatcher.dispatchAsync(() -> {
      discordWebhook.execute();

      CheckHistory history = checkHistoryRepository.load(check.getId());
      history.setType(CheckHistoryEnding.ADMISSION);
      history.setEndTime(Instant.now());
      checkHistoryRepository.save(history);
    });

  }

}
