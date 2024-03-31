package io.github.flamehub.essentials.vanish;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;

final class VanishPlaceholder extends PlaceholderExpansion {

    private final VanishedEntryCache vanishedEntryCache;

    public VanishPlaceholder(final VanishedEntryCache vanishedEntryCache) {
        this.vanishedEntryCache = vanishedEntryCache;
    }

    @Override
    public @NotNull String getIdentifier() {
        return "isvanished";
    }

    @Override
    public @NotNull String getAuthor() {
        return "opalka";
    }

    @Override
    public @NotNull String getVersion() {
        return "0.1";
    }

    @Override
    public String onRequest(final OfflinePlayer player, @NotNull final String params) {
        return vanishedEntryCache.isVanished(player.getUniqueId()) ? "&8[&#61c1dfV&8]&r " : "";
    }
}
