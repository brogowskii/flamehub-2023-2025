package io.github.flamehub.commons.bukkit;

import io.github.flamehub.commons.bukkit.message.BukkitMessagesService;
import io.github.flamehub.commons.bukkit.teleport.TeleporterService;
import io.github.flamehub.commons.config.FlameConfigService;
import io.github.flamehub.commons.database.DatabaseConnector;
import io.github.flamehub.commons.messenger.RedisMessenger;
import io.github.flamehub.commons.network.player.NetworkPlayerCache;
import io.github.flamehub.commons.redis.RedisService;
import io.github.flamehub.commons.server.NetworkServerCache;

public class BukkitModule extends BukkitPlugin {

  protected DatabaseConnector databaseConnector;
  protected RedisService redisService;
  protected RedisMessenger redisMessenger;

  protected NetworkServerCache networkServerCache;
  protected NetworkPlayerCache networkPlayerCache;

  protected BukkitMessagesService messagesService;
  protected FlameConfigService flameConfigService;

  protected TeleporterService teleporterService;


  @Override
  public void onEnable() {
    CommonsPlugin commonsPlugin = (CommonsPlugin) getServer().getPluginManager()
        .getPlugin("commons-bukkit");
    if (commonsPlugin == null) {
      getLogger().warning("NIE ZAŁADOWANO PLUGINU: COMMONS-BUKKIT ----> WYŁĄCZAM SERWER");
      getServer().shutdown();
      return;
    }

    this.databaseConnector = commonsPlugin.getDatabaseConnector();
    this.redisService = commonsPlugin.getRedisService();
    this.redisMessenger = commonsPlugin.getRedisMessenger();
    this.networkServerCache = commonsPlugin.getNetworkServerCache();
    this.networkPlayerCache = commonsPlugin.getNetworkPlayerCache();
    this.messagesService = commonsPlugin.getMessagesService();
    this.flameConfigService = commonsPlugin.getFlameConfigService();
    this.teleporterService = commonsPlugin.getTeleporterService();
  }
}
