package io.github.flamehub.essentials.user;

import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Transient;
import io.github.flamehub.commons.user.UserUpdatable;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity("essentials_users")
public final class EssentialsUser extends UserUpdatable {

  private boolean ignoreAll;
  private Set<UUID> ignoredPlayers;
  private UUID reply;

  @Transient
  private boolean socialSpy;

  public EssentialsUser() {
  }

  public EssentialsUser(final UUID uniqueId, final String name) {
    super(uniqueId, name);
  }

  public boolean isIgnoreAll() {
    return ignoreAll;
  }

  public void setIgnoreAll(boolean ignoreAll) {
    this.ignoreAll = ignoreAll;
  }

  public Set<UUID> getIgnoredPlayers() {
    if (ignoredPlayers == null) {
      ignoredPlayers = new HashSet<>();
    }
    return ignoredPlayers;
  }

  public void setIgnoredPlayers(Set<UUID> ignoredPlayers) {
    this.ignoredPlayers = ignoredPlayers;
  }

  public boolean isSocialSpy() {
    return socialSpy;
  }

  public void setSocialSpy(boolean socialSpy) {
    this.socialSpy = socialSpy;
  }

  public boolean isIgnore(UUID uniqueId) {
    return getIgnoredPlayers().contains(uniqueId);
  }

  public void addIgnore(UUID uniqueId) {
    getIgnoredPlayers().add(uniqueId);
  }

  public void removeIgnore(UUID uniqueId) {
    getIgnoredPlayers().remove(uniqueId);
  }

  public UUID getReply() {
    return reply;
  }

  public void setReply(UUID reply) {
    this.reply = reply;
  }
}
