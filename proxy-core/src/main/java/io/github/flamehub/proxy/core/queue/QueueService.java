package io.github.flamehub.proxy.core.queue;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public final class QueueService {

  private final Map<String, Queue> queues = new ConcurrentHashMap<>();

  public Queue getOrCreate(String name) {
    return queues.computeIfAbsent(name, Queue::new);
  }

  public void removeQueue(String name) {
    queues.remove(name);
  }

  public boolean isWaitingInAnyQueue(String entry) {
    return queues.values().stream().anyMatch(queue -> queue.isWaiting(entry));
  }

  public void removeEntryFromAllQueues(String entry) {
    queues.values().forEach(queue -> queue.removeEntry(entry));
  }

  public Collection<Queue> getQueues() {
    return Collections.unmodifiableCollection(queues.values());
  }

}