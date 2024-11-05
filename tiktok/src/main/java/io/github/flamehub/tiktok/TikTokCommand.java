package io.github.flamehub.tiktok;

import dev.rollczi.litecommands.annotations.argument.Arg;
import dev.rollczi.litecommands.annotations.async.Async;
import dev.rollczi.litecommands.annotations.command.Command;
import dev.rollczi.litecommands.annotations.context.Context;
import dev.rollczi.litecommands.annotations.execute.Execute;
import io.github.flamehub.commons.bukkit.CommonsPlugin;
import io.github.flamehub.commons.bukkit.dispatcher.FlameDispatcher;
import io.github.flamehub.commons.bukkit.message.BukkitMessage;
import io.github.flamehub.commons.bukkit.util.TitleUtil;
import io.github.flamehub.commons.messenger.RedisMessenger;
import io.github.flamehub.commons.network.message.NetworkMessageType;
import io.github.flamehub.tiktok.account.TikTokAccount;
import io.github.flamehub.tiktok.user.TikTokUser;
import io.github.flamehub.tiktok.user.TikTokUserRepository;
import io.github.flamehub.tiktok.video.verify.TikTokVideoVerifyCache;
import io.github.flamehub.tiktok.video.verify.TikTokVideoVerifyRepository;
import java.io.IOException;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.entity.Player;

@Command(name = "tiktok")
public final class TikTokCommand {

  private final RedisMessenger redisMessenger;
  private final TikTokService tikTokService;
  private final TikTokUserRepository tikTokUserRepository;
  private final FlameDispatcher flameDispatcher;

  private final TikTokVideoVerifyRepository tikTokVideoVerifyRepository;
  private final TikTokVideoVerifyCache tikTokVideoVerifyCache;


  public TikTokCommand(final RedisMessenger redisMessenger, final TikTokService tikTokService,
      final TikTokUserRepository tikTokUserRepository, final FlameDispatcher flameDispatcher,
      final TikTokVideoVerifyRepository tikTokVideoVerifyRepository,
      final TikTokVideoVerifyCache tikTokVideoVerifyCache) {
    this.redisMessenger = redisMessenger;
    this.tikTokService = tikTokService;
    this.tikTokUserRepository = tikTokUserRepository;
    this.flameDispatcher = flameDispatcher;
    this.tikTokVideoVerifyRepository = tikTokVideoVerifyRepository;
    this.tikTokVideoVerifyCache = tikTokVideoVerifyCache;
  }

  @Execute(name = "rozlacz")
  void disconnectTikTokAccount(@Context final Player player, @Context final TikTokUser user) {
    if (user.getSecUid() == null) {
      BukkitMessage.from("&cTwoje konto minecraft nie jest połączone z kontem tiktok!")
          .send(player);
      return;
    }

    user.setSecUid(null);
    user.setTikTokUsername(null);
    user.setTikTokAccountURL(null);
    user.markToUpdate();

    BukkitMessage.from("&aRozłączono konto TikTok z kontem Minecraft!").send(player);
  }

  @Execute(name = "polacz")
  void connectTikTokAccount(
      @Context final Player player,
      @Context final TikTokUser user,
      @Arg("nazwa konta") final String name) {
    flameDispatcher.dispatchAsync(() -> {

      if (user.getSecUid() != null) {
        BukkitMessage.from("&cTwoje konto minecraft jest już połączone z kontem tiktok!")
            .send(player);
        return;
      }

      try {
        final TikTokAccount tikTokAccount = this.tikTokService.fetchTikTokAccount(name);
        if (tikTokAccount == null) {
          BukkitMessage.from("&cNie znaleziono konta TikTok o nazwie &4" + name).send(player);
          return;
        }

        if (tikTokUserRepository.loadBySecUid(tikTokAccount.getUser().getSecUid()) != null) {
          BukkitMessage.from("&cTo konto TikTok jest już połączone z innym kontem Minecraft!")
              .send(player);
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
              .send(player);
          return;

        }

        user.setSecUid(tikTokAccount.getUser().getSecUid());
        user.setTikTokUsername(tikTokAccount.getUser().getUniqueId());
        user.markToUpdate();

        BukkitMessage.from("&aPołączono konto TikTok z kontem Minecraft!").send(player);

        CommonsPlugin.getInstance().getNetworkMessageService().send(
            BukkitMessage.from(
                    "",
                    "&#FF007C♬ &8| &#FF007C&l/ᴛ&#FF1285&lɪ&#FF248E&lᴋ&#FF3698&lᴛ&#FF48A1&lᴏ&#FF5AAA&lᴋ &8▶ &fGracz &#FF007C{player} &fwłaśnie połączył",
                    "&fswoje konto &#FF007CMinecraft &fz kontem &#FF007CTikTok&f!",
                    "&fDowiedz się więcej wpisując &#FF007C&n/tiktok polacz",
                    ""
                )
                .with("player", player.getName())
                .apply(),
            NetworkMessageType.CHAT
        );

      } catch (IOException e) {
        BukkitMessage.from("&cWystąpił nieoczekiwany bląd, spróbuj ponownie za chwilę!")
            .send(player);
      }

    });


  }

  @Execute(name = "lista")
  void list(@Context final Player player, @Context final TikTokUser user) {
    final TikTokGui tikTokGui = new TikTokGui(player, redisMessenger, flameDispatcher, user, tikTokService, tikTokVideoVerifyCache, tikTokVideoVerifyRepository);

    TitleUtil.title(player, "&c♫ &8| &c&lᴛɪᴋᴛᴏᴋ ᴘᴀɴᴇʟ", "&fᴛʀᴡᴀ ʟᴀᴅᴏᴡᴀɴɪᴇ...", 10, 50, 20);
    tikTokGui.open();

  }



}
