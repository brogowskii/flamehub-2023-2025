package io.github.flamehub.wallet.user.update;

import io.github.flamehub.commons.messenger.RedisMessenger;
import io.github.flamehub.commons.network.player.NetworkPlayer;
import io.github.flamehub.commons.network.player.NetworkPlayerCache;
import io.github.flamehub.wallet.api.WalletUser;
import io.github.flamehub.wallet.api.WalletUserRepository;

public class WalletUserUpdater {

    private final NetworkPlayerCache networkPlayerCache;
    private final WalletUserRepository walletUserRepository;
    private final RedisMessenger redisMessenger;

    public WalletUserUpdater(NetworkPlayerCache networkPlayerCache, WalletUserRepository walletUserRepository, RedisMessenger redisMessenger) {
        this.networkPlayerCache = networkPlayerCache;
        this.walletUserRepository = walletUserRepository;
        this.redisMessenger = redisMessenger;
    }

    public void update(WalletUser value, WalletUserUpdate update) {
        NetworkPlayer networkPlayer = this.networkPlayerCache.findByName(value.getName());

        // Jeżeli nie ma gracza na żadnym serwerze -> po prostu update do db.
        if (networkPlayer == null) {
            this.walletUserRepository.save(value);
            return;
        }

        this.redisMessenger.publish(networkPlayer.getServer(), update);
    }
}
