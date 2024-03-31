package io.github.flamehub.proxy.core.server;

import com.velocitypowered.api.proxy.ProxyServer;
import io.github.flamehub.commons.messenger.RedisMessenger;
import io.github.flamehub.commons.server.NetworkServer;
import io.github.flamehub.commons.server.NetworkServerCache;
import io.github.flamehub.commons.server.NetworkServerUpdate;

public class NetworkServerUpdateTask implements Runnable {

    private final ProxyServer proxyServer;
    private final RedisMessenger redisMessenger;
    private final NetworkServerCache networkServerCache;

    public NetworkServerUpdateTask(ProxyServer proxyServer, RedisMessenger redisMessenger, NetworkServerCache networkServerCache) {
        this.proxyServer = proxyServer;
        this.redisMessenger = redisMessenger;
        this.networkServerCache = networkServerCache;
    }

    @Override
    public void run() {
        NetworkServer current = this.networkServerCache.getCurrent();
        NetworkServerUpdate networkServerUpdate = new NetworkServerUpdate(
                current.getName(),
                this.proxyServer.getPlayerCount(),
                current.getStatistics().getPlayersLimit(),
                false,
                new double[4]);
        this.redisMessenger.publish("network_servers", networkServerUpdate);
    }
}
