package io.github.flamehub.tiktok;

import dev.triumphteam.gui.guis.Gui;
import dev.triumphteam.gui.guis.PaginatedGui;
import io.github.flamehub.commons.bukkit.dispatcher.FlameDispatcher;
import io.github.flamehub.commons.bukkit.message.BukkitMessage;
import io.github.flamehub.commons.bukkit.text.TextUtil;
import io.github.flamehub.commons.bukkit.util.FlameItemBuilder;
import io.github.flamehub.tiktok.user.TikTokUser;
import io.github.flamehub.tiktok.user.TikTokUserFactory;
import io.github.flamehub.tiktok.video.TikTokVideo;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import org.bukkit.Material;
import org.bukkit.entity.Player;

public final class TikTokGui {

  private final Player player;
  private final FlameDispatcher flameDispatcher;

  private final TikTokUser tikTokUser;
  private final TikTokService tikTokService;

  public TikTokGui(final Player player, final FlameDispatcher flameDispatcher, final TikTokUser tikTokUser, final TikTokService tikTokService) {
    this.player = player;
    this.flameDispatcher = flameDispatcher;
    this.tikTokUser = tikTokUser;
    this.tikTokService = tikTokService;
  }

  public void open() {

    PaginatedGui gui = Gui.paginated()
        .title(TextUtil.parse("&8Twoje tiktoki: "))
        .rows(6)
        .create();

    player.sendMessage("Trwa ładowanie twoich tiktoków....");
    CompletableFuture.supplyAsync(() -> {
          try {
            return this.tikTokService.fetchVideos(tikTokUser.getSecUid());
          } catch (IOException e) {
            throw new RuntimeException(e);
          }
        })
        .thenAcceptAsync(tikTokVideos -> {

          for (final TikTokVideo tikTokVideo : tikTokVideos) {
            gui.addItem(FlameItemBuilder.of(Material.ITEM_FRAME)
                .name("&cTikTok")
                .lore(
                    BukkitMessage.from(
                        "",
                        " &7Opis: &c{desc}",
                        " &7Liczba polubień: &c{likes}",
                        " &7Liczba wyświetleń: &c{views}",
                        " &7Liczba komentarzy: &c{comments}"
                    )
                        .with("desc", tikTokVideo.getDescription())
                        .with("likes", tikTokVideo.getDiggCount())
                        .with("views", tikTokVideo.getPlayCount())
                        .with("comments", tikTokVideo.getCommentCount())
                        .apply()
                )
                .asGuiItem());
          }

        })
        .thenRun(() -> this.flameDispatcher.dispatch(() -> gui.open(player)));



  }


}
