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
            "  &8▶ &f&lx2 &x&E&2&C&B&1&2&lᴋ&x&E&5&C&E&1&5&lʟ&x&E&8&D&1&1&9&lᴜ&x&E&B&D&4&1&C&lᴄ&x&E&E&D&7&1&F&lᴢ &x&F&4&D&D&2&6&lʟ&x&F&7&E&0&2&9&lᴇ&x&F&A&E&3&2&C&lɢ&x&F&7&E&0&2&8&lᴇ&x&F&3&D&C&2&5&lɴ&x&F&0&D&9&2&1&lᴅ&x&E&C&D&5&1&D&lᴀ&x&E&9&D&2&1&9&lʀ&x&E&5&C&E&1&6&lɴʏ",
            "  &8▶ &f&lx1 &x&4&6&C&A&3&E&lʀ&x&4&C&C&E&4&4&lᴢ&x&5&1&D&2&4&9&lᴀ&x&5&7&D&6&4&F&lᴅ&x&5&C&D&9&5&4&lᴋ&x&6&2&D&D&5&A&lɪ &x&6&0&D&C&5&8&lᴋ&x&5&A&D&8&5&2&lʟ&x&5&3&D&3&4&B&lᴜ&x&4&D&C&F&4&5&lᴄ&x&4&6&C&A&3&E&lᴢ",
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
