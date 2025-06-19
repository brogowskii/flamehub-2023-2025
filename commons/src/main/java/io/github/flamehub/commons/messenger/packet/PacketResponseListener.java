package io.github.flamehub.commons.messenger.packet;

import org.redisson.api.listener.MessageListener;

public final class PacketResponseListener implements MessageListener<Packet> {

  private final PacketResponseCache packetResponseCache;

  public PacketResponseListener(PacketResponseCache packetResponseCache) {
    this.packetResponseCache = packetResponseCache;
  }

  @Override
  @SuppressWarnings("unchecked")
  public void onMessage(CharSequence channel, Packet packet) {
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
}
