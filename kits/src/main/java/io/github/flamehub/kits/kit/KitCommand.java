package io.github.flamehub.kits.kit;

import dev.rollczi.litecommands.annotations.argument.Arg;
import dev.rollczi.litecommands.annotations.command.Command;
import dev.rollczi.litecommands.annotations.context.Context;
import dev.rollczi.litecommands.annotations.execute.Execute;
import io.github.flamehub.commons.bukkit.dispatcher.FlameDispatcher;
import io.github.flamehub.commons.bukkit.message.BukkitMessage;
import io.github.flamehub.commons.bukkit.util.InventoryUtil;
import io.github.flamehub.commons.util.TimeUtil;
import io.github.flamehub.kits.KitsConfig;
import io.github.flamehub.kits.user.KitUser;
import io.github.flamehub.kits.user.KitUserCache;
import io.github.flamehub.kits.user.KitUserRepository;
import java.time.Duration;
import java.time.Instant;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

@Command(name = "kit", aliases = {"kits", "kity", "zestawy", "zestaw"})
public final class KitCommand {

  private final FlameDispatcher flameDispatcher;
  private final KitsConfig kitConfig;
  private final KitUserCache kitUserCache;
  private final KitUserRepository kitUserRepository;

  public KitCommand(FlameDispatcher flameDispatcher, KitsConfig kitConfig,
      KitUserCache kitUserCache, KitUserRepository kitUserRepository) {
    this.flameDispatcher = flameDispatcher;
    this.kitConfig = kitConfig;
    this.kitUserCache = kitUserCache;
    this.kitUserRepository = kitUserRepository;
  }

  @Execute
  void execute(@Context Player player) {
    KitGui kitGui = new KitGui(this.flameDispatcher, this.kitConfig, this.kitUserCache,
        this.kitUserRepository);
    kitGui.open(player);
  }

  @Execute
  void claim(@Context Player player, @Arg Kit kit) {
    if (!kit.isEnable()) {
      BukkitMessage.from("&cTen zestaw został chwilowo wyłączony!").send(player);
      player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 1f, 1f);
      return;
    }

    if (!player.hasPermission(kit.getPermission())) {
      BukkitMessage.from("&cNie posiadasz uprawnień do odebrania tego zestawu!").send(player);
      player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 1f, 1f);
      return;
    }

    KitUser kitUser = kitUserCache.findByUniqueId(player.getUniqueId());
    Instant kitCooldown = kitUser.getKitCooldown(kit.getName());
    if (kitCooldown.isAfter(Instant.now())) {
      BukkitMessage.from("&cTen zestaw będziesz mógł odebrać dopiero za: &4{time}")
          .with("time", TimeUtil.formatTime(Duration.between(Instant.now(), kitCooldown)))
          .send(player);
      player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 1f, 1f);
      return;
    }

    kitUser.addCooldown(kit.getName(), Instant.now().plus(kit.getCooldownDuration()));
    for (ItemStack item : kit.getItems()) {
      InventoryUtil.addItem(player, item.clone());
    }

    player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 3f, 1f);
    this.flameDispatcher.dispatchAsync(() -> this.kitUserRepository.save(kitUser));
  }

}
