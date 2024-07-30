package io.github.flamehub.proxy.core.queue;

import java.util.LinkedList;

public final class QueueRedirectTask implements Runnable {

  private final static int MAX_PLAYERS_TO_MOVE = 3;

  private final QueueService queueService;
  private final QueueRedirectService queueRedirectTask;

  public QueueRedirectTask(QueueService queueService, QueueRedirectService queueRedirectTask) {
    this.queueService = queueService;
    this.queueRedirectTask = queueRedirectTask;
  }

  @Override
  public void run() {
    this.queueService.getQueues().forEach(this::processQueue);
  }

  private void processQueue(String queue, LinkedList<String> players) {
    if (players.isEmpty()) {
      return;
    }

    int playersToMove = Math.min(MAX_PLAYERS_TO_MOVE, players.size());
    for (int i = 0; i < playersToMove; i++) {
      String queuePlayer;
      try {
        queuePlayer = players.get(i);
      } catch (ArrayIndexOutOfBoundsException | NullPointerException e) {
        continue;
      }

      if (queuePlayer == null || queuePlayer.isEmpty()) {
        this.queueService.remove(queuePlayer);
        continue;
      }

      this.queueRedirectTask.move(queuePlayer, queue);
    }
  }

}
