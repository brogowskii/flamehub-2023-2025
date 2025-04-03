package io.github.flamehub.contest;

import dev.triumphteam.gui.guis.BaseGui;
import dev.triumphteam.gui.guis.Gui;
import io.github.flamehub.commons.bukkit.dispatcher.FlameDispatcher;
import io.github.flamehub.commons.bukkit.message.BukkitMessage;
import io.github.flamehub.commons.bukkit.text.TextUtil;
import io.github.flamehub.commons.bukkit.util.FlameItemBuilder;
import io.github.flamehub.commons.bukkit.util.GuiHelper;
import io.github.flamehub.contest.ticket.ContestTicketFacade;
import java.util.Arrays;
import java.util.concurrent.CompletableFuture;
import org.bukkit.Material;
import org.bukkit.entity.Player;

final class ContestGui {

  private final FlameDispatcher flameDispatcher;
  private final ContestFacade contestFacade;
  private final ContestTicketFacade contestTicketFacade;

  ContestGui(
      final FlameDispatcher flameDispatcher,
      final ContestFacade contestFacade,
      final ContestTicketFacade contestTicketFacade) {
    this.flameDispatcher = flameDispatcher;
    this.contestFacade = contestFacade;
    this.contestTicketFacade = contestTicketFacade;
  }

  public static void fillGui5(BaseGui gui) {
    gui.getFiller()
        .fillBorder(FlameItemBuilder.of(Material.WHITE_STAINED_GLASS_PANE).name(" ").asGuiItem());
    gui.setItem(Arrays.asList(0, 8, 36, 44),
        FlameItemBuilder.of(Material.LIME_STAINED_GLASS_PANE).name(" ").asGuiItem());
    gui.setItem(Arrays.asList(1, 7, 9, 17, 27, 35, 37, 43),
        FlameItemBuilder.of(Material.GREEN_STAINED_GLASS_PANE).name(" ").asGuiItem());
    gui.setItem(40, FlameItemBuilder.of(Material.AIR).asGuiItem());
    gui.setItem(4, FlameItemBuilder.of(Material.AIR).asGuiItem());
  }

  public void openGui(final Player player) {
    CompletableFuture.supplyAsync(() -> {
          final Gui gui = Gui.gui()
              .title(TextUtil.parse(
                  "&r\uE069 &8| &#00CA57&lɢ&#0BCC5E&lᴜ&#17CE66&lᴄ&#22D06D&lᴄ&#2DD274&lɪ &#17CE66&lᴍ&#0BCC5E&lᴀ&#00CA57&lɴ"))
              .disableAllInteractions()
              .rows(5)
              .create();
          fillGui5(gui);
          return gui;
        })
        .thenAccept(gui -> {

//          gui.setItem(3, 6, FlameItemBuilder.of(Material.PAPER)
//              .name()
//              .glow()
//              .asGuiItem());

          contestTicketFacade.getTicketsAmount(player.getUniqueId())
              .thenAccept(size -> {

                final double balance = contestFacade.balance(player.getUniqueId());
                gui.setItem(3, 4, FlameItemBuilder.of(Material.PAPER)
                    .name(
                        "&#00CA57&lᴡ&#05CB5A&lʏ&#0ACC5D&lᴛ&#0FCD61&lᴡ&#14CE64&lᴀ&#19CE67&lʀ&#1ECF6A&lᴢ&#23D06E&lᴀ&#28D171&lɴ&#2DD274&lɪ&#27D170&lᴇ &#1CCF69&lʙ&#17CE66&lɪ&#11CD62&lʟ&#0BCC5E&lᴇ&#06CB5B&lᴛ&#00CA57&lᴜ")
                    .lore(
                        "",
                        " &8▶ &fAktualnie posiadasz: &a" + size + " &fwytworzonych biletów",
                        "",
                        " &2⚠ &#2DD274Co potrzeba aby wytworzyć bilet?",
                        "  &fAby wytworzyć bilet potrzebujesz: &#2DD27410.000 hype-coinów",
                        "  &fAktualnie posiadasz: &#2DD274" + balance
                            + "&8/&#00CA5710000",
                        "",
                        "&#2DD274Kliknij aby wytworzyć bilet."
                    )
                    .customModelData(10283)
                    .glow()
                    .asGuiItem(inventoryClickEvent -> {

                      if (balance < 10_000) {
                        BukkitMessage.from(
                                "&cNie posiadasz wystarczająco hype-coinów do wytworzenia biletu!")
                            .deliver(player);
                        player.closeInventory();
                        return;
                      }

                      contestTicketFacade.createTicket(player.getUniqueId(), player.getName())
                          .thenRun(() -> {
                            BukkitMessage.from("&aPomyślnie utworzono bilet!").deliver(player);
                            player.closeInventory();
                          });

                    }));

                flameDispatcher.dispatch(() -> gui.open(player));

              });
        });

  }

}
