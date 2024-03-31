package io.github.flamehub.commons.bukkit;

import io.github.flamehub.commons.bukkit.dispatcher.FlameDispatcher;
import io.github.flamehub.commons.bukkit.message.BukkitMessagesService;
import io.github.flamehub.commons.config.MongoConfigService;
import io.github.flamehub.commons.database.DatabaseConnector;
import io.github.flamehub.commons.messenger.RedisMessenger;
import io.github.flamehub.commons.network.player.NetworkPlayerCache;
import io.github.flamehub.commons.redis.RedisService;
import io.github.flamehub.commons.server.NetworkServerCache;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class BukkitModule {

    protected Set<Object> commandSet = new HashSet<>();

    protected Plugin plugin;
    protected FlameDispatcher flameDispatcher;

    protected DatabaseConnector databaseConnector;
    protected RedisService redisService;
    protected RedisMessenger redisMessenger;

    protected NetworkServerCache networkServerCache;
    protected NetworkPlayerCache networkPlayerCache;

    protected BukkitMessagesService messagesService;
    protected MongoConfigService mongoConfigService;

    public BukkitModule(final Plugin plugin, final FlameDispatcher flameDispatcher) {
        this.plugin = plugin;
        this.flameDispatcher = flameDispatcher;

        CommonsPlugin commonsPlugin = (CommonsPlugin) this.plugin.getServer().getPluginManager().getPlugin("commons-bukkit");
        if (commonsPlugin == null) {
            this.plugin.getLogger().warning("NIE ZAŁADOWANO PLUGINU: COMMONS-BUKKIT ----> WYŁĄCZAM SERWER");
            this.plugin.getServer().shutdown();
            return;
        }

        this.databaseConnector = commonsPlugin.getDatabaseConnector();
        this.redisService = commonsPlugin.getRedisService();
        this.redisMessenger = commonsPlugin.getRedisMessenger();
        this.networkServerCache = commonsPlugin.getNetworkServerCache();
        this.networkPlayerCache = commonsPlugin.getNetworkPlayerCache();
        this.messagesService = commonsPlugin.getMessagesService();
        this.mongoConfigService = commonsPlugin.getMongoConfigService();

        System.out.println(this.getClass().getSimpleName() + " = " + this.databaseConnector.toString());
    }

    public Set<Object> getCommandSet() {
        return commandSet;
    }

    public void addCommands(final Object... commands) {
        this.commandSet.addAll(List.of(commands));
    }

    public void registerListeners(final Listener... listeners) {
        for (Listener listener : listeners) {
            this.plugin.getServer().getPluginManager().registerEvents(listener, this.plugin);
        }
    }

    public void onLoad() {
    }

    public void onDisable() {
    }

    public void onEnable() {

    }

}
