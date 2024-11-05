package io.github.flamehub.proxy.core.queue;

import io.github.flamehub.commons.config.EnableRemote;
import io.github.flamehub.commons.config.FlameConfig;
import io.github.flamehub.commons.config.FlameConfigProperties;
import java.util.concurrent.TimeUnit;

@FlameConfigProperties(name = "queue.json")
@EnableRemote(collection = "configs")
public final class QueueConfig extends FlameConfig {

  private int playersPerMove = 5;
  private long delay = TimeUnit.SECONDS.toMillis(5);

  public int getPlayersPerMove() {
    return playersPerMove;
  }

  public void setPlayersPerMove(final int playersPerMove) {
    this.playersPerMove = playersPerMove;
  }

  public long getDelay() {
    return delay;
  }

  public void setDelay(final long delay) {
    this.delay = delay;
  }
}
