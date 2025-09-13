package io.github.flamehub.commons.bukkit.setting;

public final class ServerSetting {

  private String id;
  private String friendlyName;

  public ServerSetting() {
  }

  public ServerSetting(final String id, final String friendlyName) {
    this.id = id;
    this.friendlyName = friendlyName;
  }

  public String getId() {
    return id;
  }

  public String getFriendlyName() {
    return friendlyName;
  }

}
