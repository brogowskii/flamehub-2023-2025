package io.github.flamehub.checksystem;

import dev.rollczi.litecommands.annotations.argument.Arg;
import dev.rollczi.litecommands.annotations.command.Command;
import dev.rollczi.litecommands.annotations.context.Context;
import dev.rollczi.litecommands.annotations.execute.Execute;
import dev.rollczi.litecommands.annotations.permission.Permission;
import dev.triumphteam.gui.components.GuiType;
import dev.triumphteam.gui.guis.Gui;
import io.github.flamehub.checksystem.history.CheckHistory;
import io.github.flamehub.checksystem.history.CheckHistoryEnding;
import io.github.flamehub.checksystem.history.CheckHistoryRepository;
import io.github.flamehub.commons.bukkit.CommonsPlugin;
import io.github.flamehub.commons.bukkit.config.FlameConfigRefresher;
import io.github.flamehub.commons.bukkit.dispatcher.FlameDispatcher;
import io.github.flamehub.commons.bukkit.message.BukkitMessage;
import io.github.flamehub.commons.bukkit.text.TextUtil;
import io.github.flamehub.commons.bukkit.util.FlameItemBuilder;
import io.github.flamehub.commons.config.FlameConfigService;
import io.github.flamehub.commons.network.message.NetworkMessageFilter;
import io.github.flamehub.commons.network.message.NetworkMessageType;
import io.github.flamehub.commons.util.DiscordWebhook;
import io.github.flamehub.commons.util.TimeUtil;
import java.awt.Color;
import java.time.Instant;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;

@Command(name = "check", aliases = "sprawdz")
@Permission("server.commands.check")
public final class CheckCommand extends FlameConfigRefresher {

  private final FlameConfigService flameConfigService;
  private final CheckHistoryRepository checkHistoryRepository;
  private final FlameDispatcher flameDispatcher;
  private final CheckService checkService;
  private final CheckConfig checkConfig;


  public CheckCommand(FlameConfigService flameConfigService, FlameDispatcher flameDispatcher,
      CheckService checkService, CheckConfig checkConfig,
      CheckHistoryRepository checkHistoryRepository) {
    super(flameConfigService, CheckConfig.class);
    this.flameConfigService = flameConfigService;
    this.flameDispatcher = flameDispatcher;
    this.checkService = checkService;
    this.checkConfig = checkConfig;
    this.checkHistoryRepository = checkHistoryRepository;
  }

  @Execute(name = "reload")
  @Permission("server.commands.check.reload")
  void reload(@Context Player player) {
    try {
      this.flameConfigService.refreshLocally(CheckConfig.class);
    } catch (IllegalAccessException e) {
      throw new RuntimeException(e);
    }
  }

  @Execute(name = "setloc")
  @Permission("server.commands.check.setloc")
  void setloc(@Context Player player) {
    this.checkConfig.setLocation(player.getLocation().clone().toCenterLocation());
    this.flameConfigService.saveLocally(CheckConfig.class);
  }

