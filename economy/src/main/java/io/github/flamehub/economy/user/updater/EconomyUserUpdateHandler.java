package io.github.flamehub.economy.user.updater;

import io.github.flamehub.commons.messenger.packet.PacketHandler;
import io.github.flamehub.economy.user.EconomyUser;
import io.github.flamehub.economy.user.EconomyUserCache;
import io.github.flamehub.economy.user.EconomyUserRepository;

import java.math.BigDecimal;

public final class EconomyUserUpdateHandler {

    private final EconomyUserCache economyUserCache;
    private final EconomyUserRepository walletUserRepository;

    public EconomyUserUpdateHandler(EconomyUserCache economyUserCache, EconomyUserRepository walletUserRepository) {
        this.economyUserCache = economyUserCache;
        this.walletUserRepository = walletUserRepository;
    }

    @PacketHandler
    public void handle(EconomyUserUpdate update) {
        EconomyUser economyUser = this.economyUserCache.findByUniqueId(update.getUniqueId());
        if (economyUser == null) {
            return;
        }

        economyUser.setMoney(BigDecimal.valueOf(update.getMoney()));
        this.walletUserRepository.save(economyUser);
    }


}
