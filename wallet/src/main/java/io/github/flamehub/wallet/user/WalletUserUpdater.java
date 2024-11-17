package io.github.flamehub.wallet.user;

import io.github.flamehub.commons.bukkit.dispatcher.FlameDispatcher;
import io.github.flamehub.commons.messenger.RedisMessenger;
import io.github.flamehub.commons.network.player.NetworkPlayer;
import io.github.flamehub.commons.network.player.NetworkPlayerCache;
import io.github.flamehub.commons.server.NetworkServer;
import io.github.flamehub.commons.server.NetworkServerCache;
import io.github.flamehub.wallet.api.WalletUser;
import io.github.flamehub.wallet.api.WalletUserMoneyChangeType;
import io.github.flamehub.wallet.api.WalletUserRepository;

public final class WalletUserUpdater {

  private final FlameDispatcher flameDispatcher;
  private final NetworkPlayerCache networkPlayerCache;
  private final NetworkServerCache networkServerCache;
  private final WalletUserRepository walletUserRepository;
  private final RedisMessenger redisMessenger;

  public WalletUserUpdater(
      final FlameDispatcher flameDispatcher,
      final NetworkPlayerCache networkPlayerCache,
      final NetworkServerCache networkServerCache,
      final WalletUserRepository walletUserRepository,
      final RedisMessenger redisMessenger
  ) {
    this.flameDispatcher = flameDispatcher;
    this.networkPlayerCache = networkPlayerCache;
    this.networkServerCache = networkServerCache;
    this.walletUserRepository = walletUserRepository;
    this.redisMessenger = redisMessenger;
  }

  public void update(final WalletUser walletUser, final double money,
      final WalletUserMoneyChangeType type) {
    final NetworkPlayer networkPlayer = this.networkPlayerCache.findByName(walletUser.getName());
    final NetworkServer current = this.networkServerCache.getCurrent();

    if (networkPlayer == null || networkPlayer.getServer().equals("queue")
        || networkPlayer.getServer().equals("auth")) {
      this.flameDispatcher.dispatchAsync(() -> this.walletUserRepository.save(walletUser));
      return;
    }

    // Jeżeli jest, ale po prostu na innym kanale to pakiecik wysyłamy
    if (!networkPlayer.getServer().equals(current.getName())) {
      WalletUserUpdate message = new WalletUserUpdate(networkPlayer.getUniqueId(), type, money);
      this.flameDispatcher.dispatchAsync(
          () -> this.redisMessenger.publish(networkPlayer.getServer(), message));
    }

  }

}
