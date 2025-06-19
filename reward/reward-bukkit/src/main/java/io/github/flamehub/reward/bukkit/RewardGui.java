package io.github.flamehub.reward.bukkit;

import dev.triumphteam.gui.guis.Gui;
import io.github.flamehub.commons.bukkit.text.TextUtil;
import io.github.flamehub.commons.bukkit.util.FlameItemBuilder;
import io.github.flamehub.commons.bukkit.util.GuiHelper;
import io.github.flamehub.commons.bukkit.util.SkullBuilder;
import io.github.flamehub.commons.server.NetworkServerCache;
import io.github.flamehub.reward.api.RewardReceivedEntry;
import io.github.flamehub.reward.api.RewardReceivedEntryRepository;
import org.bukkit.entity.Player;

public final class RewardGui {

  private final Player player;
  private final RewardReceivedEntryRepository rewardReceivedEntryService;
  private final NetworkServerCache networkServerCache;

  public RewardGui(Player player, RewardReceivedEntryRepository rewardReceivedEntryService,
      NetworkServerCache networkServerCache) {
    this.player = player;
    this.rewardReceivedEntryService = rewardReceivedEntryService;
    this.networkServerCache = networkServerCache;
  }

  public void open() {

    Gui gui = Gui.gui()
        .title(TextUtil.parse(
            "&#319EC5\uD83C\uDFA3 &8| &#319EC5&lɴ&#39A6CD&lᴀ&#42ADD4&lɢ&#4AB5DC&lʀ&#42ADD4&lᴏ&#39A6CD&lᴅ&#319EC5&lᴀ"))
        .rows(5)
        .disableAllInteractions()
        .create();

    GuiHelper.fillGui5(gui);

    RewardReceivedEntry rewardReceivedEntry = rewardReceivedEntryService.loadByPlayerNameAndServerCategory(
        player.getName(), networkServerCache.getCurrent().getCategory());
    gui.setItem(3, 5, FlameItemBuilder.of(SkullBuilder.create("7873c12bffb5251a0b88d5ae75c7247cb39a75ff1a81cbe4c8a39b311ddeda"))
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
            " &3⚠ &bPo odebraniu nagrody otrzymasz:",
            "  &8▶ &f&lx2 &x&A&2&1&6&D&2&lᴇ&x&A&7&1&E&D&6&lᴘ&x&A&B&2&6&D&9&lɪ&x&B&0&2&E&D&D&lᴄ&x&B&5&3&6&E&0&lᴋ&x&B&9&3&E&E&4&lɪ &x&B&8&3&C&E&3&lᴋ&x&B&3&3&3&D&F&lʟ&x&A&D&2&9&D&A&lᴜ&x&A&8&2&0&D&6&lᴄ&x&A&2&1&6&D&2&lᴢ",
            "",
            " &fStatus: " + (rewardReceivedEntry != null ? "&aOdebrałeś już swoją nagrodę."
                : "&cNie odebrałeś jeszcze swojej nagrody!"),
            "",
            "&fKliknij, aby otrzymać link z zaproszeniem na discorda."
        )
        .asGuiItem(inventoryClickEvent -> {

          player.sendMessage("https://dc.flamehub.pl/");

        }));

    gui.open(player);

  }

}
