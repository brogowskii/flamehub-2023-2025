package io.github.flamehub.wallet.user;

import io.github.flamehub.commons.bukkit.dispatcher.FlameDispatcher;
import io.github.flamehub.commons.bukkit.user.UserRedisListener;
import io.github.flamehub.commons.user.UserFactory;
import io.github.flamehub.commons.user.UserRedisCache;
import io.github.flamehub.commons.user.UserRepository;
import org.bukkit.plugin.PluginManager;

final class WalletUserListener extends UserRedisListener<WalletUser> {

  public WalletUserListener(
      final FlameDispatcher flameDispatcher,
      final PluginManager pluginManager,
      final UserRedisCache<WalletUser> userRedisCache,
      final UserRepository<WalletUser> userRepository,
      final UserFactory<WalletUser> userFactory) {
    super(flameDispatcher, pluginManager, userRedisCache, userRepository, userFactory);
  }
}
