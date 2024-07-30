package io.github.flamehub.economy.user;

import io.github.flamehub.commons.bukkit.dispatcher.FlameDispatcher;
import io.github.flamehub.commons.messenger.RedisMessenger;
import io.github.flamehub.commons.network.player.NetworkPlayer;
import io.github.flamehub.commons.network.player.NetworkPlayerCache;
import io.github.flamehub.commons.server.NetworkServer;
import io.github.flamehub.commons.server.NetworkServerCache;

final class EconomyUserUpdater {

  private final FlameDispatcher flameDispatcher;
  private final NetworkServerCache networkServerCache;
  private final NetworkPlayerCache networkPlayerCache;
  private final EconomyUserRepository economyUserRepository;
  private final RedisMessenger redisMessenger;

  EconomyUserUpdater(
      final FlameDispatcher flameDispatcher,
      final NetworkServerCache networkServerCache,
      final NetworkPlayerCache networkPlayerCache,
      final EconomyUserRepository economyUserRepository,
      final RedisMessenger redisMessenger
  ) {
    this.flameDispatcher = flameDispatcher;
    this.networkServerCache = networkServerCache;
    this.networkPlayerCache = networkPlayerCache;
    this.economyUserRepository = economyUserRepository;
    this.redisMessenger = redisMessenger;
  }

  void update(final EconomyUser economyUser, final double money, final EconomyUserUpdateType type) {
    final NetworkPlayer networkPlayer = this.networkPlayerCache.findByName(economyUser.getName());
    final NetworkServer current = this.networkServerCache.getCurrent();

    // Jeżeli nie ma go na żadnym serwerze lub jest, ale nie na tym w tej kategorii to zapisujemy prosto do db
    if (networkPlayer == null || !current.getCategory().equals(networkPlayer.getServerCategory())) {
      this.flameDispatcher.dispatchAsync(() -> this.economyUserRepository.save(economyUser));
      return;
    }

    // Jeżeli jest, ale po prostu na innym kanale to pakiecik wysyłamy
    if (!networkPlayer.getServer().equals(current.getName())) {
      EconomyUserUpdate message = new EconomyUserUpdate(networkPlayer.getUniqueId(), money, type);
      this.flameDispatcher.dispatchAsync(
          () -> this.redisMessenger.publish(networkPlayer.getServer(), message));
      return;
    }

    // Jeżeli jest na tym samym serwerze, to zaznaczamy, że trzeba zaktualizować
    economyUser.setNeedUpdate(true);

  }

}
