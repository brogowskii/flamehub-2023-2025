package io.github.flamehub.wallet;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;
import io.github.flamehub.wallet.api.WalletUser;
import io.github.flamehub.wallet.user.WalletUserCache;

public final class WalletPlaceholder extends PlaceholderExpansion {

    private final WalletUserCache walletUserCache;

    public WalletPlaceholder(WalletUserCache walletUserCache) {
        this.walletUserCache = walletUserCache;
    }

    @Override
    public @NotNull String getIdentifier() {
        return "wallet";
    }

    @Override
    public @NotNull String getAuthor() {
        return "MarcinOpałka";
    }

    @Override
    public @NotNull String getVersion() {
        return "0.1";
    }

    @Override
    public String onRequest(OfflinePlayer player, @NotNull String params) {

        WalletUser walletUser = this.walletUserCache.findByUniqueId(player.getUniqueId());
        if (walletUser == null) {
            return "";
        }

        if (params.equalsIgnoreCase("money")) {
            return String.valueOf(walletUser.getMoney().doubleValue());
        }

        return "";
    }
}
