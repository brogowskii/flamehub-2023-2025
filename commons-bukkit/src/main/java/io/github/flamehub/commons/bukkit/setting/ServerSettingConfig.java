package io.github.flamehub.commons.bukkit.setting;

import io.github.flamehub.commons.config.FlameConfig;
import io.github.flamehub.commons.config.FlameConfigProperties;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.bukkit.entity.Player;

@FlameConfigProperties(name = "serverSettings.json")
public final class ServerSettingConfig extends FlameConfig {

  private List<ServerSetting> settings = new ArrayList<>();
  private Set<String> disabledSettings = new HashSet<>();

  public ServerSettingConfig() {
  }

  public boolean isDisabled(final Player player, final String id) {
    return !player.hasPermission("serversettings.bypass") && disabledSettings.contains(id);
  }

  public List<ServerSetting> getSettings() {
    return settings;
  }

  public Set<String> getDisabledSettings() {
    return disabledSettings;
  }
}
