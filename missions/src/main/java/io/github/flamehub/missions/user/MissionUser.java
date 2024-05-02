package io.github.flamehub.missions.user;

import dev.morphia.annotations.Entity;
import io.github.flamehub.commons.user.UserUpdatable;
import io.github.flamehub.missions.Mission;

import java.util.UUID;

@Entity("mission_users")
public final class MissionUser extends UserUpdatable {

    private Mission dailyMission;

    public MissionUser() {
    }

    public MissionUser(UUID uniqueId, String name) {
        super(uniqueId, name);
    }

    public void setDailyMission(Mission dailyMission) {
        this.dailyMission = dailyMission;
    }


    public Mission getDailyMission() {
        return dailyMission;
    }

}
