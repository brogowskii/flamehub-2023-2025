package io.github.flamehub.lobby.daily;

import static io.github.flamehub.lobby.daily.DailyGui.TITLE_GRADIENT;

import io.github.flamehub.commons.bukkit.dispatcher.FlameDispatcher;
import io.github.flamehub.commons.bukkit.util.FlameItemBuilder;
import io.github.flamehub.commons.bukkit.util.SkullBuilder;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;

public final class DailyListener implements Listener {

  private final FlameDispatcher flameDispatcher;
  private final DailyUserCache dailyUserCache;
  private final DailyUserRepository dailyUserRepository;

  public DailyListener(final FlameDispatcher flameDispatcher, final DailyUserCache dailyUserCache,
      final DailyUserRepository dailyUserRepository) {
    this.flameDispatcher = flameDispatcher;
    this.dailyUserCache = dailyUserCache;
    this.dailyUserRepository = dailyUserRepository;
  }

  @EventHandler
  public void onJoin(final PlayerJoinEvent event) {
    final ItemStack itemStack = FlameItemBuilder.of(
            SkullBuilder.create("5cd5c9b41afe4ddfa06001f78c781d1a39d8e1ba9d84bb14a080a7a219efde3"))
        .name(TITLE_GRADIENT)
        .asItemStack();

    event.getPlayer().getInventory().setItem(4, itemStack);

  }

  @EventHandler
  public void onInteract(final PlayerInteractEvent event) {
    final Action action = event.getAction();
    if (action == Action.RIGHT_CLICK_AIR || action == Action.RIGHT_CLICK_BLOCK) {
      final Player player = event.getPlayer();
      if (player.getInventory().getItemInMainHand().getType() == Material.PLAYER_HEAD) {
        new DailyGui(flameDispatcher, dailyUserCache, dailyUserRepository).open(
            player);
      }
    }
  }

}
