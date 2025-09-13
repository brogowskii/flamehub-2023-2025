package io.github.flamehub.tiktok.user;

import io.github.flamehub.commons.bukkit.dispatcher.FlameDispatcher;
import io.github.flamehub.commons.bukkit.user.UserRedisListener;
import io.github.flamehub.commons.user.UserFactory;
import io.github.flamehub.commons.user.UserRedisCache;
import io.github.flamehub.commons.user.UserRepository;
import org.bukkit.plugin.PluginManager;

public final class TikTokUserListener extends UserRedisListener<TikTokUser> {


  public TikTokUserListener(final FlameDispatcher flameDispatcher,
      final PluginManager pluginManager,
      final UserRedisCache<TikTokUser> userRedisCache,
      final UserRepository<TikTokUser> userRepository,
      final UserFactory<TikTokUser> userFactory) {
    super(flameDispatcher, pluginManager, userRedisCache, userRepository, userFactory);
  }
}
