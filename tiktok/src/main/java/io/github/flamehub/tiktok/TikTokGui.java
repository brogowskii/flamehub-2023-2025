package io.github.flamehub.tiktok;

import dev.triumphteam.gui.guis.BaseGui;
import dev.triumphteam.gui.guis.Gui;
import dev.triumphteam.gui.guis.PaginatedGui;
import io.github.flamehub.commons.bukkit.CommonsPlugin;
import io.github.flamehub.commons.bukkit.dispatcher.FlameDispatcher;
import io.github.flamehub.commons.bukkit.message.BukkitMessage;
import io.github.flamehub.commons.bukkit.text.TextUtil;
import io.github.flamehub.commons.bukkit.util.FlameItemBuilder;
import io.github.flamehub.commons.bukkit.util.SkullBuilder;
import io.github.flamehub.commons.bukkit.util.TitleUtil;
import io.github.flamehub.commons.messenger.RedisMessenger;
import io.github.flamehub.commons.network.message.NetworkMessageFilter;
import io.github.flamehub.commons.network.message.NetworkMessageType;
import io.github.flamehub.commons.util.RoundUtil;
import io.github.flamehub.commons.util.TimeUtil;
import io.github.flamehub.tiktok.user.TikTokUser;
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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;

public final class TikTokGui {

  private final Player player;
  private final RedisMessenger redisMessenger;
  private final FlameDispatcher flameDispatcher;

  private final TikTokUser tikTokUser;
  private final TikTokService tikTokService;
  private final TikTokVideoVerifyCache tikTokVideoVerifyCache;
  private final TikTokVideoVerifyRepository tikTokVideoVerifyRepository;

  private TikTokVideoSort sortType = TikTokVideoSort.NONE;

  public TikTokGui(final Player player, final RedisMessenger redisMessenger, final FlameDispatcher flameDispatcher,
      final TikTokUser tikTokUser, final TikTokService tikTokService,
      final TikTokVideoVerifyCache tikTokVideoVerifyCache,
      final TikTokVideoVerifyRepository tikTokVideoVerifyRepository) {
    this.player = player;
    this.redisMessenger = redisMessenger;
    this.flameDispatcher = flameDispatcher;
    this.tikTokUser = tikTokUser;
    this.tikTokService = tikTokService;
    this.tikTokVideoVerifyCache = tikTokVideoVerifyCache;
    this.tikTokVideoVerifyRepository = tikTokVideoVerifyRepository;
  }

