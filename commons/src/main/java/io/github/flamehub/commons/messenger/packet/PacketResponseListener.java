package io.github.flamehub.commons.messenger.packet;

import io.github.flamehub.commons.messenger.codec.PacketGsonCodec;
import io.lettuce.core.pubsub.RedisPubSubListener;

public final class PacketResponseListener implements RedisPubSubListener<String, String> {

  private final PacketResponseCache packetResponseCache;
  private final String subscribedChannel;

  public PacketResponseListener(PacketResponseCache packetResponseCache, String subscribedChannel) {
    this.packetResponseCache = packetResponseCache;
    this.subscribedChannel = subscribedChannel;
  }

  @Override
  @SuppressWarnings({"unchecked"})
  public void message(String channel, String json) {
    if (!channel.equals(subscribedChannel)) {
      return;
    }

    Packet packet = PacketGsonCodec.deserialize(json);
    if (packet == null) {
      return;
    }

    if (packet instanceof PacketResponse response) {
      packetResponseCache.findByUUID(response.getUniqueId()).ifPresent(future -> {

        future.complete(response);
        packetResponseCache.remove(response.getUniqueId());

      });

    }

  }

  @Override
  public void message(String s, String k1, String json) {

  }

  @Override
  public void subscribed(String s, long l) {

  }

  @Override
  public void psubscribed(String s, long l) {

  }

  @Override
  public void unsubscribed(String s, long l) {

  }

  @Override
  public void punsubscribed(String s, long l) {

  }
}
