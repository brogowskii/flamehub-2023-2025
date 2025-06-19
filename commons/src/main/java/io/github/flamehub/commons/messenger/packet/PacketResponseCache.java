package io.github.flamehub.commons.messenger.packet;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

@SuppressWarnings("rawtypes")
public final class PacketResponseCache {

  private final Map<UUID, CompletableFuture> waitingResponses = new ConcurrentHashMap<>();

  public void add(final UUID uniqueId, final CompletableFuture responseFuture) {
    waitingResponses.put(uniqueId, responseFuture);
  }

  public void remove(final UUID uuid) {
    waitingResponses.remove(uuid);
  }

  public Optional<CompletableFuture> findByUUID(final UUID uuid) {
    return Optional.ofNullable(waitingResponses.get(uuid));
  }

}
