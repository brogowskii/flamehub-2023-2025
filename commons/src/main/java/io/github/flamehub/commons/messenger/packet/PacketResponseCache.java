package io.github.flamehub.commons.messenger.packet;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

@SuppressWarnings("rawtypes")
public final class PacketResponseCache {

  private final Map<UUID, CompletableFuture> waitingResponses = new ConcurrentHashMap<>();

  public void add(UUID uniqueId, CompletableFuture responseFuture) {
    this.waitingResponses.put(uniqueId, responseFuture);
  }

  public void remove(UUID uuid) {
    this.waitingResponses.remove(uuid);
  }

  public Optional<CompletableFuture> findByUUID(UUID uuid) {
    return Optional.ofNullable(this.waitingResponses.get(uuid));
  }

}
