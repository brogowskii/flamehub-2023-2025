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

        if (this.autoMessageConfig.getAutoMessageList().isEmpty()) {
            return;
        }

        AutoMessage autoMessage = this.autoMessageConfig.getAutoMessageList().get(index);
        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            BukkitMessage.from(autoMessage.getMessages()).send(onlinePlayer);
        }

        index++;
        if (index >= this.autoMessageConfig.getAutoMessageList().size()) {
            index = 0;
        }

    }
}
