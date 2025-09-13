package io.github.flamehub.voucher;

import dev.rollczi.litecommands.annotations.argument.Arg;
import dev.rollczi.litecommands.annotations.command.Command;
import dev.rollczi.litecommands.annotations.context.Context;
import dev.rollczi.litecommands.annotations.execute.Execute;
import dev.rollczi.litecommands.annotations.join.Join;
import dev.rollczi.litecommands.annotations.permission.Permission;
import dev.triumphteam.gui.guis.Gui;
import io.github.flamehub.commons.bukkit.config.FlameConfigRefresher;
import io.github.flamehub.commons.bukkit.text.TextUtil;
import io.github.flamehub.commons.bukkit.util.FlameItemBuilder;
import io.github.flamehub.commons.bukkit.util.InventoryUtil;
import io.github.flamehub.commons.config.FlameConfigService;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

@Command(name = "voucher")
@Permission("server.commands.voucher")
public final class VoucherCommand extends FlameConfigRefresher {

  private final VoucherService voucherService;
  private final VoucherConfig voucherConfig;
  private final FlameConfigService flameConfigService;

  public VoucherCommand(
      final VoucherService voucherService,
      final VoucherConfig voucherConfig,
      final FlameConfigService flameConfigService
  ) {
    super(flameConfigService, VoucherConfig.class);
    this.voucherService = voucherService;
    this.voucherConfig = voucherConfig;
    this.flameConfigService = flameConfigService;
  }

  @Execute(name = "vouchers")
  void vouchers(@Context final Player player) {
    final Gui gui = Gui.gui()
        .title(TextUtil.parse("&8&lVouchery"))
        .disableAllInteractions()
        .disableOtherActions()
        .rows(6)
        .create();

    for (final Map.Entry<String, Voucher> entry : voucherConfig.getVoucherMap().entrySet()) {

      final Voucher value = entry.getValue();
      final String key = entry.getKey();

      final FlameItemBuilder flameItemBuilder = FlameItemBuilder.of(value.getItemStack().clone())
          .appendLore(
              " &7ID: &f" + key,
              " &7Komendy:"
          );

      final List<String> commands = value.getCommands();
      for (final String command : commands) {
        flameItemBuilder.appendLore(" &8 - &f" + command);
      }

      gui.addItem(flameItemBuilder.asGuiItem(event -> {

        InventoryUtil.addItem(player, value.getItemStack().clone());

      }));

    }

    gui.open(player);
  }

  @Execute(name = "create")
  void create(@Context final Player player, @Arg final String id, @Join final String name) {
    final ItemStack voucher = voucherService.createVoucher(id, name,
        player.getInventory().getItemInMainHand());
    final ItemStack clone = voucher.clone();
    InventoryUtil.addItem(player, clone);

    voucherConfig.getVoucherMap().put(id, new Voucher(clone, new ArrayList<>()));
    flameConfigService.save(VoucherConfig.class);
  }

  @Execute(name = "addCommand")
  void addCommand(@Context final Player player, @Arg final String id, @Join final String command) {
    if (!voucherConfig.getVoucherMap().containsKey(id)) {
      return;
    }

    voucherConfig.getVoucherMap().get(id).getCommands().add(command);
    flameConfigService.save(VoucherConfig.class);
  }

  @Execute(name = "delete")
  void delete(@Arg final String id) {
    if (!voucherConfig.getVoucherMap().containsKey(id)) {
      return;
    }

    voucherConfig.getVoucherMap().remove(id);
    flameConfigService.save(VoucherConfig.class);
  }

  @Execute(name = "fix")
  void fix(@Context final CommandSender sender) {

    for (final Map.Entry<String, Voucher> stringVoucherEntry : voucherConfig.getVoucherMap().entrySet()) {

      final ItemStack itemStack = stringVoucherEntry.getValue().getItemStack();
      final ItemMeta itemMeta = itemStack.getItemMeta();
      itemMeta.getPersistentDataContainer().set(voucherService.getKey(), PersistentDataType.STRING, stringVoucherEntry.getKey());
      itemStack.setItemMeta(itemMeta);

    }

    flameConfigService.save(VoucherConfig.class);
  }

  @Execute(name = "reload")
  void reload(@Context final CommandSender sender) {
    refresh(sender);
  }

  @Execute(name = "update")
  void update(@Context final CommandSender sender) {
    refreshAndBroadcast(sender);
  }

}
