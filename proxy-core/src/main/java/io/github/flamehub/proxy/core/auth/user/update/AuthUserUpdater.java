package io.github.flamehub.proxy.core.auth.user.update;

import io.github.flamehub.proxy.core.auth.user.AuthUser;
import io.github.flamehub.proxy.core.auth.user.AuthUserRepository;
import io.github.flamehub.commons.messenger.RedisMessenger;
import io.github.flamehub.commons.network.player.NetworkPlayer;
import io.github.flamehub.commons.network.player.NetworkPlayerCache;
import io.github.flamehub.commons.updater.ObjectUpdate;

public class AuthUserUpdater {

    private final NetworkPlayerCache networkPlayerCache;
    private final AuthUserRepository authUserRepository;
    private final RedisMessenger redisMessenger;

    public AuthUserUpdater(NetworkPlayerCache networkPlayerCache, AuthUserRepository authUserRepository, RedisMessenger redisMessenger) {
        this.networkPlayerCache = networkPlayerCache;
        this.authUserRepository = authUserRepository;
        this.redisMessenger = redisMessenger;
    }

    public <U extends ObjectUpdate> void update(AuthUser value, U update) {
        NetworkPlayer networkPlayer = this.networkPlayerCache.findByName(value.getName());

        // Jeżeli nie ma gracza na żadnym serwerze -> po prostu update do db.
        if (networkPlayer == null) {
            this.authUserRepository.save(value);
            return;
        }

        this.redisMessenger.publish(networkPlayer.getProxy(), update);
    }
}
