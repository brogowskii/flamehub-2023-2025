package io.github.flamehub.tiktok;

import dev.triumphteam.gui.guis.BaseGui;
import dev.triumphteam.gui.guis.Gui;
import dev.triumphteam.gui.guis.PaginatedGui;
import io.github.flamehub.commons.bukkit.dispatcher.FlameDispatcher;
import io.github.flamehub.commons.bukkit.message.BukkitMessage;
import io.github.flamehub.commons.bukkit.text.TextUtil;
import io.github.flamehub.commons.bukkit.util.FlameItemBuilder;
import io.github.flamehub.commons.bukkit.util.SkullBuilder;
import io.github.flamehub.commons.bukkit.util.TitleUtil;
import io.github.flamehub.commons.messenger.RedisMessenger;
import io.github.flamehub.commons.util.RoundUtil;
import io.github.flamehub.commons.util.TimeUtil;
import io.github.flamehub.tiktok.user.TikTokUser;
import io.github.flamehub.tiktok.user.TikTokUserCache;
import io.github.flamehub.tiktok.video.TikTokVideo;
import io.github.flamehub.tiktok.video.TikTokVideoFetchException;
import io.github.flamehub.tiktok.video.TikTokVideoSort;
import io.github.flamehub.tiktok.video.TikTokVideoSorter;
import io.github.flamehub.tiktok.video.TikTokVideoWrapper;
import io.github.flamehub.tiktok.video.verify.TikTokVideoVerify;
import io.github.flamehub.tiktok.video.verify.TikTokVideoVerifyCache;
import io.github.flamehub.tiktok.video.verify.TikTokVideoVerifyCreatePacket;
import io.github.flamehub.tiktok.video.verify.TikTokVideoVerifyRepository;
import io.github.flamehub.tiktok.video.verify.TikTokVideoVerifyStatus;
import java.io.IOException;
import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitScheduler;

public final class TikTokGui {

  private final Plugin plugin;
  private final Player player;
  private final RedisMessenger redisMessenger;
  private final FlameDispatcher flameDispatcher;

  private final TikTokUser tikTokUser;
  private final TikTokUserCache tikTokUserCache;
  private final TikTokService tikTokService;
  private final TikTokVideoVerifyCache tikTokVideoVerifyCache;
  private final TikTokVideoVerifyRepository tikTokVideoVerifyRepository;

  private TikTokVideoSort sortType = TikTokVideoSort.NONE;

  public TikTokGui(
      final Plugin plugin,
      final Player player,
      final RedisMessenger redisMessenger,
      final FlameDispatcher flameDispatcher,
      final TikTokUser tikTokUser,
      final TikTokUserCache tikTokUserCache,
      final TikTokService tikTokService,
      final TikTokVideoVerifyCache tikTokVideoVerifyCache,
      final TikTokVideoVerifyRepository tikTokVideoVerifyRepository) {
    this.plugin = plugin;
    this.player = player;
    this.redisMessenger = redisMessenger;
    this.flameDispatcher = flameDispatcher;
    this.tikTokUser = tikTokUser;
    this.tikTokUserCache = tikTokUserCache;
    this.tikTokService = tikTokService;
    this.tikTokVideoVerifyCache = tikTokVideoVerifyCache;
    this.tikTokVideoVerifyRepository = tikTokVideoVerifyRepository;
  }

