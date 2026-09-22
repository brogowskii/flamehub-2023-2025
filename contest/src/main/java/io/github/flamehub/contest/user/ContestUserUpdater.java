package io.github.flamehub.contest.user;

import io.github.flamehub.commons.bukkit.dispatcher.FlameDispatcher;
import io.github.flamehub.commons.messenger.RedisMessenger;
import io.github.flamehub.commons.network.player.NetworkPlayer;
import io.github.flamehub.commons.network.player.NetworkPlayerCache;
import io.github.flamehub.commons.server.NetworkServer;
import io.github.flamehub.commons.server.NetworkServerFacade;

final class ContestUserUpdater {

  private final FlameDispatcher flameDispatcher;
  private final NetworkServerFacade networkServerFacade;
  private final NetworkPlayerCache networkPlayerCache;
  private final ContestUserRepository contestUserRepository;
  private final RedisMessenger redisMessenger;

  ContestUserUpdater(
      final FlameDispatcher flameDispatcher,
      final NetworkServerFacade networkServerFacade,
      final NetworkPlayerCache networkPlayerCache,
      final ContestUserRepository contestUserRepository,
      final RedisMessenger redisMessenger
  ) {
    this.flameDispatcher = flameDispatcher;
    this.networkServerFacade = networkServerFacade;
    this.networkPlayerCache = networkPlayerCache;
    this.contestUserRepository = contestUserRepository;
    this.redisMessenger = redisMessenger;
  }

  void update(final ContestUser contestUser, final double money, final ContestUserUpdateType type) {
    final NetworkPlayer networkPlayer = networkPlayerCache.findByName(contestUser.getName());
    final NetworkServer current = networkServerFacade.getCurrent();

    // Jeżeli nie ma go na żadnym serwerze lub jest, ale nie na tym w tej kategorii to zapisujemy prosto do db
    if (networkPlayer == null || !current.getCategory().equals(networkPlayer.getServerCategory())) {
      flameDispatcher.dispatchAsync(() -> contestUserRepository.save(contestUser));
      return;
    }

    // Jeżeli jest, ale po prostu na innym kanale to pakiecik wysyłamy
    if (!networkPlayer.getServer().equals(current.getName())) {
      final ContestUserUpdate message = new ContestUserUpdate(networkPlayer.getUniqueId(), money, type);
      flameDispatcher.dispatchAsync(
          () -> redisMessenger.publish(networkPlayer.getServer(), message));
      return;
    }

    // Jeżeli jest na tym samym serwerze, to zaznaczamy, że trzeba zaktualizować
    contestUser.setNeedUpdate(true);

  }

}
