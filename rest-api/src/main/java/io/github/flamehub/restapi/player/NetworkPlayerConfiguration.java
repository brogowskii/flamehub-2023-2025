package io.github.flamehub.restapi.player;

import io.github.flamehub.commons.messenger.RedisMessenger;
import io.github.flamehub.commons.network.player.NetworkPlayerCache;
import io.github.flamehub.commons.network.player.NetworkPlayerHandler;
import io.github.flamehub.commons.redis.RedisService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
class NetworkPlayerConfiguration {

    @Bean
    public NetworkPlayerCache networkPlayerCache(final RedisService redisService, final RedisMessenger redisMessenger) {
        NetworkPlayerCache networkPlayerCache = new NetworkPlayerCache(redisService, redisMessenger);
        networkPlayerCache.load();

        redisMessenger.subscribe("network_players", new NetworkPlayerHandler(networkPlayerCache));

        return networkPlayerCache;
    }

}