  public void open() {

    final PaginatedGui gui = Gui.paginated()
        .title(TextUtil.parse("&c♫ &8| &c&lᴛɪᴋᴛᴏᴋ ᴘᴀɴᴇʟ"))
        .rows(6)
        .disableAllInteractions()
        .create();

    fillGui6(gui);

    final Runnable runnable = () -> {
      gui.updateItem(1, 5, FlameItemBuilder.of(
              SkullBuilder.create("58d02984a43e6c6910d0d908a57e041c3cfb1dd881b5b720c55563e681f59e0e"))
          .name("")
          .lore(
              "&4⚠ &fTwoje połączone konto tiktok:",
              " &8▶ &c@" + tikTokUser.getTikTokUsername(),
              "",
              "&4⚠ &fTwoje statystyki:",
              " &8▶ &fOdebrałeś łacznie: &c" + tikTokUser.getEarnedMoney() + " vPLN",
              " &8▶ &fIlość filmów z #flamehub: &c" + tikTokUser.getTikTokVideos().size(),
              "",
              "&6⚠ &eStatystyki tiktoków aktualizują się co 1 godzinę",
              " &8▶ &fNastępna aktualizacja za: &e" + TimeUtil.formatTimeSimple(
                  tikTokUser.getLastRefreshedTime() + TimeUnit.HOURS.toMillis(1)
                      - System.currentTimeMillis()),
              "",
              "&6⚠ &eWażne informacje",
              " &8▶ &e300 &fwyświetleń pod tiktokiem &8-▶ &61 vPLN",
              " &8▶ &fAby tiktok się wyświetlił, musi mieć w opisie &e#flamehub",
              " &8▶ &fZa każdego tiktoka nagrodę można odebrać tylko raz",

              "&5⚠ &dW jaki sposób zdobyć rangę Media lub Twórca?",
              " &8▶ &fDołącz na naszego discorda, na którym nasza administracja",
              "   &fzajmuje się twórcami: &ddiscord.gg/tBmVquBw",
              ""
          )
          .asGuiItem());
    };

    runnable.run();
    final BukkitScheduler scheduler = plugin.getServer().getScheduler();
    scheduler.runTaskTimer(plugin, runnable, 0L, 20L);

    gui.setItem(6, 4, FlameItemBuilder.of(
            SkullBuilder.create("f84f597131bbe25dc058af888cb29831f79599bc67c95c802925ce4afba332fc"))
        .name("&cPoprzednia strona")
        .asGuiItem(inventoryClickEvent -> gui.previous()));

    gui.setItem(6, 6, FlameItemBuilder.of(
            SkullBuilder.create("fcfe8845a8d5e635fb87728ccc93895d42b4fc2e6a53f1ba78c845225822"))
        .name("&cNastępna strona")
        .asGuiItem(inventoryClickEvent -> gui.next()));

    gui.setItem(6, 5, FlameItemBuilder.of(Material.HOPPER)
        .name(
            "&#29ADCF♻ &8| &#29ADCF&ls&#29A9CF&lᴏ&#29A4CF&lʀ&#29A0CF&lᴛ&#299BCF&lᴏ&#299BCF&lᴡ&#29A0CF&lᴀ&#29A4CF&lɴ&#29A9CF&lɪ&#29ADCF&lᴇ")
        .lore(BukkitMessage.from(
                "",
                " {NONE}",
                " {NEWEST}",
                " {OLDEST}",
                " {VIEWS}",
                " {LIKES}",
                " {COMMENTS}",

                "",
                "&bKliknij, aby zmienić filtr sortujący."
            )
            .with("none", sortType == TikTokVideoSort.NONE ? "&2➤ &aBrak sortowania"
                : "&4➤ &cBrak sortowania")
            .with("newest",
                sortType == TikTokVideoSort.NEWEST ? "&2➤ &aNajnowsze" : "&4➤ &cNajnowsze")
            .with("oldest",
                sortType == TikTokVideoSort.OLDEST ? "&2➤ &aNajstarsze" : "&4➤ &cNajstarsze")
            .with("views",
                sortType == TikTokVideoSort.VIEWS ? "&2➤ &aWyświetlenia rosnąco"
                    : "&4➤ &cWyświetlenia rosnąco")
            .with("likes",
                sortType == TikTokVideoSort.LIKES ? "&2➤ &aPolubienia rosnąco"
                    : "&4➤ &cPolubienia rosnąco")
            .with("comments",
                sortType == TikTokVideoSort.COMMENTS ? "&2➤ &aKomentarze rosnąco"
                    : "&4➤ &cKomentarze rosnąco")
            .apply())
        .asGuiItem(event -> {

          sortType = sortType.next();
          open();

        }));

    flameDispatcher.dispatchAsync(() -> {

      Set<TikTokVideo> tikTokVideos = Set.of();
      if (tikTokUser.getLastRefreshedTime() + TimeUnit.HOURS.toMillis(1) < System.currentTimeMillis()) {
        try {
          final List<TikTokVideoWrapper> tikTokVideoWrappers = tikTokService.fetchVideos(
              tikTokUser.getSecUid());

          if (tikTokVideoWrappers != null && !tikTokVideoWrappers.isEmpty()) {

            tikTokVideos =
                tikTokVideoWrappers
                    .stream()
                    .map(TikTokVideoWrapper::unwrap)
                    .collect(Collectors.toSet());

            tikTokUser.refreshTikTokVideos(tikTokVideos);
            tikTokUserCache.update(tikTokUser.getUniqueId(), mutator -> {
              mutator.setLastRefreshedTime(System.currentTimeMillis());
            });
          }

        } catch (final IOException | TikTokVideoFetchException e) {
          TitleUtil.title(player, "&c♫ &8| &c&lᴛɪᴋᴛᴏᴋ ᴘᴀɴᴇʟ",
              "&cWystąpił bład, spróbuj ponownie za chwilę...", 10, 50, 20);
          player.sendMessage("Error: " + e.getMessage());
          gui.close(player);
        }

      } else {
        tikTokVideos = tikTokUser.getTikTokVideos();
      }


      tikTokVideos = TikTokVideoSorter.sorted(sortType, tikTokVideos);

      int i = 1;
      final List<TikTokVideo> list = tikTokVideos.stream()
          .filter(tikTokVideo -> StringUtils.containsIgnoreCase(tikTokVideo.getDescription(),
              "#flamehub"))
          .toList();

      for (final TikTokVideo tikTokVideo : list) {

        final double round = RoundUtil.round((double) tikTokVideo.getPlayCount() / 300, 2);
        final FlameItemBuilder lore = FlameItemBuilder.of(Material.ITEM_FRAME)
            .name("&8&l#" + i++)
            .lore(
                BukkitMessage.from(
                        "",
                        " &#30CAFC✎ &8| &fOpis: &#30CAFC{desc}",
                        " &#D20000❤ &8| &fPolubienia: &#D20000{likes}",
                        " &#CC00EC♬ &8| &fWyświetlenia: &#CC00EC{views}",
                        " &#61A0C4✂ &8| &fKomentarze: &#61A0C4{comments}",
                        "",
                        " &6⚠ &eZa tego tiktoka możesz otrzymać nagrodę",
                        " &eW postaci &6vPLN &eza zdobyte wyświetlenia!",
                        "",
                        " &8▶ &fAktualnie tiktok wygenerował: &6{reward} vPLN",
                        " &8▶ &fStawka za 300 wyświetleń: &61 vPLN",
                        ""
                    )
                    .with("desc", tikTokVideo.getDescription()
                        .substring(0, Math.min(tikTokVideo.getDescription().length(), 40)))
                    .with("likes", tikTokVideo.getDiggCount())
                    .with("views", tikTokVideo.getPlayCount())
                    .with("comments", tikTokVideo.getCommentCount())
                    .with("reward", round)
                    .with("date",
                        TimeUtil.formatDate(Instant.ofEpochMilli(tikTokVideo.getCreateTime())))
                    .apply()
            );

        lore.appendLore(getStatusLore(tikTokVideo));

        gui.addItem(lore.asGuiItem(inventoryClickEvent -> {

          final TikTokVideoVerify verify = tikTokVideoVerifyCache.findByKey(
              tikTokVideo.getId());
          if (verify != null) {
            if (verify.getStatus() == TikTokVideoVerifyStatus.WAITING) {
              BukkitMessage.from("&cTen tiktok jest obecnie w trakcie weryfikacji!")
                  .deliver(player);
              gui.close(player);
              return;
            }

            if (verify.getStatus() == TikTokVideoVerifyStatus.BLOCKED) {
              BukkitMessage.from("&cTen tiktok został zablokowany przez administrację!")
                  .deliver(player);
              gui.close(player);
              return;
            }

            if (verify.getStatus() == TikTokVideoVerifyStatus.VERIFIED) {
              BukkitMessage.from("&cOdebrałeś już nagrodę za tego tiktoka!").deliver(player);
              gui.close(player);
              return;
            }
          }

          if (tikTokVideo.getPlayCount() < 500) {
            BukkitMessage.from(
                    "&cNie możesz niestety odebrać nagrody za tego tiktoka! Minimalna ilość wyświetleń na chwilę obecną wynosi: &4500 &cwyświetleń")
                .deliver(player);
            gui.close(player);
            return;
          }

          BukkitMessage.from(
                  "&bW celu zapobiegania &3boostowanych &bwyświetleń, tiktok musi zostać poddany ręcznej &3weryfikacji&b...",
                  "&bPrzewidywany czas oczekiwania: &31-12h")
              .deliver(player);
          final TikTokVideoVerify tikTokVideoVerify = new TikTokVideoVerify(player.getName(),
              tikTokUser.getTikTokAccountURL(), tikTokUser.getTikTokUsername(),
              tikTokVideo.getId(), tikTokVideo.getDescription(), tikTokVideo.getPlayCount(),
              tikTokVideo.getDiggCount(), tikTokVideo.getCommentCount());
          tikTokVideoVerifyRepository.save(tikTokVideoVerify);
          redisMessenger.publish("tiktok-verify",
              new TikTokVideoVerifyCreatePacket(tikTokVideoVerify));
          gui.close(player);


        }));
      }

      flameDispatcher.dispatch(() -> gui.open(player));
    });


  }

