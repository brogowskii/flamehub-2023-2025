package io.github.flamehub.commons.bukkit.user;

import io.github.flamehub.commons.bukkit.dispatcher.FlameDispatcher;
import io.github.flamehub.commons.bukkit.user.event.AsyncPlayerJoinEvent;
import io.github.flamehub.commons.bukkit.user.event.PlayerChangeNameEvent;
import io.github.flamehub.commons.user.User;
import io.github.flamehub.commons.user.UserFactory;
import io.github.flamehub.commons.user.UserRedisCache;
import io.github.flamehub.commons.user.UserRepository;
import java.util.concurrent.CompletableFuture;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.PluginManager;

public class UserRedisListener<U extends User> implements Listener {

  private final FlameDispatcher flameDispatcher;
  private final PluginManager pluginManager;
  private final UserRedisCache<U> userRedisCache;
  private final UserRepository<U> userRepository;
  private final UserFactory<U> userFactory;

  public UserRedisListener(final FlameDispatcher flameDispatcher, final PluginManager pluginManager,
      final UserRedisCache<U> userRedisCache, final UserRepository<U> userRepository,
      final UserFactory<U> userFactory) {
    this.flameDispatcher = flameDispatcher;
    this.pluginManager = pluginManager;
    this.userRedisCache = userRedisCache;
    this.userRepository = userRepository;
    this.userFactory = userFactory;
  }


  @EventHandler(priority = EventPriority.LOWEST)
  public void onJoin(final PlayerJoinEvent event) {
    final Player source = event.getPlayer();

    CompletableFuture.supplyAsync(() -> userRedisCache.findByUniqueId(source.getUniqueId()))
        .thenAccept(user -> {
          boolean firstJoin = false;
          if (user == null) {
            user = userFactory.create(source.getUniqueId(), source.getName());
            userRedisCache.add(user);
            userRepository.save(user);
            firstJoin = true;
          }

          if (!user.getName().equalsIgnoreCase(source.getName())) {
            userRedisCache.updateName(user, source.getName());
            userRepository.save(user);
            userRedisCache.add(user);
            pluginManager.callEvent(
                new PlayerChangeNameEvent(user, user.getName(), source.getName()));
          }

          final AsyncPlayerJoinEvent bukkitPlayerJoinEvent = new AsyncPlayerJoinEvent(source, user,
              firstJoin);
          pluginManager.callEvent(bukkitPlayerJoinEvent);
        });


  }

}
