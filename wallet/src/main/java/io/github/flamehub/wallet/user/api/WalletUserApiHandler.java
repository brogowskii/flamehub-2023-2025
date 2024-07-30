package io.github.flamehub.wallet.user.api;

import io.github.flamehub.commons.messenger.packet.PacketHandler;
import io.github.flamehub.wallet.api.WalletUser;
import io.github.flamehub.wallet.api.WalletUserMoneyChange;
import io.github.flamehub.wallet.api.WalletUserRepository;
import io.github.flamehub.wallet.user.WalletUserCache;
import java.math.BigDecimal;

public final class WalletUserApiHandler {

  private final WalletUserCache walletUserCache;
  private final WalletUserRepository walletUserRepository;

  public WalletUserApiHandler(WalletUserCache walletUserCache,
      WalletUserRepository walletUserRepository) {
    this.walletUserCache = walletUserCache;
    this.walletUserRepository = walletUserRepository;
  }

  @PacketHandler
  public void handle(WalletUserMoneyChange update) {

    WalletUser walletUser = this.walletUserCache.findByName(update.getName());
    if (walletUser == null) {
      return;
    }

    switch (update.getType()) {
      case ADD -> walletUser.addMoney(BigDecimal.valueOf(update.getValue()));
      case REMOVE -> walletUser.setMoney(
          walletUser.getMoney().subtract(BigDecimal.valueOf(update.getValue())));
      case SET -> walletUser.setMoney(BigDecimal.valueOf(update.getValue()));
    }

    this.walletUserRepository.save(walletUser);

  }


}
