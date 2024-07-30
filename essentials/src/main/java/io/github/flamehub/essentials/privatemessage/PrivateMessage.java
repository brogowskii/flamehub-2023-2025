package io.github.flamehub.essentials.privatemessage;

import io.github.flamehub.commons.messenger.packet.Packet;

final class PrivateMessage implements Packet {

  private final String sender;
  private final String receiver;
  private final String message;

  PrivateMessage(final String sender, final String receiver, final String message) {
    this.sender = sender;
    this.receiver = receiver;
    this.message = message;
  }

  public String getSender() {
    return sender;
  }

  public String getReceiver() {
    return receiver;
  }

  public String getMessage() {
    return message;
  }
}
