package io.github.flamehub.tiktok.user;

import io.github.flamehub.commons.bukkit.dispatcher.FlameDispatcher;
import io.github.flamehub.commons.bukkit.user.UserDatabaseListener;
import io.github.flamehub.commons.user.UserDatabaseCache;
import io.github.flamehub.commons.user.UserDatabaseRepository;
import io.github.flamehub.commons.user.UserFactory;
import org.bukkit.plugin.PluginManager;

final class TikTokUserListener extends UserDatabaseListener<TikTokUser> {

  public TikTokUserListener(
      final FlameDispatcher flameDispatcher,
      final PluginManager pluginManager,
      final UserDatabaseCache<TikTokUser> userDatabaseCache,
      final UserDatabaseRepository<TikTokUser> userDatabaseRepository,
      final UserFactory<TikTokUser> userFactory) {
    super(flameDispatcher, pluginManager, userDatabaseCache, userDatabaseRepository, userFactory);
  }
}
