package io.github.flamehub.commons.bukkit.user.impl;

import io.github.flamehub.commons.bukkit.dispatcher.FlameDispatcher;
import io.github.flamehub.commons.bukkit.user.UserDatabaseListener;
import io.github.flamehub.commons.user.UserDatabaseCache;
import io.github.flamehub.commons.user.UserFactory;
import io.github.flamehub.commons.user.UserRepository;
import org.bukkit.plugin.PluginManager;

public final class CommonUserListener extends UserDatabaseListener<CommonUser> {

  public CommonUserListener(
      final FlameDispatcher flameDispatcher,
      final PluginManager pluginManager,
      final UserDatabaseCache<CommonUser> userDatabaseCache,
      final UserRepository<CommonUser> userRepository,
      final UserFactory<CommonUser> userFactory) {
    super(flameDispatcher, pluginManager, userDatabaseCache, userRepository, userFactory);
  }
}
