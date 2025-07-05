package io.github.flamehub.warehouse.user;

import io.github.flamehub.commons.bukkit.dispatcher.FlameDispatcher;
import io.github.flamehub.commons.bukkit.user.UserDatabaseListener;
import io.github.flamehub.commons.user.UserDatabaseCache;
import io.github.flamehub.commons.user.UserFactory;
import io.github.flamehub.commons.user.UserRepository;
import org.bukkit.plugin.PluginManager;

public final class WarehouseUserListener extends UserDatabaseListener<WarehouseUser> {

  public WarehouseUserListener(
      final FlameDispatcher flameDispatcher,
      final PluginManager pluginManager,
      final UserDatabaseCache<WarehouseUser> userDatabaseCache,
      final UserRepository<WarehouseUser> userRepository,
      final UserFactory<WarehouseUser> userFactory) {
    super(flameDispatcher, pluginManager, userDatabaseCache, userRepository, userFactory);
  }
}
