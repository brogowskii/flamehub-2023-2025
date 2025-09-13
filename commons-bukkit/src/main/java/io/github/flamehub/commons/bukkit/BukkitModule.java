package io.github.flamehub.commons.bukkit;

import io.github.flamehub.commons.bukkit.message.BukkitMessagesService;
import io.github.flamehub.commons.bukkit.setting.ServerSettingConfig;
import io.github.flamehub.commons.bukkit.teleport.TeleporterService;
import io.github.flamehub.commons.config.FlameConfigService;
import io.github.flamehub.commons.database.DatabaseConnector;
import io.github.flamehub.commons.messenger.RedisMessenger;
import io.github.flamehub.commons.network.player.NetworkPlayerCache;
import io.github.flamehub.commons.redis.RedisService;
import io.github.flamehub.commons.server.NetworkServerFacade;

public class BukkitModule extends BukkitPlugin {

  protected DatabaseConnector databaseConnector;
  protected RedisService redisService;
  protected RedisMessenger redisMessenger;

  protected NetworkServerFacade networkServerFacade;
  protected NetworkPlayerCache networkPlayerCache;

  protected BukkitMessagesService messagesService;
  protected FlameConfigService flameConfigService;

  protected TeleporterService teleporterService;
  protected ServerSettingConfig serverSettingConfig;


  @Override
  public void onEnable() {
    final CommonsPlugin commonsPlugin = (CommonsPlugin) getServer().getPluginManager()
        .getPlugin("commons-bukkit");
    if (commonsPlugin == null) {
      getLogger().warning("NIE ZAŁADOWANO PLUGINU: COMMONS-BUKKIT ----> WYŁĄCZAM SERWER");
      getServer().shutdown();
      return;
    }

    databaseConnector = commonsPlugin.getDatabaseConnector();
    redisService = commonsPlugin.getRedisService();
    redisMessenger = commonsPlugin.getRedisMessenger();
    networkServerFacade = commonsPlugin.getNetworkServerFacade();
    networkPlayerCache = commonsPlugin.getNetworkPlayerCache();
    messagesService = commonsPlugin.getMessagesService();
    flameConfigService = commonsPlugin.getFlameConfigService();
    teleporterService = commonsPlugin.getTeleporterService();
    serverSettingConfig = commonsPlugin.getServerSettingConfig();
  }
}
