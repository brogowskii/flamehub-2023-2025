package io.github.flamehub.economy.user;

import io.github.flamehub.commons.messenger.packet.PacketHandler;
import java.math.BigDecimal;

public final class EconomyUserUpdateHandler {

  private final EconomyUserFacade economyUserFacade;

  public EconomyUserUpdateHandler(final EconomyUserFacade economyUserFacade) {
    this.economyUserFacade = economyUserFacade;
  }

  @PacketHandler
  public void handle(EconomyUserUpdate update) {
    EconomyUser economyUser = economyUserFacade.findByUniqueId(update.getUniqueId());
    if (economyUser == null) {
      return;
    }

    switch (update.getType()) {
      case ADD -> economyUser.addMoney(update.getMoney());
      case REMOVE -> economyUser.setMoney(
          economyUser.getMoney().subtract(BigDecimal.valueOf(update.getMoney())));
      case SET -> economyUser.setMoney(BigDecimal.valueOf(update.getMoney()));
    }

    economyUserFacade.save(economyUser);
  }


}
