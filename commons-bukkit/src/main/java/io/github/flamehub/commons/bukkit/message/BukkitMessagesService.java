package io.github.flamehub.commons.bukkit.message;

import io.github.flamehub.commons.bukkit.text.TextBuilder;
import io.github.flamehub.commons.message.MessagesService;
import java.util.List;
import org.bukkit.command.CommandSender;

public final class BukkitMessagesService extends MessagesService {

  public TextBuilder getAsText(final String path) {
    final List<String> messages = getMessages(path);
    return TextBuilder.builder().text(messages);
  }

  @Override
  public BukkitMessage message(final String path) {
    return new BukkitMessage().add(getMessages(path));
  }

  public void sendMessage(final CommandSender commandSender, final String path) {
    message(path).deliver(commandSender);
  }

}
