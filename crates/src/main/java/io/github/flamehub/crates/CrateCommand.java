package io.github.flamehub.crates;

import dev.rollczi.litecommands.annotations.argument.Arg;
import dev.rollczi.litecommands.annotations.command.Command;
import dev.rollczi.litecommands.annotations.context.Context;
import dev.rollczi.litecommands.annotations.execute.Execute;
import dev.rollczi.litecommands.annotations.join.Join;
import dev.rollczi.litecommands.annotations.permission.Permission;
import io.github.flamehub.commons.bukkit.config.FlameConfigRefresher;
import io.github.flamehub.commons.bukkit.message.BukkitMessage;
import io.github.flamehub.commons.bukkit.text.TextBuilder;
import io.github.flamehub.commons.bukkit.text.TextUtil;
import io.github.flamehub.commons.bukkit.util.InventoryUtil;
import io.github.flamehub.commons.bukkit.util.TitleUtil;
import io.github.flamehub.commons.config.FlameConfigService;
import io.github.flamehub.commons.util.TimeUtil;
import java.text.ParseException;
import java.time.Duration;
import java.time.Instant;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import panda.std.Option;

@Command(name = "crate")
@Permission("server.commands.crate")
public final class CrateCommand extends FlameConfigRefresher {

  private final FlameConfigService flameConfigService;
  private final CratesConfig cratesConfig;

  public CrateCommand(FlameConfigService flameConfigService, CratesConfig cratesConfig) {
    super(flameConfigService, CratesConfig.class);
    this.flameConfigService = flameConfigService;
    this.cratesConfig = cratesConfig;
  }

  @Execute(name = "reload")
  void reload(@Context CommandSender sender) {
    super.refreshConfigLocally(sender);

  }

  @Execute(name = "update")
  void update(@Context CommandSender sender) {
    super.refreshConfigRemote(sender);
  }

  @Execute(name = "setitem")
  void setItem(@Context Player player, @Arg Crate crate, @Arg int slot, @Arg double chance) {
    ItemStack itemInMainHand = player.getInventory().getItemInMainHand();
    if (itemInMainHand.getType().isAir()) {
      return;
    }

    CrateItem item = new CrateItem(itemInMainHand.getType().toString(), itemInMainHand.clone(),
        chance, 5);
    crate.getItemsBySlot().put(slot, item);
    this.flameConfigService.saveLocally(CratesConfig.class);
    player.sendMessage(TextUtil.parse(
        "&aPomyślnie ustawiono item do skrzynki na slot " + slot + " z szansą " + chance + "%."));

  }

  @Execute(name = "deleteitem")
  void deleteItem(@Context Player player, @Arg Crate crate, @Arg int slot) {
    crate.getItemsBySlot().remove(slot);
    this.flameConfigService.saveLocally(CratesConfig.class);
    player.sendMessage(TextUtil.parse("&aPomyślnie usunięto item z slotu " + slot));
  }

  @Execute(name = "create")
  void create(@Context Player player, @Arg String crateId) {
    if (this.cratesConfig.findById(crateId) != null) {
      TextBuilder.builder()
          .text("&cSkrzynka o podanej nazwie już istnieje.")
          .send(player);
      return;
    }
    this.cratesConfig.add(new Crate(crateId));
    this.flameConfigService.saveLocally(CratesConfig.class);
    BukkitMessage.from("&aPomyślnie stworzyłeś skrzynie o nazwie &2" + crateId)
        .send(player);
  }

  @Execute(name = "delete")
  void delete(@Context Player player, @Arg Crate crate) {
    this.cratesConfig.remove(crate);
    this.flameConfigService.saveLocally(CratesConfig.class);
    BukkitMessage.from("&aPomyślnie usunięto skrzynie o nazwie &2" + crate.getId())
        .send(player);
  }


  @Execute(name = "setkey")
  void key(@Context Player player, @Arg Crate crate) {
    crate.setKey(player.getInventory().getItemInMainHand().clone());
    this.flameConfigService.saveLocally(CratesConfig.class);
    BukkitMessage.from("&aPomyślnie ustawiono klucz dla skrzyni o nazwie &2" + crate.getId())
        .send(player);
  }

  @Execute(name = "guiname")
  void guiName(@Context Player player, @Arg Crate crate, @Join String guiName) {
    crate.setGuiName(guiName);
    this.flameConfigService.saveLocally(CratesConfig.class);
    BukkitMessage.from(
            "&aPomyślnie ustawiono nową nazwe gui dla skrzyni o nazwie &2" + crate.getId())
        .send(player);
  }

  @Execute(name = "setlocation")
  void set(@Context Player player, @Arg Crate crate) {
    Block block = player.getTargetBlock(5);
    if (block == null) {
      player.sendMessage("block is null");
      return;
    }

    if (block.getType() == Material.AIR) {
      player.sendMessage("block is air");
      return;
    }

    crate.setLocation(block.getLocation());
    this.flameConfigService.saveLocally(CratesConfig.class);
    BukkitMessage.from("&aPomyślnie ustawiono nowa lokalizacje skrzyni o nazwie &2" + crate.getId())
        .send(player);
  }

  @Execute(name = "givekey")
  void giveKey(@Context CommandSender sender, @Arg Crate crate, @Arg Player target,
      @Arg Option<Integer> optAmount) {
    ItemStack key = crate.getKey();
    if (key == null) {
      BukkitMessage.from("&cTa skrzynia nie posiada ustawionego klucza.").send(sender);
      return;
    }

    int amount = 1;
    if (optAmount.isPresent()) {
      amount = optAmount.get();
    }

    ItemStack clone = key.clone();
    clone.setAmount(amount);
    InventoryUtil.addItem(target, clone);
  }

  @Execute(name = "keyall")
  void keyAll(@Context CommandSender player, @Arg Crate crate, @Arg int amount) {

    ItemStack key = crate.getKey();
    if (key == null) {
      BukkitMessage.from("&cTa skrzynia nie posiada ustawionego klucza.").send(player);
      return;
    }

    ItemStack clone = key.clone();
    clone.setAmount(amount);
    for (Player it : Bukkit.getOnlinePlayers()) {

      TitleUtil.title(it, "&6&lKlucze",
          "&7Cały serwer otrzymał &f&lx" + amount + " &7kluczy do " + crate.getGuiName(),
          20,
          60,
          20
      );
      InventoryUtil.addItem(it, clone);

    }

  }

  @Execute(name = "enabledfrom")
  void switchStatus(@Context CommandSender commandSender, @Arg Crate crate, @Join String date) {

    Instant instant;
    try {
      instant = TimeUtil.dateFromString(date).toInstant();
    } catch (ParseException e) {
      BukkitMessage.from("&cPodana data jest nieprawidłowa. Format: &6HH:mm:ss dd.MM.yyyy")
          .send(commandSender);
      return;
    }

    crate.setEnabledFrom(instant);
    BukkitMessage.from("&7Pomyslnie ustawiłeś odpalenie tej skrzyni za: &6" + TimeUtil.formatTime(
            Duration.between(Instant.now(), instant)))
        .send(commandSender);
    this.flameConfigService.saveLocally(CratesConfig.class);
  }

  @Execute(name = "clear")
  void clear(@Context CommandSender sender, @Arg Crate crate) {
    crate.getItemsBySlot().clear();
    this.flameConfigService.saveLocally(CratesConfig.class);
    BukkitMessage.from("&aPomyślnie wyczyszczono itemy z skrzyni o nazwie &2" + crate.getId())
        .send(sender);
  }

}
