package io.github.flamehub.mines.mine;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public final class MineQueueTask implements Runnable {

  private final Queue<Mine> minesQueue;

  public MineQueueTask() {
    this.minesQueue = new ConcurrentLinkedQueue<>();
  }

  public void queue(Mine mine) {
    this.minesQueue.offer(mine);
  }

  @Override
  public void run() {

    Mine queuedMine;
    while ((queuedMine = minesQueue.poll()) != null) {
      queuedMine.regenerate();
    }
  }
}