package io.github.flamehub.tiktok.shop;

import dev.triumphteam.gui.guis.BaseGui;
import dev.triumphteam.gui.guis.Gui;
import io.github.flamehub.commons.bukkit.CommonsPlugin;
import io.github.flamehub.commons.bukkit.message.BukkitMessage;
import io.github.flamehub.commons.bukkit.text.TextUtil;
import io.github.flamehub.commons.bukkit.util.FlameItemBuilder;
import io.github.flamehub.commons.network.message.NetworkMessageType;
import io.github.flamehub.commons.util.DiscordWebhook;
import io.github.flamehub.commons.util.TimeUtil;
import io.github.flamehub.tiktok.TikTokConstants;
import io.github.flamehub.tiktok.user.TikTokUser;
import java.awt.Color;
import java.time.Instant;
import java.util.Arrays;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;

public final class TikTokShopGui {

  private final TikTokShopConfig tikTokShopConfig;

  public TikTokShopGui(final TikTokShopConfig tikTokShopConfig) {
    this.tikTokShopConfig = tikTokShopConfig;
  }

  public static void fillGui5(BaseGui gui) {
    gui.getFiller()
        .fillBorder(FlameItemBuilder.of(Material.WHITE_STAINED_GLASS_PANE).name(" ").asGuiItem());
    gui.setItem(Arrays.asList(0, 8, 36, 44),
        FlameItemBuilder.of(Material.BLACK_STAINED_GLASS_PANE).name(" ").asGuiItem());
    gui.setItem(Arrays.asList(1, 7, 9, 17, 27, 35, 37, 43),
        FlameItemBuilder.of(Material.RED_STAINED_GLASS_PANE).name(" ").asGuiItem());
    gui.setItem(40, FlameItemBuilder.of(Material.AIR).asGuiItem());
    gui.setItem(4, FlameItemBuilder.of(Material.AIR).asGuiItem());
  }

  public void open(final Player player, final TikTokUser tikTokUser) {
    Gui gui = Gui.gui()
        .title(TextUtil.parse("&c♫ &8| &c&lᴛɪᴋᴛᴏᴋ sʜᴏᴘ"))
        .rows(5)
        .disableAllInteractions()
        .create();

    fillGui5(gui);

    for (final TikTokShopItem shopItem : tikTokShopConfig.getShopItems()) {

      gui.setItem(shopItem.getSlot(), FlameItemBuilder.of(shopItem.getGuiIcon())
          .name(shopItem.getGuiName())
          .lore(shopItem.getGuiLore())
          .glow()
          .asGuiItem(inventoryClickEvent -> {

            if (tikTokUser.getPoints() < shopItem.getPrice()) {
              BukkitMessage.from("&cNie masz wystarczająco punktów!").send(player);
              return;
            }

            tikTokUser.setPoints(tikTokUser.getPoints() - shopItem.getPrice());
            shopItem.getCommands().forEach(
                command -> Bukkit.dispatchCommand(Bukkit.getConsoleSender(),
                    command.replace("{player}", player.getName())));
            BukkitMessage.from("&aPomyślnie zakupiono!").send(player);

            gui.close(player);

            DiscordWebhook discordWebhook = new DiscordWebhook(TikTokConstants.WEBHOOK_URL);
            DiscordWebhook.EmbedObject embed = new DiscordWebhook.EmbedObject();
            embed.setAuthor("TIKTOK SKLEP || Flamehub.pl", null, "https://i.imgur.com/B3lRUdp.png");
            embed.setColor(Color.YELLOW);
            embed.addField("**Akcja:**", "Kupno " + shopItem.getGuiName(), true);
            embed.addField("**Kto:**", player.getName(), true);
            embed.addField("**Punkty:**", String.valueOf(tikTokUser.getPoints()), true);
            embed.addField("**Cena:**", String.valueOf(shopItem.getPrice()), true);
            embed.setImage("https://minotar.net/helm/" + player.getName() + "/100.png");
            embed.setTimestamp(Instant.now().toString());
            embed.setFooter("FlameHub.pl • " + TimeUtil.formatDate(Instant.now()),
                "https://i.imgur.com/B3lRUdp.png");
            discordWebhook.addEmbed(embed);
            discordWebhook.execute();

          }));

    }

    gui.open(player);
  }

}
