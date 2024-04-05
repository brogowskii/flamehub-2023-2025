package io.github.flamehub.essentials.vanish;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;

final class VanishPlaceholder extends PlaceholderExpansion {

    private final VanishFacade vanishFacade;

    VanishPlaceholder(VanishFacade vanishFacade) {
        this.vanishFacade = vanishFacade;
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
        return vanishFacade.isVanished(player.getUniqueId()) ? "&8[&#61c1dfV&8]&r " : "";
    }
}
