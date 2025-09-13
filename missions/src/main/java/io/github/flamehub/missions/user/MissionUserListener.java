package io.github.flamehub.missions.user;

import io.github.flamehub.commons.bukkit.dispatcher.FlameDispatcher;
import io.github.flamehub.commons.bukkit.user.UserDatabaseListener;
import io.github.flamehub.commons.user.UserDatabaseCache;
import io.github.flamehub.commons.user.UserFactory;
import io.github.flamehub.commons.user.UserRepository;
import org.bukkit.plugin.PluginManager;

public final class MissionUserListener extends UserDatabaseListener<MissionUser> {

  public MissionUserListener(final FlameDispatcher flameDispatcher,
      final PluginManager pluginManager,
      final UserDatabaseCache<MissionUser> userDatabaseCache,
      final UserRepository<MissionUser> userRepository,
      final UserFactory<MissionUser> userFactory) {
    super(flameDispatcher, pluginManager, userDatabaseCache, userRepository, userFactory);
  }
}
