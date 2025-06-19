package io.github.flamehub.coinflip.user;

import io.github.flamehub.commons.bukkit.dispatcher.FlameDispatcher;
import io.github.flamehub.commons.bukkit.user.UserRedisListener;
import io.github.flamehub.commons.user.UserFactory;
import io.github.flamehub.commons.user.UserRedisCache;
import io.github.flamehub.commons.user.UserRepository;
import org.bukkit.plugin.PluginManager;

public final class CoinFlipUserListener extends UserRedisListener<CoinFlipUser> {

  public CoinFlipUserListener(
      final FlameDispatcher flameDispatcher,
      final PluginManager pluginManager,
      final UserRedisCache<CoinFlipUser> userRedisCache,
      final UserRepository<CoinFlipUser> userRepository,
      final UserFactory<CoinFlipUser> userFactory) {
    super(flameDispatcher, pluginManager, userRedisCache, userRepository, userFactory);
  }
}
