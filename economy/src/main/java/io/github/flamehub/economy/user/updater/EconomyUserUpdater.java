package io.github.flamehub.economy.user.updater;

import io.github.flamehub.commons.messenger.RedisMessenger;
import io.github.flamehub.commons.network.player.NetworkPlayer;
import io.github.flamehub.commons.network.player.NetworkPlayerCache;
import io.github.flamehub.economy.user.EconomyUser;
import io.github.flamehub.economy.user.EconomyUserRepository;

public class EconomyUserUpdater {

    private final NetworkPlayerCache networkPlayerCache;
    private final EconomyUserRepository economyUserRepository;
    private final RedisMessenger redisMessenger;

    public EconomyUserUpdater(NetworkPlayerCache networkPlayerCache, EconomyUserRepository economyUserRepository, RedisMessenger redisMessenger) {
        this.networkPlayerCache = networkPlayerCache;
        this.economyUserRepository = economyUserRepository;
        this.redisMessenger = redisMessenger;
    }

    public void update(EconomyUser value, EconomyUserUpdate update) {
        NetworkPlayer networkPlayer = this.networkPlayerCache.findByName(value.getName());

        // Jeżeli nie ma gracza na żadnym serwerze -> po prostu update do db.
        if (networkPlayer == null) {
            this.economyUserRepository.save(value);
            return;
        }

        this.redisMessenger.publish(networkPlayer.getServer(), update);
    }

}
