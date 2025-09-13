package io.github.flamehub.crates;

import dev.rollczi.litecommands.annotations.argument.Arg;
import dev.rollczi.litecommands.annotations.command.Command;
import dev.rollczi.litecommands.annotations.context.Context;
import dev.rollczi.litecommands.annotations.execute.Execute;
import dev.rollczi.litecommands.annotations.join.Join;
import dev.rollczi.litecommands.annotations.optional.OptionalArg;
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

@Command(name = "crate")
@Permission("server.commands.crate")
public final class CrateCommand extends FlameConfigRefresher {

  private final FlameConfigService flameConfigService;
  private final CratesConfig cratesConfig;

  public CrateCommand(final FlameConfigService flameConfigService, final CratesConfig cratesConfig) {
    super(flameConfigService, CratesConfig.class);
    this.flameConfigService = flameConfigService;
    this.cratesConfig = cratesConfig;
  }

  @Execute(name = "reload")
  void reload(@Context final CommandSender sender) {
    refresh(sender);

  }

  @Execute(name = "update")
  void update(@Context final CommandSender sender) {
    refreshAndBroadcast(sender);
  }

  @Execute(name = "setitem")
  void setItem(@Context final Player player, @Arg final Crate crate, @Arg final int slot, @Arg final double chance) {
    final ItemStack itemInMainHand = player.getInventory().getItemInMainHand();
    if (itemInMainHand.getType().isAir()) {
      return;
    }

    final CrateItem item = new CrateItem(itemInMainHand.getType().toString(), itemInMainHand.clone(),
        chance, 5);
    crate.getItemsBySlot().put(slot, item);
    flameConfigService.save(CratesConfig.class);
    player.sendMessage(TextUtil.parse(
        "&aPomyślnie ustawiono item do skrzynki na slot " + slot + " z szansą " + chance + "%."));

  }

  @Execute(name = "deleteitem")
  void deleteItem(@Context final Player player, @Arg final Crate crate, @Arg final int slot) {
    crate.getItemsBySlot().remove(slot);
    flameConfigService.save(CratesConfig.class);
    player.sendMessage(TextUtil.parse("&aPomyślnie usunięto item z slotu " + slot));
  }

  @Execute(name = "create")
  void create(@Context final Player player, @Arg final String crateId) {
    if (cratesConfig.findById(crateId) != null) {
      TextBuilder.builder()
          .text("&cSkrzynka o podanej nazwie już istnieje.")
          .send(player);
      return;
    }
    cratesConfig.add(new Crate(crateId));
    flameConfigService.save(CratesConfig.class);
    BukkitMessage.from("&aPomyślnie stworzyłeś skrzynie o nazwie &2" + crateId)
        .deliver(player);
  }

  @Execute(name = "delete")
  void delete(@Context final Player player, @Arg final Crate crate) {
    cratesConfig.remove(crate);
    flameConfigService.save(CratesConfig.class);
    BukkitMessage.from("&aPomyślnie usunięto skrzynie o nazwie &2" + crate.getId())
        .deliver(player);
  }


  @Execute(name = "setkey")
  void key(@Context final Player player, @Arg final Crate crate) {
    crate.setKey(player.getInventory().getItemInMainHand().clone());
    flameConfigService.save(CratesConfig.class);
    BukkitMessage.from("&aPomyślnie ustawiono klucz dla skrzyni o nazwie &2" + crate.getId())
        .deliver(player);
  }

  @Execute(name = "guiname")
  void guiName(@Context final Player player, @Arg final Crate crate, @Join final String guiName) {
    crate.setGuiName(guiName);
    flameConfigService.save(CratesConfig.class);
    BukkitMessage.from(
            "&aPomyślnie ustawiono nową nazwe gui dla skrzyni o nazwie &2" + crate.getId())
        .deliver(player);
  }

  @Execute(name = "addlocation")
  void set(@Context final Player player, @Arg final Crate crate) {
    final Block block = player.getTargetBlock(5);
    if (block == null) {
      player.sendMessage("block is null");
      return;
    }

    if (block.getType() == Material.AIR) {
      player.sendMessage("block is air");
      return;
    }

    crate.getLocation().add(block.getLocation());
    flameConfigService.save(CratesConfig.class);
    BukkitMessage.from("&aPomyślnie ustawiono nowa lokalizacje skrzyni o nazwie &2" + crate.getId())
        .deliver(player);
  }

  @Execute(name = "removelocation")
  void removeLocation(@Context final Player player, @Arg final Crate crate) {
    final Block block = player.getTargetBlock(5);
    if (block == null) {
      player.sendMessage("block is null");
      return;
    }

    if (block.getType() == Material.AIR) {
      player.sendMessage("block is air");
      return;
    }

    crate.getLocation().remove(block.getLocation());
    flameConfigService.save(CratesConfig.class);
    BukkitMessage.from("&aPomyślnie usunięto lokalizacje skrzyni o nazwie &2" + crate.getId())
        .deliver(player);
  }

  @Execute(name = "givekey")
  void giveKey(@Context final CommandSender sender, @Arg final Crate crate, @Arg final Player target,
      @OptionalArg final Integer optAmount) {
    final ItemStack key = crate.getKey();
    if (key == null) {
      BukkitMessage.from("&cTa skrzynia nie posiada ustawionego klucza.").deliver(sender);
      return;
    }

    int amount = 1;
    if (optAmount != null) {
      amount = optAmount;
    }

    final ItemStack clone = key.clone();
    clone.setAmount(amount);
    InventoryUtil.addItem(target, clone);
  }

