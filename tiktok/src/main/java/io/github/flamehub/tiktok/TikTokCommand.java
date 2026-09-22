package io.github.flamehub.tiktok;

import static java.util.concurrent.CompletableFuture.runAsync;

import dev.rollczi.litecommands.annotations.argument.Arg;
import dev.rollczi.litecommands.annotations.command.Command;
import dev.rollczi.litecommands.annotations.context.Context;
import dev.rollczi.litecommands.annotations.execute.Execute;
import dev.rollczi.litecommands.annotations.permission.Permission;
import io.github.flamehub.commons.bukkit.CommonsPlugin;
import io.github.flamehub.commons.bukkit.dispatcher.FlameDispatcher;
import io.github.flamehub.commons.bukkit.message.BukkitMessage;
import io.github.flamehub.commons.bukkit.util.TitleUtil;
import io.github.flamehub.commons.messenger.RedisMessenger;
import io.github.flamehub.commons.network.message.NetworkMessageFilter;
import io.github.flamehub.commons.network.message.NetworkMessageType;
import io.github.flamehub.commons.util.TimeUtil;
import io.github.flamehub.tiktok.account.TikTokAccount;
import io.github.flamehub.tiktok.shop.TikTokShopConfig;
import io.github.flamehub.tiktok.user.TikTokUser;
import io.github.flamehub.tiktok.user.TikTokUserCache;
import io.github.flamehub.tiktok.user.TikTokUserRepository;
import io.github.flamehub.tiktok.video.verify.TikTokVideoVerifyCache;
import io.github.flamehub.tiktok.video.verify.TikTokVideoVerifyRepository;
import io.github.flamehub.tiktok.video.verify.TikTokVideoVerify;
import io.github.flamehub.tiktok.video.verify.TikTokVideoVerifyStatus;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

@Command(name = "tiktok")
@Permission("server.commands.tiktok")
public final class TikTokCommand {

  private final Plugin plugin;
  private final RedisMessenger redisMessenger;
  private final TikTokService tikTokService;
  private final TikTokUserCache tikTokUserCache;
  private final TikTokUserRepository tikTokUserRepository;
  private final TikTokShopConfig tikTokShopConfig;
  private final FlameDispatcher flameDispatcher;

  private final TikTokVideoVerifyRepository tikTokVideoVerifyRepository;
  private final TikTokVideoVerifyCache tikTokVideoVerifyCache;


  public TikTokCommand(
      final Plugin plugin,
      final RedisMessenger redisMessenger,
      final TikTokService tikTokService,
      final TikTokUserCache tikTokUserCache,
      final TikTokUserRepository tikTokUserRepository,
      final TikTokShopConfig tikTokShopConfig,
      final FlameDispatcher flameDispatcher,
      final TikTokVideoVerifyRepository tikTokVideoVerifyRepository,
      final TikTokVideoVerifyCache tikTokVideoVerifyCache
  ) {
    this.plugin = plugin;
    this.redisMessenger = redisMessenger;
    this.tikTokService = tikTokService;
    this.tikTokUserCache = tikTokUserCache;
    this.tikTokUserRepository = tikTokUserRepository;
    this.tikTokShopConfig = tikTokShopConfig;
    this.flameDispatcher = flameDispatcher;
    this.tikTokVideoVerifyRepository = tikTokVideoVerifyRepository;
    this.tikTokVideoVerifyCache = tikTokVideoVerifyCache;
  }

  @Execute(name = "rozlacz")
  CompletableFuture<Void> disconnectTikTokAccount(
      final @Context Player player,
      final @Context TikTokUser user) {

    return runAsync(() -> {
      if (user.getSecUid() == null) {
        BukkitMessage.from("&cTwoje konto minecraft nie jest połączone z kontem tiktok!")
            .deliver(player);
        return;
      }

      tikTokUserCache.update(user.getUniqueId(), mutator -> {
        mutator.setSecUid(null);
        mutator.setTikTokUsername(null);
        mutator.setTikTokAccountURL(null);
        mutator.getTikTokVideos().clear();
        BukkitMessage.from("&aRozłączono konto TikTok z kontem Minecraft!").deliver(player);
      });


    });
  }

