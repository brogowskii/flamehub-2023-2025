package io.github.flamehub.cheque;

import io.github.flamehub.commons.bukkit.message.BukkitMessage;
import io.github.flamehub.commons.bukkit.util.FlameItemBuilder;
import io.github.flamehub.commons.bukkit.util.NumberConverter;
import io.github.flamehub.commons.bukkit.util.TitleUtil;
import io.github.flamehub.commons.util.DiscordWebhook;
import io.github.flamehub.commons.util.TimeUtil;
import java.awt.Color;
import java.time.Instant;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.Plugin;

public final class ChequeService {

  private final static String CHEQUE_WEBHOOK = "https://discord.com/api/webhooks/1334287742724472884/5VKTF4lCNAaUFecyXdTZUsjxzR0VXQo600NvFlQOAyRo5xt-7ZV01ectlqv-pk_Wtntt";

  private final NamespacedKey chequeKey;
  private final Economy economy;

  public ChequeService(final Plugin plugin, final Economy economy) {
    chequeKey = new NamespacedKey(plugin, "cheque");
    this.economy = economy;
  }

  public ItemStack generateCheque(final double money, final Player player) {

    final String convertedMoney = NumberConverter.convertNumber(money);
    final ItemStack itemStack = FlameItemBuilder.of(Material.PAPER)
        .customModelData(10233)
        .name("&#00B16A&lᴄᴢᴇᴋ ᴘɪᴇɴɪᴇᴢɴʏ &8- &#00B16A&l$" + convertedMoney)
        .glow()
        .lore(
            "",
            " &8▶ &fUżywając czeku otrzymasz na swoje",
            " &8▶ &fkonto &#00B16A+" + convertedMoney + "$",
            "",
            " &8▶ &fUtworzył: &#00B16A" + player.getName() + " &8(&7" + TimeUtil.formatDate(
                Instant.now()) + "&8)",
            "",
            "&#00B16AKliknij prawym, aby wykorzystać."
        )
        .asItemStack();

    final ItemMeta itemMeta = itemStack.getItemMeta();
    itemMeta.getPersistentDataContainer().set(chequeKey, PersistentDataType.DOUBLE, money);
    itemStack.setItemMeta(itemMeta);

    return itemStack;

  }

  public double useCheque(final Player player, final ItemStack item) {
    final ItemMeta itemMeta = item.getItemMeta();
    final PersistentDataContainer persistentDataContainer = itemMeta.getPersistentDataContainer();
    if (!persistentDataContainer.has(chequeKey, PersistentDataType.DOUBLE)) {
      return 0;
    }

    final Double money = persistentDataContainer.get(chequeKey, PersistentDataType.DOUBLE);
    if (money == null) {
      return 0;
    }

    final ItemStack clone = item.clone();
    clone.setAmount(1);
    player.getInventory().removeItem(clone);

    final String s = NumberConverter.convertNumber(money);
    TitleUtil.title(player, " ", "&a+" + s + "$", 0, 20, 20);

    BukkitMessage.from("&aOtrzymałeś &2$" + s + " &ana swoje konto.").deliver(player);
    economy.depositPlayer(player, money);
    return money;

  }

  public void sendWebhook(final ChequeLog chequeLog) {
    final DiscordWebhook discordWebhook = new DiscordWebhook(CHEQUE_WEBHOOK);
    final DiscordWebhook.EmbedObject embed = new DiscordWebhook.EmbedObject();
    embed.setAuthor("CZEKI || Flamehub.pl", null,
        "https://cdn.discordapp.com/attachments/1075821576450228244/1182051810182185121/flame_marzec_bez_tla.png?ex=65a83489&is=6595bf89&hm=63c8c376307f5084a8ccfd54c7a3f431f68491483c84f178c453df9e2e1a96d8&");
    embed.setColor(Color.RED);
    embed.addField("**Kto:**", chequeLog.getWho(), true);
    embed.addField("**Akcja:**", chequeLog.getType().toString(), true);
    embed.addField("**Kwota:**", chequeLog.getMoney() + "$", true);
    embed.setTimestamp(Instant.now().toString());
    embed.setFooter("FlameHub.pl • " + TimeUtil.formatDate(Instant.now()),
        "https://cdn.discordapp.com/attachments/1075821576450228244/1182051810182185121/flame_marzec_bez_tla.png?ex=65a83489&is=6595bf89&hm=63c8c376307f5084a8ccfd54c7a3f431f68491483c84f178c453df9e2e1a96d8&");
    discordWebhook.addEmbed(embed);
    discordWebhook.execute();
  }


}
