package io.github.flamehub.commons.message;

import io.github.flamehub.commons.messenger.packet.PacketHandler;

public class MessageReloadHandler {

  private final MessagesRepository repository;

  public MessageReloadHandler(MessagesRepository repository) {
    this.repository = repository;
  }

  @PacketHandler
  public void handle(MessageReload reload) {
    repository.loadMessages();
    System.out.println("Successfully reloaded messages configuration.");
  }
}