  @Execute(name = "polacz")
  CompletableFuture<Void> connectTikTokAccount(
      final @Context Player player,
      final @Context TikTokUser user,
      final @Arg("nazwa konta") String name) {
    return runAsync(() -> {

      if (user.getSecUid() != null) {
        BukkitMessage.from("&cTwoje konto minecraft jest już połączone z kontem tiktok!")
            .deliver(player);
        return;
      }

      try {
        final TikTokAccount tikTokAccount = tikTokService.fetchTikTokAccount(name);
        if (tikTokAccount == null) {
          BukkitMessage.from("&cNie znaleziono konta TikTok o nazwie &4" + name).deliver(player);
          return;
        }

        if (tikTokUserRepository.loadBySecUid(tikTokAccount.getUser().getSecUid()) != null) {
          BukkitMessage.from("&cTo konto TikTok jest już połączone z innym kontem Minecraft!")
              .deliver(player);
          return;
        }

        if (!StringUtils.containsIgnoreCase(tikTokAccount.getUser().getSignature(),
            "nick: " + player.getName())) {

          TitleUtil.title(player, "&4Błąd!", "&cPrzeczytaj informację na chacie...", 20, 80, 20);
          BukkitMessage.from(
                  "",
                  "&cW opisie twojego konta tiktok nie znaleziono twojego nicku!",
                  "&cAby połączyć konto TikTok z kontem Minecraft, w celu weryfikacji dodaj do opisu swojego konta TikTok frazę &4Nick: "
                      + player.getName(),
                  "&cPo dodaniu frazy do opisu i zweryfikowaniu się komendą możesz już usunąć ją z opisu swojego konta TikTok!"
              )
              .deliver(player);
          return;

        }

        tikTokUserCache.update(user.getUniqueId(), mutator -> {
          mutator.setSecUid(tikTokAccount.getUser().getSecUid());
          mutator.setTikTokUsername(tikTokAccount.getUser().getUniqueId());

          BukkitMessage.from("&aPołączono konto TikTok z kontem Minecraft!").deliver(player);

          CommonsPlugin.getInstance().getNetworkMessageService().sendAsync(
              BukkitMessage.from(
                      "",
                      "&#FF007C♬ &8| &#FF007C&l/ᴛ&#FF1285&lɪ&#FF248E&lᴋ&#FF3698&lᴛ&#FF48A1&lᴏ&#FF5AAA&lᴋ &8▶ &fGracz &#FF007C{player} &fwłaśnie połączył",
                      "&fswoje konto &#FF007CMinecraft &fz kontem &#FF007CTikTok&f!",
                      "&fDowiedz się więcej wpisując &#FF007C&n/tiktok polacz",
                      ""
                  )
                  .with("player", player.getName())
                  .apply(),
              NetworkMessageFilter.builder()
                  .idForHide("tiktok")
                  .build(),
              NetworkMessageType.CHAT
          );

        });


      } catch (final IOException e) {
        BukkitMessage.from("&cWystąpił nieoczekiwany bląd, spróbuj ponownie za chwilę!")
            .deliver(player);
      }

    });


  }

  @Execute(name = "lista", aliases = "panel")
  void list(@Context final Player player, @Context final TikTokUser user) {

    if (user.getTikTokUsername() == null || user.getTikTokUsername().isEmpty()) {
      BukkitMessage.from(
              "",
              "&cTwoje konto minecraft nie jest połączone z kontem tiktok!",
              "&cAby połączyć konto TikTok z kontem Minecraft wpisz &4/tiktok polacz <nazwa konta>",
              ""
          )
          .deliver(player);
      return;
    }

    final TikTokGui tikTokGui = new TikTokGui(
        plugin,
        player,
        redisMessenger,
        flameDispatcher,
        user,
        tikTokUserCache,
        tikTokService,
        tikTokVideoVerifyCache,
        tikTokVideoVerifyRepository);

    TitleUtil.title(player, "&c♫ &8| &c&lᴛɪᴋᴛᴏᴋ ᴘᴀɴᴇʟ", "&fᴛʀᴡᴀ ʟᴀᴅᴏᴡᴀɴɪᴇ...", 10, 50, 20);
    tikTokGui.open();

  }

