package io.github.flamehub.commons.bukkit.placeholder;

import io.github.flamehub.commons.util.TimeUtil;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.time.Instant;

public final class PlayerPlaceholder extends PlaceholderExpansion {
    @Override
    public @NotNull String getIdentifier() {
        return "commonplayer";
    }

    @Override
    public @NotNull String getAuthor() {
        return "MarcinOpalka";
    }

    @Override
    public @NotNull String getVersion() {
        return "1.0";
    }

    @Override
    public String onRequest(OfflinePlayer offlinePlayer, @NotNull String params) {

        switch (params) {
            case "nickname" -> {
                return offlinePlayer.getName();
            }
            case "ping" -> {
                Player player = offlinePlayer.getPlayer();
                if (player == null) {
                    return "";
                }
                return String.valueOf(player.getPing());
            }
            case "date" -> {
                return TimeUtil.formatDate(Instant.now());
            }
        }

        return "";
    }
}
