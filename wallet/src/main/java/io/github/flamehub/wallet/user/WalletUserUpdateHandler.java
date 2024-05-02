package io.github.flamehub.wallet.user;

import io.github.flamehub.commons.messenger.packet.PacketHandler;
import io.github.flamehub.wallet.api.WalletUser;
import io.github.flamehub.wallet.api.WalletUserRepository;

import java.math.BigDecimal;

public final class WalletUserUpdateHandler {


    private final WalletUserCache walletUserCache;
    private final WalletUserRepository walletUserRepository;

    public WalletUserUpdateHandler(WalletUserCache walletUserCache, WalletUserRepository walletUserRepository) {
        this.walletUserCache = walletUserCache;
        this.walletUserRepository = walletUserRepository;
    }

    @PacketHandler
    public void handle(WalletUserUpdate update) {

        WalletUser walletUser = this.walletUserCache.findByUniqueId(update.getUniqueId());
        if (walletUser == null) {
            return;
        }

        switch (update.getType()) {
            case ADD -> walletUser.addMoney(BigDecimal.valueOf(update.getAmount()));
            case REMOVE -> walletUser.setMoney(walletUser.getMoney().subtract(BigDecimal.valueOf(update.getAmount())));
            case SET -> walletUser.setMoney(BigDecimal.valueOf(update.getAmount()));
        }

        this.walletUserRepository.save(walletUser);

    }


}
