package io.github.flamehub.mines.mine;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public final class MineQueueTask implements Runnable {

  private final Queue<Mine> minesQueue;

  public MineQueueTask() {
    minesQueue = new ConcurrentLinkedQueue<>();
  }

  public void queue(final Mine mine) {
    minesQueue.offer(mine);
  }

  @Override
  public void run() {

    Mine queuedMine;
    while ((queuedMine = minesQueue.poll()) != null) {
      queuedMine.regenerate();
    }
  }
}