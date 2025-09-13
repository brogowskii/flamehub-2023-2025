package io.github.flamehub.mines.mine;

import io.github.flamehub.commons.util.TimeUtil;

public final class MineTask implements Runnable {

  private final MineConfig mineConfig;
  private final MineQueueTask mineQueueTask;

  public MineTask(final MineConfig mineConfig, final MineQueueTask mineQueueTask) {
    this.mineConfig = mineConfig;
    this.mineQueueTask = mineQueueTask;
  }

  @Override
  public void run() {

    final long millis = System.currentTimeMillis();
    for (final Mine value : mineConfig.getMinesById().values()) {
      if (value.getLastTimeGenerate() > millis) {
        continue;
      }

      value.setLastTimeGenerate(millis + TimeUtil.parseTime(value.getRenewDelay()).toMillis());
      mineQueueTask.queue(value);
    }
  }

}
