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
            "&#a029b3\uD83C\uDFA3 &8| &#a029b3&lɴ&#af2dc4&lᴀ&#be30d5&lɢ&#cd34e6&lʀ&#be30d5&lᴏ&#af2dc4&lᴅ&#a029b3&lᴀ"))
        .rows(5)
        .disableAllInteractions()
        .create();

    GuiHelper.fillGui5(gui);

    RewardReceivedEntry rewardReceivedEntry = this.rewardReceivedEntryService.loadByPlayerNameAndServerCategory(
        player.getName(), this.networkServerCache.getCurrent().getCategory());
    gui.setItem(3, 5, FlameItemBuilder.of(SkullBuilder.createFromBase64(
            "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNGQ0MjMzN2JlMGJkY2EyMTI4MDk3ZjFjNWJiMTEwOWU1YzYzM2MxNzkyNmFmNWZiNmZjMjAwMDAwMTFhZWI1MyJ9fX0="))
        .name("&#9231b4&lD&#9a3ab9&li&#a244bd&ls&#aa4dc2&lc&#b256c7&lo&#ba60cb&lr&#c269d0&ld")
        .lore(
            "",
            "&d✪ Instrukcja odbierania:",
            " &8&l┣ &7Dołącz na naszego discorda&8: &fdc.flamehub.pl",
            " &8&l┣ &7Wejdź na kanał tekstowy&8: &fnagroda",
            " &8&l┣ &7Po wejściu na kanał wybierz tryb",
            " &8&l┣ &7na który chcesz odebrać nagrodę.",
            " &8&l┣ &7Następnie podaj swój nick w formularzu",
            " &8&l┗ &7nagroda zostanie nadana natychmiastowo!",
            "",
            "&4⚠ &cNagrodę możesz odebrać tylko raz na tym trybie!",
            "",
            "&d✪ Po odebraniu nagrody otrzymasz:",
            " &8&l┗ &f&l2x &x&8&9&5&3&E&1&lᴇ&x&8&1&4&E&D&5&lᴘ&x&7&A&4&9&C&9&lɪ&x&7&2&4&4&B&E&lᴄ&x&6&B&3&F&B&2&lᴋ&x&6&3&3&A&A&6&lɪ &x&6&B&3&F&B&2&lᴋ&x&7&2&4&4&B&E&lʟ&x&7&A&4&9&C&9&lᴜ&x&8&1&4&E&D&5&lᴄ&x&8&9&5&3&E&1&lᴢ",
            "",
            " &7Status: " + (rewardReceivedEntry != null ? "&aOdebrałeś już swoją nagrodę."
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
