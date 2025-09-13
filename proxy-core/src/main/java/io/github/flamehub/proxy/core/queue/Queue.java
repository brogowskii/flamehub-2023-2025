package io.github.flamehub.proxy.core.queue;

import java.util.concurrent.LinkedBlockingQueue;

public final class Queue {

  private final String name;
  private final LinkedBlockingQueue<String> entries = new LinkedBlockingQueue<>();

  public Queue(final String name) {
    this.name = name;
  }

  public void addEntry(final String entry) {
    entries.offer(entry);
  }

  public boolean removeEntry(final String entry) {
    return entries.remove(entry);
  }

  public boolean isWaiting(final String entry) {
    return entries.contains(entry);
  }

  public int getPlace(final String entry) {
    int index = 0;
    for (final String current : entries) {
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