  public void open() {

    PaginatedGui gui = Gui.paginated()
        .title(TextUtil.parse("&c♫ &8| &c&lᴛɪᴋᴛᴏᴋ ᴘᴀɴᴇʟ"))
        .rows(6)
        .disableAllInteractions()
        .create();

    fillGui6(gui);

    gui.setItem(1, 5, FlameItemBuilder.of(Material.GLOW_ITEM_FRAME)
        .name("")
        .lore(
            "",
            "&4⚠ &fTwoje połączone konto tiktok:",
            " &8▶ &c@" + tikTokUser.getTikTokUsername(),
            "",
            "&3⚠ &bStatystyki tiktoków aktualizują się co 3 godziny",
            " &8▶ &bNastępna aktualizacja za: &3" + TimeUtil.formatTimeSimple(tikTokUser.getLastRefreshedTime() + TimeUnit.MINUTES.toMillis(5) - System.currentTimeMillis()),
            "",
            "&4⚠ &cWażne informacje",
            " &8▶ &c500 &fwyświetleń pod tiktokiem &8-▶ &61 vPLN",
            " &8▶ &cAby tiktok się wyświetlił, musi mieć w opisie &c#flamehub",
            " &8▶ &cZa każdego tiktoka nagrodę można odebrać tylko raz",
            " &8▶ &cJeśli tiktok ma więcej niż &42000 wyświetleń &ci chcesz za niego",
            "   &codebrać &4vPLN&c, zostanie on wysłany do administracji w celu zweryfikowania",
            "   &cczy wyświetlenia są zdobyte w legalny sposób.",
            ""
        )
        .asGuiItem());

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


    CompletableFuture.supplyAsync(() -> {

          Set<TikTokVideo> tikTokVideos = new HashSet<>();
          if (tikTokUser.getLastRefreshedTime() + TimeUnit.MINUTES.toMillis(5) < System.currentTimeMillis()) {
            try {
              final List<TikTokVideoWrapper> tikTokVideoWrappers = tikTokService.fetchVideos(
                  tikTokUser.getSecUid());

              if (tikTokVideoWrappers == null) {
                TitleUtil.title(player, "&c♫ &8| &c&lᴛɪᴋᴛᴏᴋ ᴘᴀɴᴇʟ", "&cWystąpił bład, spróbuj ponownie za chwilę...", 10, 50, 20);
                gui.close(player);
                return null;
              }

              final List<TikTokVideo> tikTokVideos2 =
                  tikTokVideoWrappers
                      .stream()
                      .map(TikTokVideoWrapper::unwrap)
                      .toList();
              tikTokUser.refreshTikTokVideos(tikTokVideos2);
              tikTokUser.setLastRefreshedTime(System.currentTimeMillis());
              tikTokUser.markToUpdate();
              tikTokVideos = tikTokUser.getTikTokVideos();
            } catch (IOException | TikTokVideoFetchException e) {
              TitleUtil.title(player, "&c♫ &8| &c&lᴛɪᴋᴛᴏᴋ ᴘᴀɴᴇʟ", "&cWystąpił bład, spróbuj ponownie za chwilę...", 10, 50, 20);
              gui.close(player);
              return null;
            }

          }
          else {
            tikTokVideos = tikTokUser.getTikTokVideos();
          }


          return TikTokVideoSorter.sorted(sortType, tikTokVideos);
        })
        .thenAcceptAsync(tikTokVideos -> {

          int i = 1;
          for (final TikTokVideo tikTokVideo : tikTokVideos.stream()
              .filter(tikTokVideo -> StringUtils.containsIgnoreCase(tikTokVideo.getDescription(),
                  "#flamehub"))
              .toList()) {

            final double round = RoundUtil.round((double) tikTokVideo.getPlayCount() / 500, 2);
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
                            " &6⚠ &fZa tego &etiktoka &fmożesz otrzymać nagrodę",
                            " &fW postaci &6vPLN &fza zdobyte wyświetlenia!",
                            "",
                            " &8▶ &fZa tego tiktoka otrzymasz: &6{reward} vPLN",
                            " &8▶ &fAktualna stawka za 500 wyświetleń: &61 vPLN",
                            ""
                        )
                        .with("desc", tikTokVideo.getDescription()
                            .substring(0, Math.min(tikTokVideo.getDescription().length(), 40)))
                        .with("likes", tikTokVideo.getDiggCount())
                        .with("views", tikTokVideo.getPlayCount())
                        .with("comments", tikTokVideo.getCommentCount())
                        .with("reward", round)
                        .with("date", TimeUtil.formatDate(Instant.ofEpochMilli(System.currentTimeMillis() + tikTokVideo.getCreateTime())))
                        .apply()
                );

            lore.appendLore(getStatusLore(tikTokVideo));

            gui.addItem(lore.asGuiItem(inventoryClickEvent -> {

              final TikTokVideoVerify verify = tikTokVideoVerifyCache.findByKey(tikTokVideo.getId());
              if (verify != null) {
                BukkitMessage.from("&cTen tiktok jest obecnie w trakcie weryfikacji!").send(player);
                gui.close(player);
                return;
              }

              if (tikTokUser.getClaimedVideos().contains(tikTokVideo.getId())) {
                BukkitMessage.from("&cOdebrałeś już nagrodę za tego tiktoka!").send(player);
                gui.close(player);
                return;
              }

              if (tikTokVideo.getPlayCount() > 500 && tikTokVideo.getPlayCount() < 2000) {
                tikTokUser.getClaimedVideos().add(tikTokVideo.getId());
                tikTokUser.markToUpdate();
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "ais add " + player.getName() + " " + round);
                BukkitMessage.from(
                    "&ePomyślnie odebrano nagrodę za tego tiktoka!",
                    "&eOtrzymałeś &6" + round + " vPLN &ena swoje konto!"
                ).send(player);

                CommonsPlugin.getInstance().getNetworkMessageService().send(
                    BukkitMessage.from(
                        "",
                        "&#FF007C♬ &8| &#FF007C&l/ᴛ&#FF1285&lɪ&#FF248E&lᴋ&#FF3698&lᴛ&#FF48A1&lᴏ&#FF5AAA&lᴋ &8▶ &fGracz &#FF007C{player} &fodebrał nagrodę",
                        "&fw postaci &#FF007C&lvPLN'ów &fza &#FF007Ctiktoka &fz naszego serwera!",
                        "&fDowiedz się więcej wpisując &#FF007C&n/tiktok pomoc",
                        ""
                        )
                        .with("player", player.getName())
                        .apply(),
                    NetworkMessageType.CHAT
                );

                gui.close(player);
                return;
              }

              if (tikTokVideo.getPlayCount() < 500) {
                BukkitMessage.from("&cNie możesz niestety odebrać nagrody za tego tiktoka! Minimalna ilość wyświetleń na chwilę obecną wynosi: &4500 &cwyświetleń").send(player);
                gui.close(player);
                return;
              }

              if (tikTokVideo.getPlayCount() > 2000) {
                BukkitMessage.from("&bTen tiktok ma więcej niż &32000 &bwyświetleń! W celu zapobiegania &3boostowanych &bwyświetleń, musi zostać poddany ręcznej &3weryfikacji&b...").send(player);
                final TikTokVideoVerify tikTokVideoVerify = new TikTokVideoVerify(player.getName(),
                    tikTokUser.getTikTokAccountURL(), tikTokUser.getTikTokUsername(),
                    tikTokVideo.getId(), tikTokVideo.getDescription(), tikTokVideo.getPlayCount(),
                    tikTokVideo.getDiggCount(), tikTokVideo.getCommentCount());
                tikTokVideoVerifyRepository.save(tikTokVideoVerify);
                redisMessenger.publish("tiktok-verify", new TikTokVideoVerifyCreatePacket(tikTokVideoVerify));
                gui.close(player);

              }



            }));
          }

        })
        .thenRun(() -> this.flameDispatcher.dispatch(() -> gui.open(player)));


  }

  void fillGui6(BaseGui gui) {

    gui.getFiller()
        .fillBorder(FlameItemBuilder.of(Material.WHITE_STAINED_GLASS_PANE).name(" ").asGuiItem());
    gui.setItem(Arrays.asList(0, 8, 45, 53),
        FlameItemBuilder.of(Material.BLACK_STAINED_GLASS_PANE).name(" ").asGuiItem());
    gui.setItem(Arrays.asList(1, 7, 9, 17, 36, 44, 46, 52),
        FlameItemBuilder.of(Material.RED_STAINED_GLASS_PANE).name(" ").asGuiItem());
    gui.setItem(49, FlameItemBuilder.of(Material.AIR).asGuiItem());
    gui.setItem(4, FlameItemBuilder.of(Material.AIR).asGuiItem());


  }

  String[] getStatusLore(TikTokVideo tikTokVideo) {

    final TikTokVideoVerify verify = tikTokVideoVerifyCache.findByKey(tikTokVideo.getId());
    if (verify != null && verify.getStatus() == TikTokVideoVerifyStatus.WAITING) {
      return new String[] {
          "&3⚠ &bTen tiktok jest obecnie w trakcie weryfikacji!",
          "&bPrzewidywany czas oczekiwania: &31-3h"
      };
    }

    if (verify != null && verify.getStatus() == TikTokVideoVerifyStatus.BLOCKED) {
      return new String[] {
          "&4⚠ &#EF0B4DTen tiktok został zablokowany przez administrację!",
          "&#EF0B4DPowód zablokowania to najprawdopodobniej",
          "&#EF0B4Dboostowanie wyświetleń, polubień lub komentarzy!",
          "",
          "&#EF0B4DJeśli uważasz, że to błąd, skontaktuj się z nami!",
          "&#EF0B4Dna naszym discordzie: &4dc.flamehub.pl"
      };
    }

    final boolean contains = tikTokUser.getClaimedVideos().contains(tikTokVideo.getId());
    if (contains) {
      return new String[] {
          "&6⚠ &eOdebrałeś już nagrodę za tego tiktoka!"
      };
    }

    if (tikTokVideo.getPlayCount() > 500 && tikTokVideo.getPlayCount() < 2000) {
      return new String[] {
          "&2⚠ &aKliknij, aby odebrać nagrodę!"
      };
    }

    if (tikTokVideo.getPlayCount() > 2000) {
      return new String[] {
          "&3⚠ &bTen tiktok ma więcej niż &32000 &bwyświetleń!",
          "&bW celu zapobiegania &3boostowanych &bwyświetleń,",
          "&bmusi zostać poddany ręcznej &3weryfikacji&b...",
          "&bPrzewidywany czas oczekiwania: &31-3h",
          "",
          "&bKliknij aby zgłosić tiktok do weryfikacji!"
      };
    }



    if (tikTokVideo.getPlayCount() < 500) {
      return new String[] {
          "&4⚠ &#EF0B4DNie możesz niestety odebrać nagrody",
          "&#EF0B4Dza tego tiktoka! Minimalna ilość wyświetleń",
          "&#EF0B4Dna chwilę obecną wynosi: &4500 &#EF0B4Dwyświetleń"
      };
    }

    return new String[]{};


  }

}
