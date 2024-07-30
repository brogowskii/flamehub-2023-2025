package io.github.flamehub.commons.user;

import dev.morphia.annotations.Transient;
import java.util.UUID;

public class UserUpdatable extends User {

  @Transient
  private boolean needUpdate = false;

  public UserUpdatable() {

  }

  public UserUpdatable(final UUID uniqueId, final String name) {
    super(uniqueId, name);
  }

  public void markToUpdate() {
    this.needUpdate = true;
  }

  public void markUpdated() {
    this.needUpdate = false;
  }

  public boolean isNeedUpdate() {
    return needUpdate;
  }

  public void setNeedUpdate(boolean needUpdate) {
    this.needUpdate = needUpdate;
  }
}
