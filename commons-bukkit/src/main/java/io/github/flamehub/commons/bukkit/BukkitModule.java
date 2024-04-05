package io.github.flamehub.commons.bukkit;

import io.github.flamehub.commons.bukkit.dispatcher.FlameDispatcher;
import io.github.flamehub.commons.bukkit.message.BukkitMessagesService;
import io.github.flamehub.commons.bukkit.teleport.TeleporterService;
import io.github.flamehub.commons.config.MongoConfigService;
import io.github.flamehub.commons.database.DatabaseConnector;
import io.github.flamehub.commons.messenger.RedisMessenger;
import io.github.flamehub.commons.network.player.NetworkPlayerCache;
import io.github.flamehub.commons.redis.RedisService;
import io.github.flamehub.commons.server.NetworkServerCache;
import org.bukkit.plugin.Plugin;

public class BukkitModule extends BukkitPlugin {

    protected DatabaseConnector databaseConnector;
    protected RedisService redisService;
    protected RedisMessenger redisMessenger;

    protected NetworkServerCache networkServerCache;
    protected NetworkPlayerCache networkPlayerCache;

    protected BukkitMessagesService messagesService;
    protected MongoConfigService mongoConfigService;

    protected TeleporterService teleporterService;

    @Override
    public void onEnable() {
        CommonsPlugin commonsPlugin = (CommonsPlugin) this.getServer().getPluginManager().getPlugin("commons-bukkit");
        if (commonsPlugin == null) {
            this.getLogger().warning("NIE ZAŁADOWANO PLUGINU: COMMONS-BUKKIT ----> WYŁĄCZAM SERWER");
            this.getServer().shutdown();
            return;
        }

        this.databaseConnector = commonsPlugin.getDatabaseConnector();
        this.redisService = commonsPlugin.getRedisService();
        this.redisMessenger = commonsPlugin.getRedisMessenger();
        this.networkServerCache = commonsPlugin.getNetworkServerCache();
        this.networkPlayerCache = commonsPlugin.getNetworkPlayerCache();
        this.messagesService = commonsPlugin.getMessagesService();
        this.mongoConfigService = commonsPlugin.getMongoConfigService();
        this.teleporterService = commonsPlugin.getTeleporterService();
    }
}
