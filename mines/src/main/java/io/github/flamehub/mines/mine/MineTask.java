package io.github.flamehub.mines.mine;

import io.github.flamehub.commons.util.TimeUtil;

public final class MineTask implements Runnable {

  private final MineConfig mineConfig;
  private final MineQueueTask mineQueueTask;

  public MineTask(MineConfig mineConfig, MineQueueTask mineQueueTask) {
    this.mineConfig = mineConfig;
    this.mineQueueTask = mineQueueTask;
  }

  @Override
  public void run() {

    long millis = System.currentTimeMillis();
    for (Mine value : this.mineConfig.getMinesById().values()) {
      if (value.getLastTimeGenerate() > millis) {
        continue;
      }

      value.setLastTimeGenerate(millis + TimeUtil.parseTime(value.getRenewDelay()).toMillis());
      this.mineQueueTask.queue(value);
    }
  }

}
