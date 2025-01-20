package io.github.flamehub.commons.bukkit.user;

import io.github.flamehub.commons.user.User;
import io.github.flamehub.commons.user.UserLocallyCache;
import io.github.flamehub.commons.user.UserFactory;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class UserListener<U extends User> implements Listener {

  private final UserLocallyCache<U> userLocallyCache;
  private final UserFactory<U> userFactory;

  public UserListener(UserLocallyCache<U> userLocallyCache, UserFactory<U> userFactory) {
    this.userLocallyCache = userLocallyCache;
    this.userFactory = userFactory;
  }

  @EventHandler(priority = EventPriority.LOWEST)
  public void onJoin(PlayerJoinEvent event) {
    Player source = event.getPlayer();

    U user = userLocallyCache.findByUniqueId(source.getUniqueId());
    if (user == null) {
      user = userFactory.create(source.getUniqueId(), source.getName());
      userLocallyCache.add(user);
    }

  }
}
