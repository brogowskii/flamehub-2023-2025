package io.github.flamehub.voucher;

import io.github.flamehub.commons.bukkit.message.BukkitMessage;
import io.github.flamehub.commons.bukkit.text.TextUtil;
import io.github.flamehub.commons.bukkit.util.FlameItemBuilder;
import java.util.Map;
import java.util.Objects;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.Plugin;
import org.w3c.dom.Text;

public final class VoucherService {

  private final VoucherConfig voucherConfig;
  private final NamespacedKey key;

  public VoucherService(
      final VoucherConfig voucherConfig,
      final Plugin plugin
  ) {
    this.voucherConfig = voucherConfig;
    key = new NamespacedKey(plugin, "voucher");
  }

  public ItemStack createVoucher(final String id, final String name, final ItemStack item) {

    final FlameItemBuilder builder = FlameItemBuilder.of(item.getType())
        .name(name)
        .lore(
            "",
            " &8▶ &eKliknij prawym, aby użyć ten voucher.",
            ""
        )
        .glow();

    final ItemStack itemStack = builder
        .asItemStack();

    final ItemMeta itemMeta = itemStack.getItemMeta();
    itemMeta.getPersistentDataContainer().set(key, PersistentDataType.STRING, id);
    if (item.getItemMeta() != null && item.getItemMeta().hasCustomModelData()) {
      itemMeta.setCustomModelData(item.getItemMeta().getCustomModelData());
    }

    itemStack.setItemMeta(itemMeta);

    return itemStack;

  }

  public void useVoucher(final Player player, final ItemStack itemStack) {
    final ItemMeta itemMeta = itemStack.getItemMeta();
    if (!itemMeta.getPersistentDataContainer().has(key, PersistentDataType.STRING)) {
      return;
    }

    final String id = itemMeta.getPersistentDataContainer().get(key, PersistentDataType.STRING);
    final Voucher voucher = voucherConfig.getVoucherMap().get(id);
    if (voucher == null) {
      return;
    }

    final ItemStack clone = itemStack.clone();
    clone.setAmount(1);
    player.getInventory().removeItem(clone);

    for (final String command : voucher.getCommands()) {
      Bukkit.dispatchCommand(Bukkit.getConsoleSender(),
          command.replace("{player}", player.getName()));
    }

    BukkitMessage.from(
            "&aPomyślnie użyto: " + TextUtil.serialize(clone.getItemMeta().displayName()))
        .deliver(player);
  }

  private static final PlainTextComponentSerializer PLAIN = PlainTextComponentSerializer.plainText();

  public Voucher findByItem(final ItemStack itemStack) {
    if (itemStack == null) return null;

    for (final Voucher v : voucherConfig.getVoucherMap().values()) {

      if (TextUtil.serialize(itemStack.getItemMeta().displayName()).equalsIgnoreCase(TextUtil.serialize(v.getItemStack().getItemMeta().displayName()))) {
        return v;
      }
    }
    return null;
  }

  private static String normalizePlain(Component c) {
    // zbij do zwykłego tekstu, wytnij spacje po bokach (unikniesz różnic w stylu/kolorach)
    return PLAIN.serialize(c).trim();
  }


  public NamespacedKey getKey() {
    return key;
  }
}
