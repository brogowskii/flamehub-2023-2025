package io.github.flamehub.afkzone;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;

public final class AfkZoneReward implements Serializable {

  @JsonIgnore
  private Map<UUID, BossBar> bossBarMap;

  @JsonIgnore
  private Map<UUID, Long> uuidInstantMap;

  private String id;
  private int seconds;

  private String command;

  private String title;
  private BarColor color;
  private BarStyle style;

  public AfkZoneReward() {
  }

  public AfkZoneReward(String id, int seconds, String command, String title, BarColor color,
      BarStyle style) {
    this.id = id;
    this.seconds = seconds;
    this.command = command;
    this.title = title;
    this.color = color;
    this.style = style;
  }

  @JsonIgnore
  public Map<UUID, BossBar> getBossBarMap() {
    if (bossBarMap == null) {
      bossBarMap = new HashMap<>();
    }

    return bossBarMap;
  }

  @JsonIgnore
  public Map<UUID, Long> getUuidInstantMap() {
    if (this.uuidInstantMap == null) {
      uuidInstantMap = new HashMap<>();
    }

    return uuidInstantMap;
  }

  public String getId() {
    return id;
  }

  public int getSeconds() {
    return seconds;
  }

  public String getCommand() {
    return command;
  }

  public String getTitle() {
    return title;
  }

  public BarColor getColor() {
    return color;
  }

  public BarStyle getStyle() {
    return style;
  }
}
