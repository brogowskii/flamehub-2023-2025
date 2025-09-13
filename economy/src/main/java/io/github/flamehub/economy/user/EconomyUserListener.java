package io.github.flamehub.economy.user;

import io.github.flamehub.commons.bukkit.dispatcher.FlameDispatcher;
import io.github.flamehub.commons.bukkit.user.UserDatabaseListener;
import io.github.flamehub.commons.user.UserDatabaseCache;
import io.github.flamehub.commons.user.UserFactory;
import io.github.flamehub.commons.user.UserRepository;
import org.bukkit.plugin.PluginManager;

final class EconomyUserListener extends UserDatabaseListener<EconomyUser> {

  EconomyUserListener(
      final FlameDispatcher flameDispatcher,
      final PluginManager pluginManager,
      final UserDatabaseCache<EconomyUser> userDatabaseCache,
      final UserRepository<EconomyUser> userRepository,
      final UserFactory<EconomyUser> userFactory
  ) {
    super(flameDispatcher, pluginManager, userDatabaseCache, userRepository, userFactory);
  }
}
