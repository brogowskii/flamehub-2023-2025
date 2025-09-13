package io.github.flamehub.reward.bukkit;

import dev.triumphteam.gui.guis.Gui;
import io.github.flamehub.commons.bukkit.text.TextUtil;
import io.github.flamehub.commons.bukkit.util.FlameItemBuilder;
import io.github.flamehub.commons.bukkit.util.GuiHelper;
import io.github.flamehub.commons.bukkit.util.SkullBuilder;
import io.github.flamehub.commons.server.NetworkServerContext;
import io.github.flamehub.reward.api.RewardReceivedEntry;
import io.github.flamehub.reward.api.RewardReceivedEntryRepository;
import org.bukkit.entity.Player;

public final class RewardGui {

  private final Player player;
  private final RewardReceivedEntryRepository rewardReceivedEntryService;
  private final RewardConfig rewardConfig;

  public RewardGui(
      final Player player,
      final RewardReceivedEntryRepository rewardReceivedEntryService,
      final RewardConfig rewardConfig
  ) {
    this.player = player;
    this.rewardReceivedEntryService = rewardReceivedEntryService;
    this.rewardConfig = rewardConfig;
  }

  public void open() {

    final Gui gui = Gui.gui()
        .title(TextUtil.parse(
            "&#319EC5\uD83C\uDFA3 &8| &#319EC5&lɴ&#39A6CD&lᴀ&#42ADD4&lɢ&#4AB5DC&lʀ&#42ADD4&lᴏ&#39A6CD&lᴅ&#319EC5&lᴀ"))
        .rows(5)
        .disableAllInteractions()
        .create();

    GuiHelper.fillGui5(gui);

    final RewardReceivedEntry rewardReceivedEntry = rewardReceivedEntryService
        .loadByPlayerNameAndServerCategory(player.getName(), NetworkServerContext.CURRENT_CATEGORY);

    final FlameItemBuilder lore = FlameItemBuilder.of(
            SkullBuilder.create("7873c12bffb5251a0b88d5ae75c7247cb39a75ff1a81cbe4c8a39b311ddeda"))
        .name("&#319EC5&lᴅ&#39A6CD&lɪ&#42ADD4&ls&#4AB5DC&lᴄ&#42ADD4&lᴏ&#39A6CD&lʀ&#319EC5&lᴅ")
        .lore(
            "",
            " &3⚠ &bInstrukcja odbierania:",
            " &7Dołącz na naszego discorda&8: &fdc.flamehub.pl",
            " &7Wejdź na kanał tekstowy&8: &fnagroda",
            " &7Po wejściu na kanał wybierz tryb",
            " &7na który chcesz odebrać nagrodę.",
            " &7Następnie podaj swój nick w formularzu",
            " &7nagroda zostanie nadana natychmiastowo!",
            "",
            " &4⚠ &cNagrodę możesz odebrać tylko raz na tym trybie!",
            "",
            " &3⚠ &bPo odebraniu nagrody otrzymasz:"
        );

    lore.appendLore(rewardConfig.getRewards());

    lore.appendLore(
        "",
        " &fStatus: " + (rewardReceivedEntry != null ? "&aOdebrałeś już swoją nagrodę."
            : "&cNie odebrałeś jeszcze swojej nagrody!"),
        "",
        "&fKliknij, aby otrzymać link z zaproszeniem na discorda."
    );

    gui.setItem(3, 5, lore
        .asGuiItem(inventoryClickEvent -> {

          player.sendMessage("https://dc.flamehub.pl/");

        }));

    gui.open(player);

  }

}
