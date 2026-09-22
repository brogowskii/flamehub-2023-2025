package io.github.flamehub.contest;

import dev.triumphteam.gui.guis.Gui;
import io.github.flamehub.commons.bukkit.dispatcher.FlameDispatcher;
import io.github.flamehub.commons.bukkit.message.BukkitMessage;
import io.github.flamehub.commons.bukkit.text.TextUtil;
import io.github.flamehub.commons.bukkit.util.FlameItemBuilder;
import io.github.flamehub.commons.bukkit.util.GuiHelper;
import io.github.flamehub.contest.ticket.ContestTicketFacade;
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

  public void openGui(final Player player) {
    CompletableFuture.supplyAsync(() -> {
          final Gui gui = Gui.gui()
              .title(TextUtil.parse(
                  "&f\uE005 &8| &#39C9E5&lᴇ&#40CAE5&lᴠ&#46CCE6&lᴇ&#4DCDE6&lɴ&#53CEE6&lᴛ &#61D1E7&lᴋ&#67D2E7&lᴏ&#6ED3E7&lɴ&#74D4E7&lᴋ&#7BD6E8&lᴜ&#82D7E8&lʀ&#88D8E8&ls&#8FD9E8&lᴏ&#95DBE9&lᴡ&#9CDCE9&lʏ"))
              .disableAllInteractions()
              .rows(5)
              .create();
          GuiHelper.fillGui5(gui, Material.LIGHT_BLUE_STAINED_GLASS_PANE,
              Material.CYAN_STAINED_GLASS_PANE);
          return gui;
        })
        .thenAccept(gui -> {

          contestTicketFacade.getTicketsAmount(player.getUniqueId())
              .thenAccept(size -> {

                final double balance = contestFacade.balance(player.getUniqueId());

                gui.setItem(3, 5, FlameItemBuilder.of(Material.NAME_TAG)
                    .name(
                        "&#CB2EBA&lᴡ&#D43BBD&lʏ&#DD46C1&lᴛ&#E652C4&lᴡ&#EF5DC8&lᴀ&#E652C4&lʀ&#DD46C1&lᴢ&#D43BBD&lᴀ&#CB2EBA&lɴɪᴇ ʙɪʟᴇᴛᴜ")
                    .lore(
                        "",
                        " &#EE35DA⚠ &fTwoje statystyki:",
                        " &8➥ &fAktualnie posiadasz: &#CB2EBA" + size + " &fbiletów",
                        " &8➥ &fAktualnie posiadasz: &#E533D2" + balance + " &fpunktów",
                        "",
                        " &#FF69B4✎ &fInformacje o konkursie:",
                        " &8➥ &fKoszt jednego biletu: &#E533D210.000 &fpunktów",
                        " &8➥ &eIm więcej biletów, tym większe szanse!",
                        " &8➥ &fPunkty zdobędziesz za między innymi:",
                        " &8➥ &bᴋᴏᴘᴀɴɪᴇ&8, &eᴏᴛᴡɪᴇʀᴀɴɪᴇ sᴋʀᴢʏɴᴇᴋ&8, &cᴢᴀʙɪᴊᴀɴɪᴇ",
                        " &8➥ &6sᴛᴀɴɪᴇ ɴᴀ sᴛʀᴇғɪᴇ ᴀғᴋ &foraz",
                        " &8➥ &#DE47C1ɴᴀɢʀʏᴡᴀɴɪᴇ ᴛɪᴋᴛᴏᴋóᴡ &8-> &#DE47C11000 &fᴠɪᴇᴡs &8= &#DE47C12500 ᴘᴜɴᴋᴛóᴡ (/ᴛɪᴋᴛᴏᴋ ᴘᴀɴᴇʟ)",
                        "",
                        " &6⭐ &fNagrody w konkursie:",
                        " &8➥ &e1 miejsce&8: &e750 zł &flub sprzęt gamingowy do &e1000 zł",
                        " &8➥ &62 miejsce&8: &6500 zł &flub sprzęt gamingowy do &6750 zł",
                        " &8➥ &c3 miejsce&8: &c250 zł &flub sprzęt gamingowy do &c400 zł",
                        "",
                        " &#EE35DA⚠ &fLosowanie odbędzie się: &b26.10.2025 18:00",
                        "",
                        balance >= 10_000 ?
                            " &#32CD32✓ &aMasz wystarczająco punktów!"
                            : " &#FF6347✗ &cPotrzebujesz więcej punktów! &8(&7" + (10_000 - balance)
                                + " &fbrakuje&8)",
                        "",
                        balance >= 10_000 ?
                            "&#CB2EBA⬆ &fKliknij aby utworzyć bilet!"
                            : "&7Zdobądź więcej punktów grając na serwerze!"
                    )
                    .glow()
                    .asGuiItem(inventoryClickEvent -> {
                      final double freshBalance = contestFacade.balance(player.getUniqueId());

                      if (freshBalance < 10_000) {
                        BukkitMessage.from(
                                "&#FF6347⚠ &cNie posiadasz wystarczająco punktów do wytworzenia biletu!")
                            .deliver(player);
                        BukkitMessage.from(
                                "&cPotrzebujesz: &#E533D210.000 &cpunktów, masz: &#CB2EBA"
                                    + freshBalance + " &cpunktów")
                            .deliver(player);
                        flameDispatcher.dispatch(player::closeInventory);
                        return;
                      }

                      contestFacade.removePoints(player.getUniqueId(), 10_000);

                      contestTicketFacade.createTicket(player.getUniqueId(), player.getName())
                          .thenRun(() -> {
                            BukkitMessage.from("&#32CD32✓ &aPomyślnie utworzono bilet konkursowy!")
                                .deliver(player);
                            BukkitMessage.from(
                                    "&7Odebrano ci &#E533D210.000 &7punktów za utworzenie biletu.")
                                .deliver(player);
                            flameDispatcher.dispatch(player::closeInventory);
                          });

                    }));

                flameDispatcher.dispatch(() -> gui.open(player));

              });
        });

  }

}
