package io.github.flamehub.tiktok;

import dev.rollczi.litecommands.annotations.argument.Arg;
import dev.rollczi.litecommands.annotations.command.Command;
import dev.rollczi.litecommands.annotations.context.Context;
import dev.rollczi.litecommands.annotations.execute.Execute;
import io.github.flamehub.commons.bukkit.dispatcher.FlameDispatcher;
import io.github.flamehub.commons.bukkit.message.BukkitMessage;
import io.github.flamehub.commons.bukkit.util.TitleUtil;
import io.github.flamehub.tiktok.account.TikTokAccount;
import io.github.flamehub.tiktok.user.TikTokUser;
import java.io.IOException;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.entity.Player;

@Command(name = "tiktok")
public final class TikTokCommand {

  private final TikTokService tikTokService;
  private final FlameDispatcher flameDispatcher;

  public TikTokCommand(final TikTokService tikTokService, final FlameDispatcher flameDispatcher) {
    this.tikTokService = tikTokService;
    this.flameDispatcher = flameDispatcher;
  }

  @Execute(name = "polacz")
  void connectTikTokAccount(
      @Context final Player player,
      @Context final TikTokUser user,
      @Arg("nazwa konta") final String name) {

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

    } catch (IOException e) {
      BukkitMessage.from("&cWystąpił nieoczekiwany bląd, spróbuj ponownie za chwilę!").send(player);
    }


  }

  @Execute(name = "lista")
  void list(@Context final Player player, @Context final TikTokUser user) {
    final TikTokGui tikTokGui = new TikTokGui(player, flameDispatcher, user, tikTokService);
    tikTokGui.open();

  }


}
