package io.github.flamehub.report;

import dev.triumphteam.gui.guis.Gui;
import dev.triumphteam.gui.guis.PaginatedGui;
import io.github.flamehub.commons.bukkit.dispatcher.FlameDispatcher;
import io.github.flamehub.commons.bukkit.message.BukkitMessage;
import io.github.flamehub.commons.bukkit.text.TextUtil;
import io.github.flamehub.commons.bukkit.util.FlameItemBuilder;
import io.github.flamehub.commons.util.TimeUtil;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;

public final class ReportAdminGui {

  private final FlameDispatcher flameDispatcher;
  private final ReportRepository reportRepository;

  public ReportAdminGui(
      final FlameDispatcher flameDispatcher,
      final ReportRepository reportRepository
  ) {
    this.flameDispatcher = flameDispatcher;
    this.reportRepository = reportRepository;
  }

  public void open(final Player player) {

    final PaginatedGui gui = Gui.paginated()
        .rows(6)
        .pageSize(45)
        .disableAllInteractions()
        .title(TextUtil.parse("&8Lista zgłoszeń"))
        .create();

    flameDispatcher.dispatchAsync(
        () -> {
          final List<Report> reports = reportRepository.loadAll();
          reports.sort((r1, r2) -> r2.getCreateDate().compareTo(r1.getCreateDate()));
          final AtomicInteger i = new AtomicInteger();

          reports.forEach(
              report -> {
                final int andIncrement = i.getAndIncrement();
                gui.addItem(FlameItemBuilder.of(Material.ITEM_FRAME)
                    .name("&8&l#" + andIncrement)
                    .lore(
                        "",
                        "&8▶ &7Cheaty: &f" + report.getType(),
                        "&8▶ &7Zgłaszający: &f" + report.getReporter(),
                        "&8▶ &7Podejrzany: &c" + report.getTargetName(),
                        "&8▶ &7Data: &f" + TimeUtil.formatDate(report.getCreateDate()),
                        "",
                        "&eKliknij lewym, aby przeteleportować się do podejrzanego.",
                        "&eKliknij prawym, aby sprawdzić tego gracza.",
                        "&eKliknij scroll, aby usunąć zgłoszenie."
                    )
                    .asGuiItem(event -> {

                      if (event.getClick().isLeftClick()) {

                        flameDispatcher.dispatch(
                            () -> Bukkit.dispatchCommand(player, "tp " + report.getTargetName()));
                      } else if (event.getClick().isRightClick()) {

                        reportRepository.delete(report);
                        flameDispatcher.dispatch(() -> {

                          gui.close(player);
                          Bukkit.dispatchCommand(player, "sprawdz " + report.getTargetName());

                        });


                      } else if (event.getClick().isMouseClick()) {

                        reportRepository.delete(report);
                        open(player);

                        BukkitMessage.from("&cUsunięto zgłoszenie!").deliver(player);

                      }

                    }));
              });

          flameDispatcher.dispatch(() -> gui.open(player));
        });
  }

}
