package io.github.flamehub.mines.mine;

import io.github.flamehub.commons.util.TimeUtil;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;

public final class MinePlaceholder extends PlaceholderExpansion {

  private final MineConfig mineConfig;

  public MinePlaceholder(MineConfig mineConfig) {
    this.mineConfig = mineConfig;
  }

  @Override
  public @NotNull String getIdentifier() {
    return "mines";
  }

  @Override
  public @NotNull String getAuthor() {
    return "opalka";
  }

  @Override
  public @NotNull String getVersion() {
    return "1.0";
  }

  @Override
  public String onRequest(OfflinePlayer player, @NotNull String params) {
    Mine byId = this.mineConfig.findById(params);
    if (byId == null) {
      return "null";
    }

    long l = byId.getLastTimeGenerate();
    return TimeUtil.formatTimeSimple(l - System.currentTimeMillis());
  }
}
