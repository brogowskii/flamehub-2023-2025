package io.github.flamehub.commons.bukkit.user.impl;

import dev.morphia.annotations.Entity;
import io.github.flamehub.commons.user.User;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity("common_users")
public final class CommonUser extends User {

  private Set<String> disabledNetworkMessages = new HashSet<>();

  public CommonUser() {
  }

  public CommonUser(final UUID uniqueId, final String name) {
    super(uniqueId, name);
  }

  public boolean isDisabledNetworkMessage(final String messageId) {
    return disabledNetworkMessages.contains(messageId);
  }

  public void addDisabledNetworkMessage(final String messageId) {
    disabledNetworkMessages.add(messageId);
  }

  public void removeDisabledNetworkMessage(final String messageId) {
    disabledNetworkMessages.remove(messageId);
  }

  public Set<String> getDisabledNetworkMessages() {
    return disabledNetworkMessages;
  }
}
