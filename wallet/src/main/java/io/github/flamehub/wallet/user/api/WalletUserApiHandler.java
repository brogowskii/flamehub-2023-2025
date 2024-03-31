package io.github.flamehub.wallet.user.api;

import io.github.flamehub.commons.messenger.packet.PacketHandler;
import io.github.flamehub.wallet.api.WalletUserMoneyChange;
import io.github.flamehub.wallet.api.WalletUser;
import io.github.flamehub.wallet.user.WalletUserCache;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.math.BigDecimal;

public final class WalletUserApiHandler {

    private final WalletUserCache walletUserCache;

    public WalletUserApiHandler(WalletUserCache walletUserCache) {
        this.walletUserCache = walletUserCache;
    }

    @PacketHandler
    public void handle(WalletUserMoneyChange packet) {
        Player player = Bukkit.getPlayer(packet.getName());
        if (player == null) {
            return;
        }

        WalletUser walletUser = this.walletUserCache.findByName(packet.getName());
        if (walletUser == null) {
            return;
        }

        walletUser.setMoney(BigDecimal.valueOf(packet.getValue()));

    }

}
