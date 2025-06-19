package io.github.flamehub.commons.redis;

import io.github.flamehub.commons.network.player.NetworkPlayer;
import io.github.flamehub.commons.redis.codec.FuryCodec;
import io.github.flamehub.commons.server.NetworkServerUpdate;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.redisson.config.SingleServerConfig;

public final class RedisService {

  private final String clientId;

  private final RedissonClient client;
  private final FuryCodec furyCodec;

  public RedisService(String hostname, String password, int port, ClassLoader classLoader) {
    Config config = new Config();
    config.setThreads(8);
    config.setNettyThreads(16);

    furyCodec = new FuryCodec(classLoader);
    config.setCodec(furyCodec);
    config.setUseThreadClassLoader(false);
    final SingleServerConfig singleServerConfig = config.useSingleServer();
    singleServerConfig.setAddress("redis://" + hostname + ":" + port);

    if (password != null) {
      singleServerConfig.setPassword(password);
    }

    this.client = Redisson.create(config);
    this.clientId = client.getId();
  }


  public String getClientId() {
    return clientId;
  }

  public RedissonClient getClient() {
    return client;
  }

  public FuryCodec getFuryCodec() {
    return furyCodec;
  }
}
