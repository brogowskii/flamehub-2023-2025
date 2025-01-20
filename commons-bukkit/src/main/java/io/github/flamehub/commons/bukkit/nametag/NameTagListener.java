package io.github.flamehub.commons.bukkit.nametag;

import io.github.flamehub.commons.bukkit.CommonsPlugin;
import io.github.flamehub.commons.bukkit.dispatcher.FlameDispatcher;
import java.util.UUID;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.event.EventBus;
import net.luckperms.api.event.user.UserDataRecalculateEvent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public final class NameTagListener implements Listener {

  private final static LuckPerms LUCK_PERMS = LuckPermsProvider.get();
  private final NameTagService nameTagService;
  private final FlameDispatcher flameDispatcher;

  public NameTagListener(NameTagService nameTagService, FlameDispatcher flameDispatcher) {
    this.nameTagService = nameTagService;
    this.flameDispatcher = flameDispatcher;

    if (Bukkit.getPluginManager().getPlugin("LuckPerms") != null) {
      EventBus eventBus = LUCK_PERMS.getEventBus();
      eventBus.subscribe(CommonsPlugin.getInstance(), UserDataRecalculateEvent.class,
          this::recalculate);
    }
  }

  @EventHandler
  public void onJoin(PlayerJoinEvent event) {

    flameDispatcher.dispatchAsync(() -> {
      nameTagService.create(event.getPlayer());
      nameTagService.update(event.getPlayer());
    });
  }

  @EventHandler(priority = EventPriority.LOWEST)
  public void onQuit(PlayerQuitEvent event) {

    flameDispatcher.dispatchAsync(() -> {
      nameTagService.remove(event.getPlayer());
      CommonsPlugin.getInstance().getServer().getPluginManager()
          .callEvent(new NameTagRemoveEvent(event.getPlayer()));
    });
  }


  private void recalculate(UserDataRecalculateEvent event) {
    UUID uniqueId = event.getUser().getUniqueId();

    Player player = Bukkit.getPlayer(uniqueId);
    if (player == null) {
      return;
    }

    flameDispatcher.dispatchAsync(() -> {
      nameTagService.create(player);
      nameTagService.update(player);
    });


  }

}
