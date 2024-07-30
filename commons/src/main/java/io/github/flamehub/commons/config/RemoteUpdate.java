package io.github.flamehub.commons.config;

import io.github.flamehub.commons.messenger.packet.Packet;

public final class RemoteUpdate implements Packet {

  private final String configClassName;

  public RemoteUpdate(String configClassName) {
    this.configClassName = configClassName;
  }

  public String getConfigClassName() {
    return configClassName;
  }
}
