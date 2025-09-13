package io.github.flamehub.commons.redis;

import io.github.flamehub.commons.redis.codec.FuryCodec;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.redisson.config.SingleServerConfig;

public final class RedisService {

  private final String clientId;

  private final RedissonClient client;
  private final FuryCodec furyCodec;

  public RedisService(final String hostname, final String password, final int port, final ClassLoader classLoader) {
    final Config config = new Config();
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

    client = Redisson.create(config);
    clientId = client.getId();
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
