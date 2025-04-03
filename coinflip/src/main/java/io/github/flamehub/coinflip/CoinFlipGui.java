package io.github.flamehub.coinflip;

import com.destroystokyo.paper.profile.PlayerProfile;
import dev.triumphteam.gui.guis.Gui;
import io.github.flamehub.commons.bukkit.text.TextUtil;
import io.github.flamehub.commons.bukkit.util.FlameItemBuilder;
import io.github.flamehub.commons.bukkit.util.GuiHelper;
import io.github.flamehub.commons.bukkit.util.HexUtil;
import io.github.flamehub.commons.bukkit.util.SkullBuilder;
import java.util.Collection;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.jetbrains.annotations.NotNull;

public final class CoinFlipGui {

  private final CoinFlipConfig coinFlipConfig;
  private final CoinFlipGameCache coinFlipGameCache;

  public CoinFlipGui(
      final CoinFlipConfig coinFlipConfig,
      final CoinFlipGameCache coinFlipGameCache
  ) {
    this.coinFlipConfig = coinFlipConfig;
    this.coinFlipGameCache = coinFlipGameCache;
  }

  public void open(final Player player) {

    final Gui gui = Gui.gui()
        .title(TextUtil.parse("&#CB2EBA&lᴄ&#D430C2&lᴏ&#DD32CA&lɪ&#E533D2&lɴ&#EE35DA&lꜰ&#E233CF&lʟ&#D730C5&lɪ&#CB2EBA&lᴘ"))
        .rows(6)
        .disableAllInteractions()
        .create();
    GuiHelper.fillGui6(gui);


    final Collection<CoinFlipGame> values = coinFlipGameCache.values();
    int i = values.size();
    for (final CoinFlipGame value : values) {

      final CoinFlipPlayer creator = value.getCreator();
      final OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(creator.getUniqueId());
      final PlayerProfile playerProfile = offlinePlayer.getPlayerProfile();
      final Component displayName = coinFlipConfig.getCurrency().getItemMeta().displayName();
      final String serializedDisplayName = TextUtil.serialize(displayName);
      final String coloredCreatorName = HexUtil.interpolateColors(creator.getName(), "#CB2EBA", "#EE35DA", false);
      gui.addItem(FlameItemBuilder.of(create(playerProfile))
          .name("&#CB2EBA&lᴄ&#D430C2&lᴏ&#DD32CA&lɪ&#E533D2&lɴ&#EE35DA&lꜰ&#E233CF&lʟ&#D730C5&lɪ&#CB2EBA&lᴘ &f#" + i--)
          .lore(
              "",
              "&8▶ &fStworzył: " + coloredCreatorName,
              "&8▶ &fStawka: &fx" + value.getBet() + " " + serializedDisplayName,
              "",
              "&eKliknij, aby zagrać!"
          )
          .asGuiItem(inventoryClickEvent -> {

            if (coinFlipGameCache.get(value.getId()) == null) {
              player.sendMessage(TextUtil.parse("&cTa gra już nie istnieje."));
              return;
            }

            player.closeInventory();


          }));

    }

    gui.open(player);

  }

  public static ItemStack create(@NotNull PlayerProfile playerProfile) {
    ItemStack head = new ItemStack(Material.PLAYER_HEAD);
    SkullMeta meta = (SkullMeta)head.getItemMeta();
    meta.setPlayerProfile(playerProfile);
    head.setItemMeta(meta);
    return head;
  }

}
