package io.github.flamehub.proxy.core.queue;

import java.util.concurrent.LinkedBlockingQueue;

public final class QueueRedirectTask implements Runnable {

  private long nextMove;

  private final QueueConfig queueConfig;
  private final QueueService queueService;
  private final QueueRedirectService queueRedirectService;

  public QueueRedirectTask(final QueueConfig queueConfig, final QueueService queueService, final QueueRedirectService queueRedirectService) {
    this.queueConfig = queueConfig;
    this.queueService = queueService;
    this.queueRedirectService = queueRedirectService;
    this.nextMove = System.currentTimeMillis() + queueConfig.getDelay();
  }

  @Override
  public void run() {

    final long currentTimeMillis = System.currentTimeMillis();
    if (currentTimeMillis < this.nextMove) {
      return;
    }

    nextMove = currentTimeMillis + queueConfig.getDelay();
    this.queueService.getQueues().forEach(this::processQueue);
  }

  private void processQueue(final Queue queue) {
    final LinkedBlockingQueue<String> entries = queue.getEntries();
    if (entries.isEmpty()) {
      return;
    }

    final int playersToMove = Math.min(queueConfig.getPlayersPerMove(), entries.size());
    for (int i = 0; i < playersToMove; i++) {
      final String queuePlayer = entries.poll();
      if (queuePlayer == null || queuePlayer.isEmpty()) {
        continue;
      }

      this.queueRedirectService.move(queuePlayer, queue);
    }
  }

}
