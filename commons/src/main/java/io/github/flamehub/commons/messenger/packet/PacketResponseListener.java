package io.github.flamehub.commons.messenger.packet;

import org.redisson.api.listener.MessageListener;

public final class PacketResponseListener implements MessageListener<Packet> {

  private final PacketResponseCache packetResponseCache;

  public PacketResponseListener(final PacketResponseCache packetResponseCache) {
    this.packetResponseCache = packetResponseCache;
  }

  @Override
  @SuppressWarnings("unchecked")
  public void onMessage(final CharSequence channel, final Packet packet) {
    if (packet == null) {
      return;
    }
    if (packet instanceof final PacketResponse response) {
      packetResponseCache.findByUUID(response.getUniqueId()).ifPresent(future -> {
        future.complete(response);
        packetResponseCache.remove(response.getUniqueId());
      });
    }
  }
}
