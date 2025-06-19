package io.github.flamehub.reward.bot;

import io.github.flamehub.commons.database.DatabaseConnector;
import io.github.flamehub.commons.database.DatastoreFactory;
import io.github.flamehub.commons.messenger.RedisMessenger;
import io.github.flamehub.commons.network.player.NetworkPlayerCache;
import io.github.flamehub.commons.network.player.NetworkPlayerHandler;
import io.github.flamehub.commons.property.PropertyLoader;
import io.github.flamehub.commons.redis.RedisService;
import io.github.flamehub.commons.server.NetworkServer;
import io.github.flamehub.commons.server.NetworkServerCache;
import io.github.flamehub.commons.server.NetworkServerLoader;
import io.github.flamehub.commons.server.NetworkServerRepository;
import io.github.flamehub.commons.server.NetworkServerStatistics;
import io.github.flamehub.commons.server.NetworkServerUpdateHandler;
import io.github.flamehub.reward.api.RewardReceivedEntry;
import io.github.flamehub.reward.api.RewardReceivedEntryRepository;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import java.util.logging.Logger;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.requests.GatewayIntent;
import org.jetbrains.annotations.NotNull;

public final class RewardBot {

  private final DatabaseConnector databaseConnector;

  private final RedisService redisService;
  private final RedisMessenger redisMessenger;

  private final NetworkPlayerCache networkPlayerCache;
  private final NetworkServerCache networkServerCache;
  private final NetworkServerRepository networkServerRepository;
  private final NetworkServerLoader networkServerLoader;

  private final RewardReceivedEntryRepository rewardReceivedEntryRepository;

  private final JDA jda;

  public RewardBot() {
    saveResource("credentials.properties", false);
    final PropertyLoader credentialsProperties = new PropertyLoader(
        getJarFile() + "/credentials.properties"
    );
    this.databaseConnector = new DatabaseConnector(credentialsProperties.getProperty("mongo.uri"));
    this.redisService = new RedisService(
        credentialsProperties.getProperty("redis.host"),
        credentialsProperties.getProperty("redis.password"),
        Integer.parseInt(credentialsProperties.getProperty("redis.port")),
        RewardBot.class.getClassLoader()
    );
    this.redisMessenger = new RedisMessenger(redisService.getClient());
    redisMessenger.subscribeCallbacks("callbacks");

    this.networkPlayerCache = new NetworkPlayerCache(redisService, redisMessenger);
    networkPlayerCache.load();

    this.rewardReceivedEntryRepository = new RewardReceivedEntryRepository(
        DatastoreFactory.create(
            databaseConnector.getMongoClient(),
            "global",
            RewardReceivedEntry.class
        ),
        RewardReceivedEntry.class
    );

    this.networkServerCache = new NetworkServerCache();
    this.networkServerRepository = new NetworkServerRepository(
        DatastoreFactory.create(
            databaseConnector.getMongoClient(),
            "global",
            NetworkServer.class,
            NetworkServerStatistics.class
        ),
        NetworkServer.class
    );
    this.networkServerLoader = new NetworkServerLoader(
        Logger.getLogger(RewardBot.class.getSimpleName()),
        networkServerCache,
        networkServerRepository,
        "reward-bot"
    );
    networkServerLoader.load();

    redisMessenger.subscribe("network_servers",
        new NetworkServerUpdateHandler(Logger.getLogger("RewardBot"), networkServerCache));

    redisMessenger.subscribe("network_players",
        new NetworkPlayerHandler(networkPlayerCache));

    this.jda = JDABuilder.createLight(
            "MTA3NTUwMTQyODQ3NjQ3NzU2Mw.GqvTJy.u_f935QnZxqCbTk7LoqdbfVNtzYNp4SwZESySk")
        .enableIntents(
            GatewayIntent.GUILD_MEMBERS,
            GatewayIntent.GUILD_EMOJIS_AND_STICKERS,
            GatewayIntent.GUILD_WEBHOOKS,
            GatewayIntent.GUILD_INVITES,
            GatewayIntent.GUILD_VOICE_STATES,
            GatewayIntent.GUILD_PRESENCES,
            GatewayIntent.GUILD_MESSAGES,
            GatewayIntent.GUILD_MESSAGE_REACTIONS,
            GatewayIntent.GUILD_MESSAGE_TYPING,
            GatewayIntent.DIRECT_MESSAGES,
            GatewayIntent.DIRECT_MESSAGE_REACTIONS,
            GatewayIntent.DIRECT_MESSAGE_TYPING,
            GatewayIntent.MESSAGE_CONTENT,
            GatewayIntent.SCHEDULED_EVENTS
        )
        .addEventListeners(
            new RewardBotListeners(rewardReceivedEntryRepository, networkPlayerCache,
                networkServerCache, redisMessenger)
        )
        .setActivity(Activity.of(Activity.ActivityType.STREAMING, "Flamehub.pl - Reward System"))
        .build();
  }

  public File getJarFile() {
    File jarFile = null;
    try {
      jarFile = new File(
          RewardBot.class.getProtectionDomain().getCodeSource().getLocation().toURI());
    } catch (URISyntaxException e) {
      throw new RuntimeException(e);
    }
    return new File(jarFile.getParent());
  }


  public void saveResource(@NotNull String resourcePath, boolean replace) {
    if (resourcePath == null || resourcePath.equals("")) {
      throw new IllegalArgumentException("ResourcePath cannot be null or empty");
    }

    resourcePath = resourcePath.replace('\\', '/');
    InputStream in = getResource(resourcePath);
    if (in == null) {
      throw new IllegalArgumentException(
          "The embedded resource '" + resourcePath + "' cannot be found");
    }

    File outFile = new File(getJarFile(), resourcePath);
    int lastIndex = resourcePath.lastIndexOf('/');
    File outDir = new File(getJarFile(), resourcePath.substring(0, Math.max(lastIndex, 0)));

    if (!outDir.exists()) {
      outDir.mkdirs();
    }

    try {
      if (!outFile.exists() || replace) {
        OutputStream out = new FileOutputStream(outFile);
        byte[] buf = new byte[1024];
        int len;
        while ((len = in.read(buf)) > 0) {
          out.write(buf, 0, len);
        }
        out.close();
        in.close();
      } else {
        System.out.println("Could not save " + outFile.getName() + " to " + outFile + " because "
            + outFile.getName() + " already exists.");
      }
    } catch (IOException ex) {
      System.out.println("Could not save " + outFile.getName() + " to " + outFile);
    }
  }

  public InputStream getResource(@NotNull String filename) {

    try {
      URL url = getClass().getClassLoader().getResource(filename);

      if (url == null) {
        return null;
      }

      URLConnection connection = url.openConnection();
      connection.setUseCaches(false);
      return connection.getInputStream();
    } catch (IOException ex) {
      return null;
    }
  }

}
