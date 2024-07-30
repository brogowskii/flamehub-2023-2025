package io.github.flamehub.proxy.core.queue;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public final class QueueService {

  private final Map<String, LinkedList<String>> queues = new ConcurrentHashMap<>();

  public void add(String queue, String entry) {
    queues.computeIfAbsent(queue, k -> new LinkedList<>()).add(entry);
  }

  public void remove(String entry) {
    queues.forEach((key, value) -> value.remove(entry));
  }

  public void remove(String queue, String entry) {
    LinkedList<String> queueList = queues.get(queue);
    if (queueList != null) {
      queueList.remove(entry);
    }

  }

  public List<String> findPlayersFromQueue(String queue) {
    return new LinkedList<>(queues.getOrDefault(queue, new LinkedList<>()));
  }

  public boolean isInQueue(String queue, String entry) {
    LinkedList<String> queueList = queues.get(queue);
    return queueList != null && queueList.contains(entry);
  }

  public List<String> getAllPlayers(String queue) {
    return findPlayersFromQueue(queue);
  }

  public int getPlace(String queue, String player) {
    List<String> players = findPlayersFromQueue(queue);
    return players.indexOf(player);
  }

  public Map<String, LinkedList<String>> getQueues() {
    return queues;
  }
}