package io.github.flamehub.commons.bukkit.user;

import io.github.flamehub.commons.bukkit.dispatcher.FlameDispatcher;
import io.github.flamehub.commons.bukkit.user.event.AsyncPlayerJoinEvent;
import io.github.flamehub.commons.bukkit.user.event.PlayerChangeNameEvent;
import io.github.flamehub.commons.bukkit.user.event.UserQuitEvent;
import io.github.flamehub.commons.user.User;
import io.github.flamehub.commons.user.UserDatabaseCache;
import io.github.flamehub.commons.user.UserRepository;
import io.github.flamehub.commons.user.UserFactory;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.PluginManager;

public class UserDatabaseListener<U extends User> implements Listener {

  private final FlameDispatcher flameDispatcher;
  private final PluginManager pluginManager;
  private final UserDatabaseCache<U> userDatabaseCache;
  private final UserRepository<U> userRepository;
  private final UserFactory<U> userFactory;

  public UserDatabaseListener(
      final FlameDispatcher flameDispatcher,
      final PluginManager pluginManager,
      final UserDatabaseCache<U> userDatabaseCache,
      final UserRepository<U> userRepository,
      final UserFactory<U> userFactory
  ) {
    this.flameDispatcher = flameDispatcher;
    this.pluginManager = pluginManager;
    this.userDatabaseCache = userDatabaseCache;
    this.userRepository = userRepository;
    this.userFactory = userFactory;
  }


  @EventHandler(priority = EventPriority.LOWEST)
  public void onJoin(final PlayerJoinEvent event) {
    final Player source = event.getPlayer();
    flameDispatcher.dispatchAsyncLater(() -> {

      boolean firstJoin = false;
      U user = userDatabaseCache.findByUniqueId(source.getUniqueId());
      if (user == null) {
        user = userFactory.create(source.getUniqueId(), source.getName());
        userRepository.save(user);
        firstJoin = true;

      }

      userDatabaseCache.add(user);
      if (!user.getName().equalsIgnoreCase(source.getName())) {
        userDatabaseCache.updateName(user, source.getName());
        userRepository.save(user);
        pluginManager.callEvent(
            new PlayerChangeNameEvent(user, user.getName(), source.getName()));
      }

      final AsyncPlayerJoinEvent bukkitPlayerJoinEvent = new AsyncPlayerJoinEvent(source, user,
          firstJoin);
      pluginManager.callEvent(bukkitPlayerJoinEvent);
    }, 10L);

  }

  @EventHandler(priority = EventPriority.HIGHEST)
  public void onQuit(final PlayerQuitEvent event) {
    final Player source = event.getPlayer();
    final U user = userDatabaseCache.findByUniqueId(source.getUniqueId());
    if (user == null) {
      return;
    }

    UserQuitEvent userQuitEvent = new UserQuitEvent(source, user);
    pluginManager.callEvent(userQuitEvent);
    userDatabaseCache.remove(user);
    flameDispatcher.dispatchAsync(() -> userRepository.save(user));

  }
}
