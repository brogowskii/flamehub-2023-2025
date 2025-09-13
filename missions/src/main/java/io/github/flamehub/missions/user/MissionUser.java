package io.github.flamehub.missions.user;

import dev.morphia.annotations.Entity;
import io.github.flamehub.commons.user.UserUpdatable;
import io.github.flamehub.missions.MissionProgress;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity("mission_users")
public final class MissionUser extends UserUpdatable {

  private List<MissionProgress> activeMissions = new ArrayList<>();

  public MissionUser() {
  }

  public MissionUser(final UUID uniqueId, final String name) {
    super(uniqueId, name);
  }

  public List<MissionProgress> getActiveMissions() {
    return activeMissions;
  }

  public void setActiveMissions(final List<MissionProgress> activeMissions) {
    this.activeMissions = activeMissions;
  }
}