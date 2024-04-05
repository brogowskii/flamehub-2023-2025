package io.github.flamehub.economy;

import io.github.flamehub.economy.user.EconomyUserFacade;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import io.github.flamehub.commons.bukkit.util.NumberConverter;
import io.github.flamehub.economy.user.EconomyUser;

final class EconomyPlaceholder extends PlaceholderExpansion {

    private final EconomyUserFacade economyUserFacade;

    EconomyPlaceholder(final EconomyUserFacade economyUserFacade) {
        this.economyUserFacade = economyUserFacade;
    }

    @Override
    public @NotNull String getIdentifier() {
        return "economy";
    }

    @Override
    public @NotNull String getAuthor() {
        return "opałka (nocek to cwel)";
    }

    @Override
    public @NotNull String getVersion() {
        return "0.1";
    }

    @Override
    public @Nullable String onRequest(OfflinePlayer player, @NotNull String params) {
        if (params.equals("money")) {
            EconomyUser economyUser = this.economyUserFacade.findByUniqueId(player.getUniqueId());
            if (economyUser == null) {
                return "";
            }

            return NumberConverter.convertNumber(economyUser.getMoney().doubleValue());
        }

        return "";
    }
}