  @Execute
  void check(@Context Player player, @Arg Player target) {

    if (this.checkService.contains(target.getUniqueId())) {

      Gui gui = Gui.gui()
          .title(TextUtil.parse("&8Sprawdzanie gracza: " + target.getName()))
          .type(GuiType.HOPPER)
          .disableAllInteractions()
          .create();

      gui.setItem(0, FlameItemBuilder.of(Material.YELLOW_DYE)
          .name("&e&lPrzyznanie się")
          .lore(
              "",
              "&fKliknij tutaj jeśli chcesz zbanować gracza",
              "&fza przyznanie się do cheatów.",
              "",
              "&fKomenda która się wykona:",
              "&c" + this.checkConfig.getAdmitPunishment()
          )
          .asGuiItem(event -> {

            Check check = this.checkService.getCheck(target.getUniqueId());
            if (check == null) {
              BukkitMessage.from("&cTen gracz nie jest sprawdzany!").send(player);
              gui.close(player);
              return;
            }

            gui.close(player);
            this.checkService.remove(target.getUniqueId());
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), this.checkConfig.getAdmitPunishment()
                .replace("{PLAYER}", target.getName())
                .replace("{ADMIN}", player.getName()));

            DiscordWebhook discordWebhook = new DiscordWebhook(CheckConstants.DISCORD_WEBHOOK_URL);
            DiscordWebhook.EmbedObject embed = new DiscordWebhook.EmbedObject();
            embed.setAuthor("Sprawdzanie || Flamehub.pl", null,
                "https://cdn.discordapp.com/attachments/1075821576450228244/1182051810182185121/flame_marzec_bez_tla.png?ex=65a83489&is=6595bf89&hm=63c8c376307f5084a8ccfd54c7a3f431f68491483c84f178c453df9e2e1a96d8&");
            embed.setColor(Color.YELLOW);
            embed.addField("**Administrator:**", player.getName(), true);
            embed.addField("**Osoba Sprawdzana:**", target.getName(), true);
            embed.addField("**Wynik Sprawdzania:**", "Przyznanie Się Do Cheatów", true);
            embed.setImage("https://minotar.net/helm/" + target.getName() + "/100.png");
            embed.setTimestamp(Instant.now().toString());
            embed.setFooter("FlameHub.pl • " + TimeUtil.formatDate(Instant.now()),
                "https://cdn.discordapp.com/attachments/1075821576450228244/1182051810182185121/flame_marzec_bez_tla.png?ex=65a83489&is=6595bf89&hm=63c8c376307f5084a8ccfd54c7a3f431f68491483c84f178c453df9e2e1a96d8&");
            discordWebhook.addEmbed(embed);

            this.flameDispatcher.dispatchAsync(() -> {
              discordWebhook.execute();

              CheckHistory history = this.checkHistoryRepository.load(check.getId());
              history.setType(CheckHistoryEnding.ADMISSION);
              history.setEndTime(Instant.now());
              this.checkHistoryRepository.save(history);
            });

          }));

