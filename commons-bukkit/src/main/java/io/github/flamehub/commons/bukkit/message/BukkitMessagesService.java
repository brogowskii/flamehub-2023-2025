package io.github.flamehub.commons.bukkit.message;

import org.bukkit.command.CommandSender;
import io.github.flamehub.commons.bukkit.text.TextBuilder;
import io.github.flamehub.commons.message.MessagesService;

import java.util.List;

public final class BukkitMessagesService extends MessagesService {

    public TextBuilder getAsText(String path) {
        List<String> messages = this.getMessages(path);
        return TextBuilder.builder().text(messages);
    }

    @Override
    public BukkitMessage message(String path) {
        return new BukkitMessage().add(getMessages(path));
    }

    public void sendMessage(CommandSender commandSender, String path) {
        message(path).send(commandSender);
    }

}
