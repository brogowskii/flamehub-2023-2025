package io.github.flamehub.proxy.core.message;

import com.velocitypowered.api.command.CommandSource;
import io.github.flamehub.commons.message.MessagesService;

public class VelocityMessagesService extends MessagesService {

  @Override
  public VelocityMessage message(String path) {
    return new VelocityMessage().add(getMessages(path));
  }

  public void sendMessage(CommandSource commandSource, String path) {
    message(path).send(commandSource);
  }
}