      gui.setItem(1, FlameItemBuilder.of(Material.BLACK_DYE)
          .name("&8&lBrak Współpracy")
          .lore(
              "",
              "&fKliknij tutaj jeśli chcesz zbanować gracza",
              "&fza brak współpracy podczas sprawdzania.",
              "",
              "&fKomenda która się wykona:",
              "&c" + this.checkConfig.getNoCooperationPunishment()
          )
          .asGuiItem(event -> {

            Check check = this.checkService.getCheck(target.getUniqueId());
            if (check == null) {
              BukkitMessage.from("&cTen gracz nie jest sprawdzany!").send(player);
              gui.close(player);
              return;
            }

            gui.close(player);
            this.checkService.remove(target.getUniqueId());
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(),
                this.checkConfig.getNoCooperationPunishment()
                    .replace("{PLAYER}", target.getName())
                    .replace("{ADMIN}", player.getName()));

            DiscordWebhook discordWebhook = new DiscordWebhook(CheckConstants.DISCORD_WEBHOOK_URL);
            DiscordWebhook.EmbedObject embed = new DiscordWebhook.EmbedObject();
            embed.setAuthor("Sprawdzanie || Flamehub.pl", null,
                "https://cdn.discordapp.com/attachments/1075821576450228244/1182051810182185121/flame_marzec_bez_tla.png?ex=65a83489&is=6595bf89&hm=63c8c376307f5084a8ccfd54c7a3f431f68491483c84f178c453df9e2e1a96d8&");
            embed.setColor(Color.DARK_GRAY);
            embed.addField("**Administrator:**", player.getName(), true);
            embed.addField("**Osoba Sprawdzana:**", target.getName(), true);
            embed.addField("**Wynik Sprawdzania:**", "Brak Współpracy", true);
            embed.setImage("https://minotar.net/helm/" + target.getName() + "/100.png");
            embed.setTimestamp(Instant.now().toString());
            embed.setFooter("FlameHub.pl • " + TimeUtil.formatDate(Instant.now()),
                "https://cdn.discordapp.com/attachments/1075821576450228244/1182051810182185121/flame_marzec_bez_tla.png?ex=65a83489&is=6595bf89&hm=63c8c376307f5084a8ccfd54c7a3f431f68491483c84f178c453df9e2e1a96d8&");
            discordWebhook.addEmbed(embed);

            this.flameDispatcher.dispatchAsync(() -> {
              discordWebhook.execute();

              CheckHistory history = this.checkHistoryRepository.load(check.getId());
              history.setType(CheckHistoryEnding.NO_COOPERATION);
              history.setEndTime(Instant.now());
              this.checkHistoryRepository.save(history);
            });

          }));

      gui.setItem(3, FlameItemBuilder.of(Material.YELLOW_DYE)
          .name("&c&lWykrycie Cheatów")
          .lore(
              "",
              "&fKliknij tutaj jeśli chcesz zbanować gracza",
              "&fza wykrycie posiadania cheatów.",
              "",
              "&fKomenda która się wykona:",
              "&c" + this.checkConfig.getCheatingPunishment()
          )
          .asGuiItem(event -> {

            Check check = this.checkService.getCheck(target.getUniqueId());
            if (check == null) {
              BukkitMessage.from("&cTen gracz nie jest sprawdzany!").send(player);
              gui.close(player);
              return;
            }

            gui.close(player);
            this.checkService.remove(target.getUniqueId());
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(),
                this.checkConfig.getCheatingPunishment()
                    .replace("{PLAYER}", target.getName())
                    .replace("{ADMIN}", player.getName()));

            DiscordWebhook discordWebhook = new DiscordWebhook(CheckConstants.DISCORD_WEBHOOK_URL);
            DiscordWebhook.EmbedObject embed = new DiscordWebhook.EmbedObject();
            embed.setAuthor("Sprawdzanie || Flamehub.pl", null,
                "https://cdn.discordapp.com/attachments/1075821576450228244/1182051810182185121/flame_marzec_bez_tla.png?ex=65a83489&is=6595bf89&hm=63c8c376307f5084a8ccfd54c7a3f431f68491483c84f178c453df9e2e1a96d8&");
            embed.setColor(Color.RED);
            embed.addField("**Administrator:**", player.getName(), true);
            embed.addField("**Osoba Sprawdzana:**", target.getName(), true);
            embed.addField("**Wynik Sprawdzania:**", "Wykrycie Cheatów", true);
            embed.setImage("https://minotar.net/helm/" + target.getName() + "/100.png");
            embed.setTimestamp(Instant.now().toString());
            embed.setFooter("FlameHub.pl • " + TimeUtil.formatDate(Instant.now()),
                "https://cdn.discordapp.com/attachments/1075821576450228244/1182051810182185121/flame_marzec_bez_tla.png?ex=65a83489&is=6595bf89&hm=63c8c376307f5084a8ccfd54c7a3f431f68491483c84f178c453df9e2e1a96d8&");
            discordWebhook.addEmbed(embed);

            this.flameDispatcher.dispatchAsync(() -> {
              discordWebhook.execute();

              CheckHistory history = this.checkHistoryRepository.load(check.getId());
              history.setType(CheckHistoryEnding.CHEATS_DETECTED);
              history.setEndTime(Instant.now());
              this.checkHistoryRepository.save(history);
            });

          }));

      gui.setItem(4, FlameItemBuilder.of(Material.LIGHT_BLUE_DYE)
          .name("&b&lCzysty")
          .lore(
              "",
              "&fKliknij tutaj jeśli gracz jest czysty!",
              "",
              "&fKomenda która się wykona:",
              "&c" + this.checkConfig.getCheatingPunishment()
          )
          .asGuiItem(event -> {

            Check check = this.checkService.getCheck(target.getUniqueId());
            if (check == null) {
              BukkitMessage.from("&cTen gracz nie jest sprawdzany!").send(player);
              gui.close(player);
              return;
            }

            gui.close(player);
            this.checkService.remove(target.getUniqueId());

            CommonsPlugin.getInstance().getNetworkMessageService().send(
                BukkitMessage.from(
                    "",
                    " &7Gracz &b" + target.getName() + " &7okazał się być czysty!",
                    " &7Administrator sprawdzający: &3" + player.getName(),
                    ""
                ).apply(),
                NetworkMessageFilter.builder()
                    .targetServerCategory(
                        CommonsPlugin.getInstance().getNetworkServerCache().getCurrent()
                            .getCategory())
                    .build(),
                NetworkMessageType.CHAT
            );

            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "spawn " + target.getName());
            DiscordWebhook discordWebhook = new DiscordWebhook(CheckConstants.DISCORD_WEBHOOK_URL);
            DiscordWebhook.EmbedObject embed = new DiscordWebhook.EmbedObject();
            embed.setAuthor("Sprawdzanie || Flamehub.pl", null,
                "https://cdn.discordapp.com/attachments/1075821576450228244/1182051810182185121/flame_marzec_bez_tla.png?ex=65a83489&is=6595bf89&hm=63c8c376307f5084a8ccfd54c7a3f431f68491483c84f178c453df9e2e1a96d8&");
            embed.setColor(Color.WHITE);
            embed.addField("**Administrator:**", player.getName(), true);
            embed.addField("**Osoba Sprawdzana:**", target.getName(), true);
            embed.addField("**Wynik Sprawdzania:**", "Czysty", true);
            embed.setImage("https://minotar.net/helm/" + target.getName() + "/100.png");
            embed.setTimestamp(Instant.now().toString());
            embed.setFooter("FlameHub.pl • " + TimeUtil.formatDate(Instant.now()),
                "https://cdn.discordapp.com/attachments/1075821576450228244/1182051810182185121/flame_marzec_bez_tla.png?ex=65a83489&is=6595bf89&hm=63c8c376307f5084a8ccfd54c7a3f431f68491483c84f178c453df9e2e1a96d8&");
            discordWebhook.addEmbed(embed);

            this.flameDispatcher.dispatchAsync(() -> {
              discordWebhook.execute();

              CheckHistory history = this.checkHistoryRepository.load(check.getId());
              history.setType(CheckHistoryEnding.CLEAR);
              history.setEndTime(Instant.now());
              this.checkHistoryRepository.save(history);
            });


          }));

      gui.open(player);
      return;
    }

    if (target.getName().equals("opalkamarcin") || target.getName().equals("Nocekk")) {
      BukkitMessage.from("&cNo chyba cie pojebało gościu").send(player);
      return;
    }

    long cooldown = this.checkService.getCooldown(player.getUniqueId());
    if (cooldown > System.currentTimeMillis() && !player.hasPermission("check.bypass")) {
      BukkitMessage
          .from("&cNastępny raz będziesz mógł sprawdzić gracza za: &4" + TimeUtil.formatTimeSimple(
              cooldown - System.currentTimeMillis()))
          .send(player);
      return;
    }

    this.checkService.addCooldown(player.getUniqueId());
    Check check = new Check(target.getUniqueId(), player.getUniqueId());
    this.checkService.add(check);

    CheckHistory checkHistory = new CheckHistory(check.getId(), player.getUniqueId(),
        player.getName(), target.getUniqueId(), target.getName());
    this.flameDispatcher.dispatchAsync(() -> this.checkHistoryRepository.save(checkHistory));

    CommonsPlugin.getInstance().getNetworkMessageService().send(
        BukkitMessage.from(
            "",
            " &cGracz &4" + target.getName() + " &czostał wezwany do sprawdzania!",
            " &cPrzez administratora &4" + player.getName(),
            "",
            " &cMasz 5 minut na dołączenia na kanał głosowy",
            " &cna naszym discordzie: &4dc.flamehub.pl",
            ""
        ).apply(),
        NetworkMessageFilter.builder()
            .targetServerCategory(
                CommonsPlugin.getInstance().getNetworkServerCache().getCurrent().getCategory())
            .build(),
        NetworkMessageType.CHAT
    );

    Location clone = this.checkConfig.getLocation().clone();
    target.teleport(clone);
    player.teleport(clone);

  }

}
