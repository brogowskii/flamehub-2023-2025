package io.github.flamehub.tiktok.user;

import io.github.flamehub.commons.bukkit.dispatcher.FlameDispatcher;
import io.github.flamehub.commons.bukkit.user.UserDatabaseListener;
import io.github.flamehub.commons.user.UserDatabaseCache;
import io.github.flamehub.commons.user.UserRepository;
import io.github.flamehub.commons.user.UserFactory;
import org.bukkit.plugin.PluginManager;

public final class TikTokUserListener extends UserDatabaseListener<TikTokUser> {

  public TikTokUserListener(
      final FlameDispatcher flameDispatcher,
      final PluginManager pluginManager,
      final UserDatabaseCache<TikTokUser> userDatabaseCache,
      final UserRepository<TikTokUser> userRepository,
      final UserFactory<TikTokUser> userFactory) {
    super(flameDispatcher, pluginManager, userDatabaseCache, userRepository, userFactory);
  }


}
