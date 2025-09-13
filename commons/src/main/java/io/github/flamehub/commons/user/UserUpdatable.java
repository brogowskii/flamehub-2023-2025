package io.github.flamehub.commons.user;

import dev.morphia.annotations.Transient;
import java.util.UUID;

public class UserUpdatable extends User {

  @Transient
  private boolean needUpdate;

  public UserUpdatable() {

  }

  public UserUpdatable(final UUID uniqueId, final String name) {
    super(uniqueId, name);
  }

  public void markToUpdate() {
    needUpdate = true;
  }

  public void markUpdated() {
    needUpdate = false;
  }

  public boolean isNeedUpdate() {
    return needUpdate;
  }

  public void setNeedUpdate(final boolean needUpdate) {
    this.needUpdate = needUpdate;
  }
}
