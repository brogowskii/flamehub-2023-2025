package io.github.flamehub.checksystem.history;

import dev.rollczi.litecommands.annotations.argument.Arg;
import dev.rollczi.litecommands.annotations.command.Command;
import dev.rollczi.litecommands.annotations.context.Context;
import dev.rollczi.litecommands.annotations.execute.Execute;
import dev.rollczi.litecommands.annotations.permission.Permission;
import dev.triumphteam.gui.guis.Gui;
import io.github.flamehub.commons.bukkit.dispatcher.FlameDispatcher;
import io.github.flamehub.commons.bukkit.message.BukkitMessage;
import io.github.flamehub.commons.bukkit.text.TextUtil;
import io.github.flamehub.commons.bukkit.util.FlameItemBuilder;
import io.github.flamehub.commons.util.TimeUtil;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import org.bukkit.Material;
import org.bukkit.entity.Player;

@Command(name = "checkhistory")
@Permission("server.commands.checkhistory")
public final class CheckHistoryCommand {

  private final FlameDispatcher flameDispatcher;
  private final CheckHistoryRepository checkHistoryRepository;

  public CheckHistoryCommand(FlameDispatcher flameDispatcher,
      CheckHistoryRepository checkHistoryRepository) {
    this.flameDispatcher = flameDispatcher;
    this.checkHistoryRepository = checkHistoryRepository;
  }

  @Execute(name = "findByAdmin")
  void findByAdmin(@Context Player player, @Arg String adminNickname) {

    BukkitMessage.from("&aŁaduje historię sprawdzania administratora &2" + adminNickname + "&a...")
        .deliver(player);
    CompletableFuture.supplyAsync(
            () -> checkHistoryRepository.loadAllIgnoreCase("adminNickname", adminNickname))
        .thenAcceptAsync(checkHistories -> {

          Gui gui = Gui.gui()
              .rows(6)
              .disableAllInteractions()
              .title(TextUtil.parse("&aHistoria administratora: &2" + adminNickname))
              .create();

          int i = checkHistories.size();
          for (CheckHistory checkHistory : checkHistories) {
            i--;
            gui.addItem(FlameItemBuilder.of(Material.GLOW_ITEM_FRAME)
                .name("&e&lHistoria sprawdzania &8&l#" + i)
                .lore(
                    "",
                    " &7ID: &f" + checkHistory.getId().toString(),
                    " &7Wynik sprawdzania: &f" + checkHistory.getType().toString(),
                    " &7Administrator: &f" + checkHistory.getAdminNickname() + " &8("
                        + checkHistory.getAdminUUID() + "&8)",
                    " &7Sprawdzany: &f" + checkHistory.getCheckedPlayerNickname() + " &8("
                        + checkHistory.getCheckedPlayerUUID() + "&8)",
                    " &7Data rozpoczęcia: &f" + TimeUtil.formatDate(checkHistory.getStartTime()),
                    " &7Data zakończenia: &f" + (checkHistory.getEndTime() == null
                        ? "&cJeszcze trwa..." : TimeUtil.formatDate(checkHistory.getEndTime())),
                    " &7Czas trwania: &f" +
                        (checkHistory.getEndTime() == null ?
                            TimeUtil.formatTime(
                                Duration.between(checkHistory.getStartTime(), Instant.now()))
                            : TimeUtil.formatTime(Duration.between(checkHistory.getStartTime(),
                                checkHistory.getEndTime()))
                        ),
                    "",
                    "&aKliknij aby zobaczyć historię",
                    "&awiadomości sprawdzanego gracza."
                )
                .asGuiItem(event -> {

                  flameDispatcher.dispatch(
                      () -> openChatHistory(player, checkHistory.getCheckedPlayerNickname(),
                          checkHistory.getCheckedPlayerChatHistory()));

                }));
          }

          flameDispatcher.dispatch(() -> gui.open(player));

        });

  }

  void openChatHistory(Player player, String checkedPlayerNickname, List<String> messages) {
    Gui gui = Gui.gui()
        .rows(6)
        .disableAllInteractions()
        .title(TextUtil.parse("&aHistoria wiadomości"))
        .create();

    for (String message : messages) {
      gui.addItem(FlameItemBuilder.of(Material.PAPER)
          .name(" ")
          .lore("&7" + checkedPlayerNickname + " &8» &f" + message)
          .asGuiItem());
    }

    gui.open(player);
  }


}
