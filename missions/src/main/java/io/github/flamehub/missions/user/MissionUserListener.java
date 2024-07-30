package io.github.flamehub.missions.user;

import io.github.flamehub.commons.bukkit.dispatcher.FlameDispatcher;
import io.github.flamehub.commons.bukkit.user.UserDatabaseListener;
import io.github.flamehub.commons.user.UserDatabaseCache;
import io.github.flamehub.commons.user.UserDatabaseRepository;
import io.github.flamehub.commons.user.UserFactory;
import org.bukkit.plugin.PluginManager;

public final class MissionUserListener extends UserDatabaseListener<MissionUser> {

  public MissionUserListener(FlameDispatcher flameDispatcher, PluginManager pluginManager,
      UserDatabaseCache<MissionUser> userDatabaseCache,
      UserDatabaseRepository<MissionUser> userDatabaseRepository,
      UserFactory<MissionUser> userFactory) {
    super(flameDispatcher, pluginManager, userDatabaseCache, userDatabaseRepository, userFactory);
  }
}
