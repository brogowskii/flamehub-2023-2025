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
import io.github.flamehub.commons.util.RoundUtil;
import io.github.flamehub.tiktok.user.TikTokUser;
import io.github.flamehub.tiktok.video.TikTokVideoWrapper;
import java.io.IOException;
import java.util.Arrays;
import java.util.concurrent.CompletableFuture;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.Material;
import org.bukkit.entity.Player;

public final class TikTokGui {

  private final Player player;
  private final FlameDispatcher flameDispatcher;

  private final TikTokUser tikTokUser;
  private final TikTokService tikTokService;

  public TikTokGui(final Player player, final FlameDispatcher flameDispatcher,
      final TikTokUser tikTokUser, final TikTokService tikTokService) {
    this.player = player;
    this.flameDispatcher = flameDispatcher;
    this.tikTokUser = tikTokUser;
    this.tikTokService = tikTokService;
  }

  public void open() {

    PaginatedGui gui = Gui.paginated()
        .title(TextUtil.parse("&c♫ &8| &c&lᴛɪᴋᴛᴏᴋ ᴘᴀɴᴇʟ "))
        .rows(6)
        .disableAllInteractions()
        .create();

    fillGui6(gui);

    gui.setItem(1, 5, FlameItemBuilder.of(Material.GLOW_ITEM_FRAME)
        .name("")
        .lore(
            "",
            "&4⚠ &fTwoje połączone konto tiktok:",
            " &8▶ &c" + tikTokUser.getTikTokUsername(),
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

    TitleUtil.title(player, "&c♫ &8| &c&lᴛɪᴋᴛᴏᴋ ᴘᴀɴᴇʟ", "&fᴛʀᴡᴀ ʟᴀᴅᴏᴡᴀɴɪᴇ...", 10, 50, 20);
    CompletableFuture.supplyAsync(() -> {
          try {
            return this.tikTokService.fetchVideos(tikTokUser.getSecUid());
          } catch (IOException e) {
            throw new RuntimeException(e);
          }
        })
        .thenAcceptAsync(tikTokVideos -> {

          int i = 1;
          for (final TikTokVideoWrapper tikTokVideoWrapper : tikTokVideos.stream()
              .filter(tikTokVideoWrapper -> StringUtils.containsIgnoreCase(tikTokVideoWrapper.getDescription(),
                  "#flamehub"))
              .toList()) {

            final double round = RoundUtil.round((double) tikTokVideoWrapper.getPlayCount() / 1000, 2);
            gui.addItem(FlameItemBuilder.of(Material.ITEM_FRAME)
                .name("&8&l#" + i++)
                .lore(
                    BukkitMessage.from(
                            "",
                            " &8▶ &fOpis: &c{desc}",
                            " &8▶ &fPolubienia: &c{likes}",
                            " &8▶ &fWyświetlenia: &c{views}",
                            " &8▶ &fKomentarze: &c{comments}",
                            "",
                            " &6⚠ &fZa tego &etiktoka &fmożesz otrzymać nagrodę",
                            " &fW postaci &6vPLN &fza zdobyte wyświetlenia!",
                            "",
                            " &8▶ &fZa tego tiktoka otrzymasz: &6{reward} vPLN",
                            " &8▶ &fAktualna stawka za 500 wyświetleń: &61 vPLN",
                            "",
                            ""
                        )
                        .with("desc", tikTokVideoWrapper.getDescription()
                            .substring(0, Math.min(tikTokVideoWrapper.getDescription().length(), 40)))
                        .with("likes", tikTokVideoWrapper.getDiggCount())
                        .with("views", tikTokVideoWrapper.getPlayCount())
                        .with("comments", tikTokVideoWrapper.getCommentCount())
                        .with("reward", round)
                        .apply()
                )
                .asGuiItem());
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

}