  @Execute(name = "odbierz")
  CompletableFuture<Void> claimRank(@Context final Player player, @Context final TikTokUser user) {
    return runAsync(() -> {
      
      if (user.getTikTokUsername() == null || user.getTikTokUsername().isEmpty()) {
        BukkitMessage.from("&cTwoje konto minecraft nie jest połączone z kontem tiktok!").deliver(player);
        return;
      }

      final long fourteenDaysAgo = System.currentTimeMillis() - (14L * 24 * 60 * 60 * 1000);
      if (user.getLastTimeReceivedRank() > fourteenDaysAgo) {
        final long timeLeft = user.getLastTimeReceivedRank() + (14L * 24 * 60 * 60 * 1000) - System.currentTimeMillis();
        BukkitMessage.from("&cMożesz odebrać rangę dopiero za: &4" + TimeUtil.formatTimeSimple(timeLeft))
            .deliver(player);
        return;
      }

      final List<TikTokVideoVerify> verifiedVideos = tikTokVideoVerifyRepository.loadAll("playerName", player.getName())
          .stream()
          .filter(verify -> verify.getStatus() == TikTokVideoVerifyStatus.VERIFIED)
          .toList();

      if (verifiedVideos.isEmpty()) {
        BukkitMessage.from("&cNie masz żadnych zweryfikowanych tiktoków! Aby odebrać rangę, musisz mieć przynajmniej jeden zaakceptowany tiktok.").deliver(player);
        return;
      }

      final int followersCount = user.getFollowersCount();
      final long fourteenDaysAgoForVideos = System.currentTimeMillis() - (14L * 24 * 60 * 60 * 1000);
      final List<TikTokVideoVerify> recentVideos = verifiedVideos.stream()
          .filter(verify -> verify.getCreateTime().toEpochMilli() >= fourteenDaysAgoForVideos)
          .toList();
      
      final boolean hasRecentVideo = !recentVideos.isEmpty();

      if (followersCount >= 1000 && recentVideos.size() >= 3) {
        BukkitMessage.from(
          "",
          " &fSpełniasz wymagania na rangę &f\uE050",
          " &fRanga została nadana na stałe.",
          "",
          "&fTwoje statystyki:",
          "&8▶ &fFollowersi: &d" + followersCount,
          "&8▶ &fFilmy z ostatnich 14 dni: &d" + recentVideos.size(),
          ""
        ).deliver(player);

        player.performCommand("lp user " + player.getName() + " parent addtemp tworca 14d");
        
        tikTokUserCache.update(user.getUniqueId(), mutator -> {
          mutator.setLastTimeReceivedRank(System.currentTimeMillis());
        });
        
        return;
      }

      if (followersCount >= 100 && hasRecentVideo) {
        BukkitMessage.from(
          "",
          " &fSpełniasz wymagania na rangę &f\uE047",
          " &fRanga została nadana na &514 dni&f.",
          "",
          "&fTwoje statystyki:",
          "&8▶ &fFollowersi: &5" + followersCount,
          "&8▶ &fFilm z ostatnich 14 dni: &a✓",
          ""
        ).deliver(player);

        player.performCommand("lp user " + player.getName() + " parent addtemp media 14d");

        tikTokUserCache.update(user.getUniqueId(), mutator -> {
          mutator.setLastTimeReceivedRank(System.currentTimeMillis());
        });
        
        return;
      }

      BukkitMessage.from(
        "",
        "&c&l✗ &cNie spełniasz wymagań na żadną rangę.",
        "",
        "&fWymagania na rangę &f\uE047 &f(na 14 dni):",
        "&8▶ &fMinimum &a100 &ffollowersów &8(" + (followersCount >= 100 ? "&a✓" : "&c✗ &fmasz: " + followersCount) + "&8)",
        "&8▶ &fPrzynajmniej 1 zweryfikowany film z ostatnich 14 dni &8(" + (hasRecentVideo ? "&a✓" : "&c✗") + "&8)",
        "",
        "&fWymagania na rangę &f\uE050 &f(na 14 dni):",
        "&8▶ &fMinimum &d1000 &ffollowersów &8(" + (followersCount >= 1000 ? "&a✓" : "&c✗ &fmasz: " + followersCount) + "&8)",
        "&8▶ &fMinimum &d3 &ffilmy z ostatnich 14 dni &8(" + (recentVideos.size() >= 3 ? "&a✓" : "&c✗ &fmasz: " + recentVideos.size()) + "&8)",
        "",
        "&eNastępne odbieranie rangi będzie możliwe za 14 dni od ostatniego odebrania.",
        ""
      ).deliver(player);
      
    });
  }


}
