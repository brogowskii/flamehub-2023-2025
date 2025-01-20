package io.github.flamehub.commons.redis.cache;

import io.github.flamehub.commons.messenger.packet.Packet;

public final class CacheUpdate implements Packet {

  private final String pid;
  private final String key;

  public CacheUpdate(final String pid, final String key) {
    this.pid = pid;
    this.key = key;
  }

  public String getPid() {
    return pid;
  }

  public String getKey() {
    return key;
  }
}
