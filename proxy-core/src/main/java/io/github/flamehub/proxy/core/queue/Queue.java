package io.github.flamehub.proxy.core.queue;

import java.util.concurrent.LinkedBlockingQueue;

public final class Queue {

  private final String name;
  private final LinkedBlockingQueue<String> entries = new LinkedBlockingQueue<>();

  public Queue(String name) {
    this.name = name;
  }

  public void addEntry(String entry) {
    entries.offer(entry);
  }

  public boolean removeEntry(String entry) {
    return entries.remove(entry);
  }

  public boolean isWaiting(String entry) {
    return entries.contains(entry);
  }

  public int getPlace(String entry) {
    int index = 0;
    for (String current : entries) {
      if (current.equals(entry)) {
        return index;
      }
      index++;
    }
    return -1;
  }

  public LinkedBlockingQueue<String> getEntries() {
    return new LinkedBlockingQueue<>(entries);
  }

  public String getName() {
    return name;
  }
}
