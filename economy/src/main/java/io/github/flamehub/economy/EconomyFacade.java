package io.github.flamehub.economy;

import io.github.flamehub.economy.user.EconomyUser;
import io.github.flamehub.economy.user.EconomyUserFacade;
import io.github.flamehub.economy.user.EconomyUserUpdateType;
import java.util.UUID;
import org.bukkit.entity.Player;

public final class EconomyFacade {

  private final EconomyUserFacade economyUserFacade;

  public EconomyFacade(final EconomyUserFacade economyUserFacade) {
    this.economyUserFacade = economyUserFacade;
  }

  public void deposit(final UUID uuid, final double money) {
    final EconomyUser economyUser = economyUserFacade.findByUniqueId(uuid);
    if (economyUser == null) {
      return;
    }

    economyUser.addMoney(money);
    economyUserFacade.update(economyUser, money, EconomyUserUpdateType.ADD);

  }

  public void deposit(final String name, final double money) {
    final EconomyUser economyUser = economyUserFacade.findByName(name);
    if (economyUser == null) {
      return;
    }

    economyUser.addMoney(money);
    economyUserFacade.update(economyUser, money, EconomyUserUpdateType.ADD);

  }

  public void withdraw(final UUID uuid, final double money) {
    final EconomyUser economyUser = economyUserFacade.findByUniqueId(uuid);
    if (economyUser == null) {
      return;
    }

    economyUser.removeMoney(money);
    economyUserFacade.update(economyUser, money, EconomyUserUpdateType.REMOVE);

  }

  public void withdraw(final String name, final double money) {
    final EconomyUser economyUser = economyUserFacade.findByName(name);
    if (economyUser == null) {
      return;
    }

    economyUser.removeMoney(money);
    economyUserFacade.update(economyUser, money, EconomyUserUpdateType.REMOVE);

  }

  public double getBalance(final Player player) {
    return getBalance(player.getUniqueId());
  }

  public double getBalance(final UUID uuid) {
    final EconomyUser economyUser = economyUserFacade.findByUniqueId(uuid);
    if (economyUser == null) {
      return 0;
    }

    return economyUser.getMoney().doubleValue();
  }

}
