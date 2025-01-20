package io.github.flamehub.wallet.log;

import dev.triumphteam.gui.guis.Gui;
import dev.triumphteam.gui.guis.PaginatedGui;
import io.github.flamehub.commons.bukkit.dispatcher.FlameDispatcher;
import io.github.flamehub.commons.bukkit.text.TextUtil;
import io.github.flamehub.commons.bukkit.util.FlameItemBuilder;
import io.github.flamehub.commons.bukkit.util.GuiHelper;
import io.github.flamehub.commons.bukkit.util.SkullBuilder;
import io.github.flamehub.commons.util.TimeUtil;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicInteger;
import org.bukkit.Material;
import org.bukkit.entity.Player;

public final class WalletLogGui {

  private final WalletLogRepository walletLogRepository;
  private final FlameDispatcher flameDispatcher;

  public WalletLogGui(final WalletLogRepository walletLogRepository,
      final FlameDispatcher flameDispatcher) {
    this.walletLogRepository = walletLogRepository;
    this.flameDispatcher = flameDispatcher;
  }

  public void open(final Player player, final WalletLogAction action, final String target) {

    PaginatedGui gui = Gui.paginated()
        .title(TextUtil.parse("&8&l" + action + " &8| &7" + target))
        .pageSize(28)
        .rows(6)
        .disableAllInteractions()
        .create();

    GuiHelper.fillGui6(gui);

    gui.setItem(6, 4, FlameItemBuilder.of(
            SkullBuilder.create("6e8c3ce2aee6cf2faade7db37bbae73a36627ac1473fef75b410a0af97659f"))
        .name("&cPoprzednia strona")
        .asGuiItem(inventoryClickEvent -> gui.previous()));

    gui.setItem(6, 6, FlameItemBuilder.of(
            SkullBuilder.create("6e8cd53664d9307b6869b9abbae2b7737ab762bb18bb34f31c5ca8f3edb63b6"))
        .name("&cNastępna strona")
        .asGuiItem(inventoryClickEvent -> gui.next()));

    CompletableFuture.supplyAsync(() -> walletLogRepository.load(target, action))
        .thenAccept(walletLogs -> {

          AtomicInteger index = new AtomicInteger(0);
          walletLogs
              .stream()
              .sorted((o1, o2) -> o2.getDate().compareTo(o1.getDate()))
              .forEach(walletLog -> {
                final FlameItemBuilder flameItemBuilder = FlameItemBuilder.of(Material.ITEM_FRAME)
                    .name("&8&l#" + index.getAndIncrement());

                if (action == WalletLogAction.BUY) {
                  flameItemBuilder.lore(
                      "",
                      "&7Akcja: &f" + walletLog.getAction(),
                      "&7Kwota: &f" + walletLog.getAmount() + " vPLN",
                      "&7Data: &f" + TimeUtil.formatDate(walletLog.getDate()),
                      "&7Transakcja: &f" + walletLog.getBoughtItem(),
                      ""
                  );
                } else {
                  flameItemBuilder.lore(
                      "",
                      "&7Akcja: &f" + walletLog.getAction(),
                      "&7Kwota: &f" + walletLog.getAmount() + " vPLN",
                      "&7Data: &f" + TimeUtil.formatDate(walletLog.getDate()),
                      "&7Komu: &f" + walletLog.getBuyerName(),
                      ""
                  );
                }

                gui.addItem(flameItemBuilder.asGuiItem());

              });



          flameDispatcher.dispatch(() -> gui.open(player));
        });


  }

}
