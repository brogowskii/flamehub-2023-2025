package io.github.flamehub.missions;

import io.github.flamehub.commons.config.FlameConfig;
import io.github.flamehub.commons.config.FlameConfigProperties;
import java.util.Arrays;
import java.util.List;

@FlameConfigProperties(name = "missions.json")
public final class MissionConfig extends FlameConfig {



  private List<MissionDefinition> missions = Arrays.asList(
      new MissionDefinition(MissionType.KILL, 10, 1500),
      new MissionDefinition(MissionType.BLOCK_BREAK, 5000, 1500),
      new MissionDefinition(MissionType.WOOL_BREAK, 100, 1500),
      new MissionDefinition(MissionType.DAMAGE_DEALT, 1000, 1500),
      new MissionDefinition(MissionType.CLAIM_RANKING, 100, 1500),
      new MissionDefinition(MissionType.EAT_GOLDEN_APPLES, 50, 2),
      new MissionDefinition(MissionType.OPEN_CRATE, 100, 2)
  );

  public MissionConfig() {
  }

  public MissionConfig(final List<MissionDefinition> missions) {
    this.missions = missions;
  }

  public List<MissionDefinition> getMissions() {
    return missions;
  }

  public static final class MissionDefinition {

    private MissionType type;
    private long required;
    private int experience;

    public MissionDefinition(final MissionType type, final long required, final int experience) {
      this.type = type;
      this.required = required;
      this.experience = experience;
    }

    public MissionDefinition() {
    }

    public MissionType getType() {
      return type;
    }

    public long getRequired() {
      return required;
    }

    public int getExperience() {
      return experience;
    }
  }
}