  void fillGui6(final BaseGui gui) {

    gui.getFiller()
        .fillBorder(FlameItemBuilder.of(Material.WHITE_STAINED_GLASS_PANE).name(" ").asGuiItem());
    gui.setItem(Arrays.asList(0, 8, 45, 53),
        FlameItemBuilder.of(Material.BLACK_STAINED_GLASS_PANE).name(" ").asGuiItem());
    gui.setItem(Arrays.asList(1, 7, 9, 17, 36, 44, 46, 52),
        FlameItemBuilder.of(Material.RED_STAINED_GLASS_PANE).name(" ").asGuiItem());
    gui.setItem(49, FlameItemBuilder.of(Material.AIR).asGuiItem());
    gui.setItem(4, FlameItemBuilder.of(Material.AIR).asGuiItem());


  }

  String[] getStatusLore(final TikTokVideo tikTokVideo) {

    final TikTokVideoVerify verify = tikTokVideoVerifyCache.findByKey(tikTokVideo.getId());
    if (verify != null && verify.getStatus() == TikTokVideoVerifyStatus.WAITING) {
      return new String[]{
          "&3⚠ &bTen tiktok jest obecnie w trakcie weryfikacji!",
          "&bPrzewidywany czas oczekiwania: &31-12h"
      };
    }

    if (verify != null && verify.getStatus() == TikTokVideoVerifyStatus.BLOCKED) {
      return new String[]{
          "&4⚠ &#EF0B4DTen tiktok został zablokowany przez administrację!",
          "&#EF0B4DPowód zablokowania to najprawdopodobniej",
          "&#EF0B4Dboostowanie wyświetleń, polubień lub komentarzy!",
          "",
          "&#EF0B4DJeśli uważasz, że to błąd, skontaktuj się z nami!",
          "&#EF0B4Dna naszym discordzie: &4dc.flamehub.pl"
      };
    }

    if (verify != null && verify.getStatus() == TikTokVideoVerifyStatus.VERIFIED) {
      return new String[]{
          "&6⚠ &eOdebrałeś już nagrodę za tego tiktoka!"
      };
    }

    if (tikTokVideo.getPlayCount() < 500) {
      return new String[]{
          "&4⚠ &#EF0B4DNie możesz niestety odebrać nagrody",
          "&#EF0B4Dza tego tiktoka! Minimalna ilość wyświetleń",
          "&#EF0B4Dna chwilę obecną wynosi: &4500 &#EF0B4Dwyświetleń"
      };
    }

    return new String[]{
        "&bW celu zapobiegania &3boostowanych &bwyświetleń,",
        "&btiktok musi zostać poddany ręcznej &3weryfikacji&b...",
        "&bPrzewidywany czas oczekiwania: &31-3h",
        "",
        "&bKliknij aby zgłosić tiktok do weryfikacji!"
    };


  }

}