  @Execute(name = "keyall")
  void keyAll(@Context final CommandSender player, @Arg final Crate crate, @Arg final int amount) {

    final ItemStack key = crate.getKey();
    if (key == null) {
      BukkitMessage.from("&cTa skrzynia nie posiada ustawionego klucza.").deliver(player);
      return;
    }

    final ItemStack clone = key.clone();
    clone.setAmount(amount);
    for (final Player it : Bukkit.getOnlinePlayers()) {

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
  void switchStatus(@Context final CommandSender commandSender, @Arg final Crate crate, @Join final String date) {

    final Instant instant;
    try {
      instant = TimeUtil.dateFromString(date).toInstant();
    } catch (final ParseException e) {
      BukkitMessage.from("&cPodana data jest nieprawidłowa. Format: &6HH:mm:ss dd.MM.yyyy")
          .deliver(commandSender);
      return;
    }

    crate.setEnabledFrom(instant);
    BukkitMessage.from("&7Pomyslnie ustawiłeś odpalenie tej skrzyni za: &6" + TimeUtil.formatTime(
            Duration.between(Instant.now(), instant)))
        .deliver(commandSender);
    flameConfigService.save(CratesConfig.class);
  }

  @Execute(name = "clear")
  void clear(@Context final CommandSender sender, @Arg final Crate crate) {
    crate.getItemsBySlot().clear();
    flameConfigService.save(CratesConfig.class);
    BukkitMessage.from("&aPomyślnie wyczyszczono itemy z skrzyni o nazwie &2" + crate.getId())
        .deliver(sender);
  }

  @Execute(name = "setrotationtime")
  void setRotationTime(@Context final Player player, @Arg final Crate crate, @Join final String rotationTime) {
    try {
      // Sprawdź czy format czasu jest poprawny
      TimeUtil.parseTime(rotationTime);

      crate.setRotationTime(rotationTime);
      flameConfigService.save(CratesConfig.class);

      BukkitMessage.from("&aPomyślnie ustawiono czas rotacji na &6" + rotationTime + " &adla skrzyni &2" + crate.getId())
          .deliver(player);
    } catch (Exception e) {
      BukkitMessage.from("&cNieprawidłowy format czasu! Użyj np: 1h, 30m, 1d")
          .deliver(player);
    }
  }

  @Execute(name = "setrotationitems")
  void setRotationItems(@Context final Player player, @Arg final Crate crate, @Arg final int itemCount) {
    if (itemCount <= 0) {
      BukkitMessage.from("&cLiczba itemów w rotacji musi być większa od 0!")
          .deliver(player);
      return;
    }

    crate.setRotationItems(itemCount);
    flameConfigService.save(CratesConfig.class);

    BukkitMessage.from("&aPomyślnie ustawiono liczbę itemów w rotacji na &6" + itemCount + " &adla skrzyni &2" + crate.getId())
        .deliver(player);
  }

  @Execute(name = "removerotation")
  void removeRotation(@Context final Player player, @Arg final Crate crate) {
    crate.setRotationTime(null);
    crate.getCurrentRotationSlots().clear();
    flameConfigService.save(CratesConfig.class);

    BukkitMessage.from("&aPomyślnie usunięto rotację ze skrzyni &2" + crate.getId())
        .deliver(player);
  }

  @Execute(name = "forcerotation")
  void forceRotation(@Context final Player player, @Arg final Crate crate) {
    if (!crate.hasRotation()) {
      BukkitMessage.from("&cSkrzynia &6" + crate.getId() + " &cnie ma ustawionej rotacji!")
          .deliver(player);
      return;
    }

    crate.getCurrentRotationSlots().clear();
    crate.performRotation();
    flameConfigService.save(CratesConfig.class);

    BukkitMessage.from("&aPomyślnie wykonano rotację dla skrzyni &2" + crate.getId())
        .deliver(player);
  }

  @Execute(name = "rotationinfo")
  void rotationInfo(@Context final Player player, @Arg final Crate crate) {
    if (!crate.hasRotation()) {
      BukkitMessage.from("&cSkrzynia &6" + crate.getId() + " &cnie ma ustawionej rotacji!")
          .deliver(player);
      return;
    }

    final Duration timeUntilNext = crate.getTimeUntilNextRotation();
    final String timeFormatted = timeUntilNext.isNegative() ? "&cTrzeba wykonać rotację!" : TimeUtil.formatTime(timeUntilNext);

    BukkitMessage.from("&6&lInformacje o rotacji skrzyni &2" + crate.getId() + "&6:")
        .deliver(player);
    BukkitMessage.from("&7• Czas rotacji: &f" + crate.getRotationTime())
        .deliver(player);
    BukkitMessage.from("&7• Liczba itemów w rotacji: &f" + crate.getRotationItems())
        .deliver(player);
    BukkitMessage.from("&7• Do następnej rotacji: &f" + timeFormatted)
        .deliver(player);
    BukkitMessage.from("&7• Aktualne sloty: &f" + crate.getCurrentRotationSlots().toString())
        .deliver(player);
  }

}
