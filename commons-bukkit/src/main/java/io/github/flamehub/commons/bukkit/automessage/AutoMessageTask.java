package io.github.flamehub.commons.bukkit.automessage;

import io.github.flamehub.commons.bukkit.message.BukkitMessage;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public final class AutoMessageTask implements Runnable {

  private final AutoMessageConfig autoMessageConfig;
  int index = 0;

  public AutoMessageTask(AutoMessageConfig toolsConfig) {
    this.autoMessageConfig = toolsConfig;
  }

  @Override
  public void run() {

    if (autoMessageConfig.getAutoMessageList().isEmpty()) {
      return;
    }

    AutoMessage autoMessage = autoMessageConfig.getAutoMessageList().get(index);
    for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
      BukkitMessage.from(autoMessage.getMessages()).deliver(onlinePlayer);
    }

    index++;
    if (index >= autoMessageConfig.getAutoMessageList().size()) {
      index = 0;
    }

  }
}
