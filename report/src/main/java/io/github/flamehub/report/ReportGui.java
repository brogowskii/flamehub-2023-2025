package io.github.flamehub.report;

import dev.triumphteam.gui.guis.Gui;
import io.github.flamehub.commons.bukkit.dispatcher.FlameDispatcher;
import io.github.flamehub.commons.bukkit.message.BukkitMessage;
import io.github.flamehub.commons.bukkit.text.TextUtil;
import io.github.flamehub.commons.bukkit.util.FlameItemBuilder;
import io.github.flamehub.commons.bukkit.util.GuiHelper;
import io.github.flamehub.commons.network.message.NetworkMessageFilter;
import io.github.flamehub.commons.network.message.NetworkMessageService;
import io.github.flamehub.commons.network.message.NetworkMessageType;
import io.github.flamehub.commons.network.player.NetworkPlayer;
import io.github.flamehub.commons.server.NetworkServerFacade;
import java.util.List;
import org.bukkit.entity.Player;

public final class ReportGui {

  private final ReportRepository reportRepository;
  private final NetworkServerFacade networkServerFacade;
  private final NetworkMessageService networkMessageService;
  private final FlameDispatcher flameDispatcher;

  public ReportGui(
      final ReportRepository reportRepository,
      final NetworkServerFacade networkServerFacade,
      final NetworkMessageService networkMessageService,
      final FlameDispatcher flameDispatcher
  ) {
    this.reportRepository = reportRepository;
    this.networkServerFacade = networkServerFacade;
    this.networkMessageService = networkMessageService;
    this.flameDispatcher = flameDispatcher;
  }

  public void open(final Player player, final NetworkPlayer target) {

    final Gui gui = Gui.gui()
        .rows(5)
        .title(TextUtil.parse("&c&lZgłoś gracza: &f" + target.getName()))
        .disableAllInteractions()
        .create();

    GuiHelper.fillGui5(gui);

    for (final ReportType value : ReportType.values()) {
      if (value.getSlot() == -1) {
        continue;
      }

      gui.setItem(value.getSlot(), FlameItemBuilder.of(value.getMaterial())
          .name(value.getName())
          .asGuiItem(inventoryClickEvent -> {

            final Report report = new Report(value, target.getName(), target.getUniqueId(),
                player.getName());

            flameDispatcher.dispatchAsync(() -> {
              reportRepository.save(report);
              networkMessageService.send(List.of("",
                      "&#CD0B0B[Zgłoszenie] &7(" + networkServerFacade.getCurrent().getName()
                          + "&7) &8| &cGracz &#CD0B0B" + target.getName()
                          + " &8-▶ &czgłoszony za: &#CD0B0B" + value, ""),
                  NetworkMessageFilter.builder()
                      .targetPermission("server.commands.reportadmin")
                      .build(), NetworkMessageType.CHAT);
            });

            gui.close(player);
            BukkitMessage.from(
                    "&cZgłosiłeś gracza &4" + target.getName() + " &cza " + value.getName())
                .deliver(player);

          }));
    }

    gui.open(player);

  }
}
