package io.github.flamehub.tiktok.video.verify;

import dev.triumphteam.gui.guis.BaseGui;
import dev.triumphteam.gui.guis.Gui;
import dev.triumphteam.gui.guis.PaginatedGui;
import io.github.flamehub.commons.bukkit.CommonsPlugin;
import io.github.flamehub.commons.bukkit.dispatcher.FlameDispatcher;
import io.github.flamehub.commons.bukkit.message.BukkitMessage;
import io.github.flamehub.commons.bukkit.text.TextUtil;
import io.github.flamehub.commons.bukkit.util.FlameItemBuilder;
import io.github.flamehub.commons.bukkit.util.GuiHelper;
import io.github.flamehub.commons.bukkit.util.SkullBuilder;
import io.github.flamehub.commons.messenger.RedisMessenger;
import io.github.flamehub.commons.network.message.NetworkMessageFilter;
import io.github.flamehub.commons.network.message.NetworkMessageType;
import io.github.flamehub.commons.util.DiscordWebhook;
import io.github.flamehub.commons.util.RoundUtil;
import io.github.flamehub.commons.util.TimeUtil;
import io.github.flamehub.tiktok.TikTokConstants;
import io.github.flamehub.tiktok.user.TikTokUserCache;
import java.awt.Color;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;

public final class TikTokVideoVerifyGui {

  private final FlameDispatcher flameDispatcher;
  private final RedisMessenger redisMessenger;
  private final TikTokUserCache tikTokUserCache;
  private final TikTokVideoVerifyCache tikTokVideoVerifyCache;
  private final TikTokVideoVerifyRepository tikTokVideoVerifyRepository;

  private TikTokVideoVerifyFilter filter = TikTokVideoVerifyFilter.ONLY_WAITING;
  private TikTokVideoVerifySort sort = TikTokVideoVerifySort.NEWEST;

  public TikTokVideoVerifyGui(
      final FlameDispatcher flameDispatcher,
      final RedisMessenger redisMessenger,
      final TikTokUserCache tikTokUserCache,
      final TikTokVideoVerifyCache tikTokVideoVerifyCache,
      final TikTokVideoVerifyRepository tikTokVideoVerifyRepository) {
    this.flameDispatcher = flameDispatcher;
    this.redisMessenger = redisMessenger;
    this.tikTokUserCache = tikTokUserCache;
    this.tikTokVideoVerifyCache = tikTokVideoVerifyCache;
    this.tikTokVideoVerifyRepository = tikTokVideoVerifyRepository;
  }

  public void open(final Player player, final int page) {

    final PaginatedGui gui = Gui.paginated()
        .title(TextUtil.parse(""))
        .rows(6)
        .pageSize(28)
        .disableAllInteractions()
        .create();

    GuiHelper.fillGui6(gui);

    gui.setItem(6, 5, FlameItemBuilder.of(Material.HOPPER)
        .name(
            "&#29ADCF♻ &8| &#29ADCF&lꜰ&#29A9CF&lɪ&#29A6CF&lʟ&#29A2CF&lᴛ&#299FCF&lʀ&#299BCF&lᴏ&#299FCF&lᴡ&#29A2CF&lᴀ&#29A6CF&lɴ&#29A9CF&lɪ&#29ADCF&lᴇ")
        .lore(BukkitMessage.from(
                "",
                " {NONE}",
                " {WAITING}",
                " {ACCEPTED}",
                " {BLOCKED}",
                "",
                "&bKliknij, aby zmienić filtrowanie."
            )
            .with("none", filter == TikTokVideoVerifyFilter.ALL ? "&2➤ &aWszystko"
                : "&4➤ &cWszystko")
            .with("waiting",
                filter == TikTokVideoVerifyFilter.ONLY_WAITING ? "&2➤ &aTylko oczekujące"
                    : "&4➤ &cTylko oczekujące")
            .with("accepted",
                filter == TikTokVideoVerifyFilter.ONLY_ACCEPTED ? "&2➤ &aTylko zaakceptowane"
                    : "&4➤ &cTylko zaakcpetowane")
            .with("blocked",
                filter == TikTokVideoVerifyFilter.ONLY_BLOCKED ? "&2➤ &aTylko zablokowane"
                    : "&4➤ &cTylko zablokowane")
            .apply())
        .asGuiItem(event -> {

          filter = filter.next();
          open(player, page);

        }));

    gui.setItem(6, 9, FlameItemBuilder.of(Material.BREWING_STAND)
        .name(
            "&e♻ &8| &e&lsᴏʀᴛᴏᴡᴀɴɪᴇ")
        .lore(BukkitMessage.from(
                "",
                " {NONE}",
                " {VPLN}",
                " {NEWEST}",
                " {OLDEST}",
                "",
                "&eKliknij, aby zmienić sortowanie."
            )
            .with("none", sort == TikTokVideoVerifySort.NONE ? "&2➤ &aBrak"
                : "&4➤ &cWszystko")
            .with("vpln",
                sort == TikTokVideoVerifySort.VPLN ? "&2➤ &avPLN"
                    : "&4➤ &cvPLN")
            .with("newest",
                sort == TikTokVideoVerifySort.NEWEST ? "&2➤ &aNajnowsze"
                    : "&4➤ &cNajnowsze")
            .with("oldest",
                sort == TikTokVideoVerifySort.OLDEST ? "&2➤ &aNajstarsze"
                    : "&4➤ &cNajstarsze")
            .apply())
        .asGuiItem(event -> {

          sort = sort.next();
          open(player, page);

        }));

    gui.setItem(6, 4, FlameItemBuilder.of(
            SkullBuilder.create("f84f597131bbe25dc058af888cb29831f79599bc67c95c802925ce4afba332fc"))
        .name("&cPoprzednia strona")
        .asGuiItem(inventoryClickEvent -> {
          gui.previous();
          updateTitle(gui, gui.getCurrentPageNum(), gui.getPagesNum());
        }));

    gui.setItem(6, 6, FlameItemBuilder.of(
            SkullBuilder.create("fcfe8845a8d5e635fb87728ccc93895d42b4fc2e6a53f1ba78c845225822"))
        .name("&cNastępna strona")
        .asGuiItem(inventoryClickEvent -> {
          gui.next();
          updateTitle(gui, gui.getCurrentPageNum(), gui.getPagesNum());
        }));

    List<TikTokVideoVerify> values = new ArrayList<>(tikTokVideoVerifyCache.values());
    if (filter == TikTokVideoVerifyFilter.ONLY_WAITING) {
      values = values
          .stream()
          .filter(verify -> verify.getStatus() == TikTokVideoVerifyStatus.WAITING)
          .collect(Collectors.toList());
    } else if (filter == TikTokVideoVerifyFilter.ONLY_ACCEPTED) {
      values = values.
          stream()
          .filter(verify -> verify.getStatus() == TikTokVideoVerifyStatus.VERIFIED)
          .collect(Collectors.toList());
    } else if (filter == TikTokVideoVerifyFilter.ONLY_BLOCKED) {
      values = values.
          stream()
          .filter(verify -> verify.getStatus() == TikTokVideoVerifyStatus.BLOCKED)
          .collect(Collectors.toList());
    }

    if (sort == TikTokVideoVerifySort.NEWEST) {
      values.sort(Comparator.comparing(TikTokVideoVerify::getCreateTime).reversed());
    } else if (sort == TikTokVideoVerifySort.OLDEST) {
      values.sort(Comparator.comparing(TikTokVideoVerify::getCreateTime));
    } else if (sort == TikTokVideoVerifySort.VPLN) {
      values.sort(Comparator.comparing(TikTokVideoVerify::getPlayCount).reversed());
    }

    for (final TikTokVideoVerify value : values) {

      final double round = RoundUtil.round(
          (double) value.getPlayCount() / 300, 2);
      gui.addItem(FlameItemBuilder.of(Material.ITEM_FRAME)
          .name("&8&l#" + value.getId())
          .lore(
              "",
              "&8▶ &fStatus: &c" + value.getStatus().toString(),
              "&8▶ &fAutor: &c" + value.getPlayerName(),
              "&8▶ &fOpis: &c" + value.getDescription()
                  .substring(0, Math.min(value.getDescription().length(), 40)),
              "&8▶ &fData: &c" + TimeUtil.formatDate(value.getCreateTime()),
              "",
              "&8▶ &fPolubienia: &c" + value.getDiggCount(),
              "&8▶ &fKomentarze: &c" + value.getCommentCount(),
              "&8▶ &fWyświetlenia: &c" + value.getPlayCount(),
              "",
              "&8▶ &fZa tego tiktoka gracz",
              "&8▶ &fotrzyma nagrodę w wysokości &6" + round + " &evPLNów",
              "",
              "&eKliknij &6&lLPM&e, aby zaakceptować.",
              "&eKliknij &6&lPPM&e, aby odrzucić i zbanować film.",
              "&eKliknij &6&lSCROLL&e, aby otworzyć link."
          )
          .asGuiItem(inventoryClickEvent -> {

            if (value.getStatus() != TikTokVideoVerifyStatus.WAITING) {
              BukkitMessage.from("&cTen tiktok nie jest w stanie oczekującym!").deliver(player);
              return;
            }

            if (inventoryClickEvent.getClick().isLeftClick()) {

              value.setStatus(TikTokVideoVerifyStatus.VERIFIED);
              CompletableFuture.supplyAsync(() -> tikTokUserCache.findByName(value.getPlayerName()))
                  .thenAccept(tikTokUser -> {
                    if (tikTokUser != null) {
                      tikTokUserCache.update(tikTokUser.getUniqueId(),
                          mutator -> mutator.addEarnedMoney(round));
                    }

                    redisMessenger.publish("tiktok-verify",
                        new TikTokVideoVerifyStatusPacket(value.getId(),
                            TikTokVideoVerifyStatus.VERIFIED));
                    tikTokVideoVerifyRepository.save(value);

                  })
                  .thenRun(() -> {

                    BukkitMessage.from("&aZaakceptowano prośbę o weryfikację tego tiktoka!")
                        .deliver(player);

                    CommonsPlugin.getInstance().getNetworkMessageService().send(
                        BukkitMessage.from(
                                "",
                                "&#FF007C♬ &8| &#FF007C&l/ᴛ&#FF1285&lɪ&#FF248E&lᴋ&#FF3698&lᴛ&#FF48A1&lᴏ&#FF5AAA&lᴋ &8▶ &fGracz &#FF007C{player} &fodebrał nagrodę",
                                "&fw postaci &#FF007C&lvPLN'ów &fza &#FF007Ctiktoka &fz naszego serwera!",
                                "&fDowiedz się więcej wpisując &#FF007C&n/tiktok",
                                ""
                            )
                            .with("player", value.getPlayerName())
                            .apply(),
                        NetworkMessageFilter.builder()
                            .idForHide("tiktok")
                            .build(),
                        NetworkMessageType.CHAT
                    );

                    final DiscordWebhook discordWebhook = new DiscordWebhook(TikTokConstants.WEBHOOK_URL);
                    final DiscordWebhook.EmbedObject embed = new DiscordWebhook.EmbedObject();
                    embed.setAuthor("TIKTOK || Flamehub.pl", null,
                        "https://i.imgur.com/B3lRUdp.png");
                    embed.setColor(Color.YELLOW);
                    embed.addField("**Akcja:**", "Akceptacja tiktoka", true);
                    embed.addField("**Administrator:**", player.getName(), true);
                    embed.addField("**Kto:**", value.getPlayerName(), true);
                    embed.addField("**Link do filmu:**",
                        "https://www.tiktok.com/@" + value.getTikTokAccountUsername() + "/video/"
                            + value.getId(), true);
                    embed.addField("**Ile:**", String.valueOf(round), true);
                    embed.setImage("https://minotar.net/helm/" + player.getName() + "/100.png");
                    embed.setTimestamp(Instant.now().toString());
                    embed.setFooter("FlameHub.pl • " + TimeUtil.formatDate(Instant.now()),
                        "https://i.imgur.com/B3lRUdp.png");
                    discordWebhook.addEmbed(embed);
                    discordWebhook.execute();

                    flameDispatcher.dispatch(() -> {
                      open(player, page);
                      Bukkit.dispatchCommand(Bukkit.getConsoleSender(),
                          "ais add " + value.getPlayerName() + " " + round);
                    });

                  });


            } else if (inventoryClickEvent.getClick().isRightClick()) {

              CompletableFuture.runAsync(() -> {
                    value.setStatus(TikTokVideoVerifyStatus.BLOCKED);
                    redisMessenger.publish("tiktok-verify",
                        new TikTokVideoVerifyStatusPacket(value.getId(),
                            TikTokVideoVerifyStatus.BLOCKED));
                    tikTokVideoVerifyRepository.save(value);
                  })
                  .thenRun(() -> {
                    BukkitMessage.from("&cOdrzucono prośbę o weryfikację tego tiktoka!")
                        .deliver(player);

                    final DiscordWebhook discordWebhook = new DiscordWebhook(TikTokConstants.WEBHOOK_URL);
                    final DiscordWebhook.EmbedObject embed = new DiscordWebhook.EmbedObject();
                    embed.setAuthor("TIKTOK || Flamehub.pl", null,
                        "https://i.imgur.com/B3lRUdp.png");
                    embed.setColor(Color.YELLOW);
                    embed.addField("**Akcja:**", "Odrzucenie tiktoka", true);
                    embed.addField("**Administrator:**", player.getName(), true);
                    embed.addField("**Kto:**", value.getPlayerName(), true);
                    embed.addField("**Link do filmu:**",
                        "https://www.tiktok.com/@" + value.getTikTokAccountUsername() + "/video/"
                            + value.getId(), true);
                    embed.setImage("https://minotar.net/helm/" + player.getName() + "/100.png");
                    embed.setTimestamp(Instant.now().toString());
                    embed.setFooter("FlameHub.pl • " + TimeUtil.formatDate(Instant.now()),
                        "https://i.imgur.com/B3lRUdp.png");
                    discordWebhook.addEmbed(embed);
                    discordWebhook.execute();

                    flameDispatcher.dispatch(() -> open(player, page));
                  });


            } else if (inventoryClickEvent.getClick().isMouseClick()) {
              player.sendMessage(
                  "https://www.tiktok.com/@" + value.getTikTokAccountUsername() + "/video/"
                      + value.getId());
            }
          }));

    }

    updateTitle(gui, gui.getCurrentPageNum(), gui.getPagesNum());
    gui.open(player, page);

  }

  private void updateTitle(final BaseGui gui, final int currentPageNum, final int maxPageNum) {
    gui.updateTitle(TextUtil.legacyColor(BukkitMessage.from(
            "&c♫ &8| &c&lᴛɪᴋᴛᴏᴋ ᴘᴀɴᴇʟ &8(&f{page}&8/&7{max_page}&8)")
        .with("page", currentPageNum)
        .with("max_page", maxPageNum == 0 ? 1 : maxPageNum)
        .applyFirst())
    );
  }

}
