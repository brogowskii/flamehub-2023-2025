package io.github.flamehub.economy;

import io.github.flamehub.economy.user.EconomyUser;
import io.github.flamehub.economy.user.EconomyUserFacade;
import io.github.flamehub.economy.user.EconomyUserUpdateType;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.OfflinePlayer;

class EconomyVaultProvider implements Economy {

  private final EconomyUserFacade economyUserFacade;

  EconomyVaultProvider(final EconomyUserFacade economyUserFacade) {
    this.economyUserFacade = economyUserFacade;
  }

  @Override
  public boolean isEnabled() {
    return false;
  }

  @Override
  public String getName() {
    return null;
  }

  @Override
  public boolean hasBankSupport() {
    return false;
  }

  @Override
  public int fractionalDigits() {
    return -1;
  }

  @Override
  public String format(final double v) {
    return new BigDecimal(v).setScale(2, RoundingMode.CEILING).toString();
  }

  @Override
  public String currencyNamePlural() {
    return currencyNameSingular();
  }

  @Override
  public String currencyNameSingular() {
    return "$";
  }

  @Override
  public boolean hasAccount(final String s) {
    return false;
  }

  @Override
  public boolean hasAccount(final OfflinePlayer offlinePlayer) {
    return false;
  }

  @Override
  public boolean hasAccount(final String s, final String s1) {
    return false;
  }

  @Override
  public boolean hasAccount(final OfflinePlayer offlinePlayer, final String s) {
    return false;
  }

  @Override
  public double getBalance(final String s) {

    final EconomyUser economyUser = economyUserFacade.findByName(s);
    return economyUser.getMoney().doubleValue();
  }

  @Override
  public double getBalance(final OfflinePlayer offlinePlayer) {

    final EconomyUser economyUser = economyUserFacade.findByUniqueId(offlinePlayer.getUniqueId());
    return economyUser.getMoney().doubleValue();

  }

  @Override
  public double getBalance(final String s, final String s1) {
    return getBalance(s);
  }

  @Override
  public double getBalance(final OfflinePlayer offlinePlayer, final String s) {
    return getBalance(offlinePlayer);
  }

  @Override
  public boolean has(final String s, final double v) {

    final EconomyUser economyUser = economyUserFacade.findByName(s);
    return economyUser.hasEnough(BigDecimal.valueOf(v));

  }

  @Override
  public boolean has(final OfflinePlayer offlinePlayer, final double v) {

    final EconomyUser economyUser = economyUserFacade.findByUniqueId(offlinePlayer.getUniqueId());
    return economyUser.hasEnough(BigDecimal.valueOf(v));

  }

  @Override
  public boolean has(final String s, final String s1, final double v) {
    return has(s, v);
  }

  @Override
  public boolean has(final OfflinePlayer offlinePlayer, final String s, final double v) {
    return has(offlinePlayer, v);
  }

  @Override
  public EconomyResponse withdrawPlayer(final String playerName, final double amount) {
    if (playerName == null) {
      return new EconomyResponse(0, 0, EconomyResponse.ResponseType.FAILURE,
          "Nazwa gracza nie może być nullem.");
    }

    if (amount < 0) {
      return new EconomyResponse(0, 0, EconomyResponse.ResponseType.FAILURE,
          "Nie można wypłacić ujemnej kwoty.");
    }

    final EconomyUser economyUser = economyUserFacade.findByName(playerName);
    if (economyUser == null) {
      return new EconomyResponse(0, 0, EconomyResponse.ResponseType.FAILURE,
          "Ten gracz nie istnieje w bazie danych!");
    }

    economyUser.removeMoney(amount);
    economyUserFacade.update(economyUser, amount, EconomyUserUpdateType.REMOVE);

    return new EconomyResponse(amount, economyUser.getMoney().doubleValue(),
        EconomyResponse.ResponseType.SUCCESS, null);
  }

  @Override
  public EconomyResponse withdrawPlayer(final OfflinePlayer offlinePlayer, final double amount) {
    if (offlinePlayer == null) {
      return new EconomyResponse(0, 0, EconomyResponse.ResponseType.FAILURE,
          "OfflinePlayer cannot be null!");
    }
    if (amount < 0) {
      return new EconomyResponse(0, 0, EconomyResponse.ResponseType.FAILURE,
          "Nie można wypłacić ujemnej kwoty.");
    }

    final EconomyUser economyUser = economyUserFacade.findByUniqueId(offlinePlayer.getUniqueId());
    if (economyUser == null) {
      return new EconomyResponse(0, 0, EconomyResponse.ResponseType.FAILURE,
          "Ten gracz nie istnieje w bazie danych!");
    }

    economyUser.removeMoney(amount);
    economyUserFacade.update(economyUser, amount, EconomyUserUpdateType.REMOVE);

    return new EconomyResponse(amount, economyUser.getMoney().doubleValue(),
        EconomyResponse.ResponseType.SUCCESS, null);
  }

  @Override
  public EconomyResponse withdrawPlayer(final String s, final String s1, final double v) {
    return withdrawPlayer(s, v);
  }

  @Override
  public EconomyResponse withdrawPlayer(final OfflinePlayer offlinePlayer, final String s, final double v) {
    return withdrawPlayer(offlinePlayer, v);
  }

  @Override
  public EconomyResponse depositPlayer(final String playerName, final double amount) {
    if (playerName == null) {
      return new EconomyResponse(0, 0, EconomyResponse.ResponseType.FAILURE,
          "Nazwa gracza nie może być nullem.");
    }
    if (amount < 0) {
      return new EconomyResponse(0, 0, EconomyResponse.ResponseType.FAILURE,
          "Nie można wpłacić ujemnej kwoty.");
    }

    final EconomyUser economyUser = economyUserFacade.findByName(playerName);
    if (economyUser == null) {
      return new EconomyResponse(0, 0, EconomyResponse.ResponseType.FAILURE,
          "Ten gracz nie istnieje w bazie danych!");
    }

    economyUser.addMoney(amount);
    economyUserFacade.update(economyUser, amount, EconomyUserUpdateType.ADD);

    return new EconomyResponse(amount, economyUser.getMoney().doubleValue(),
        EconomyResponse.ResponseType.SUCCESS, null);
  }

  @Override
  public EconomyResponse depositPlayer(final OfflinePlayer offlinePlayer, final double amount) {
    if (offlinePlayer == null) {
      return new EconomyResponse(0, 0, EconomyResponse.ResponseType.FAILURE,
          "OfflinePlayer cannot be null.");
    }
    if (amount < 0) {
      return new EconomyResponse(0, 0, EconomyResponse.ResponseType.FAILURE,
          "Nie można wpłacić ujemnej kwoty.");
    }

    final EconomyUser economyUser = economyUserFacade.findByUniqueId(offlinePlayer.getUniqueId());
    if (economyUser == null) {
      return new EconomyResponse(0, 0, EconomyResponse.ResponseType.FAILURE,
          "Ten gracz nie istnieje w bazie danych!");
    }

    economyUser.addMoney(amount);
    economyUserFacade.update(economyUser, amount, EconomyUserUpdateType.ADD);

    return new EconomyResponse(amount, economyUser.getMoney().doubleValue(),
        EconomyResponse.ResponseType.SUCCESS, null);
  }

  @Override
  public EconomyResponse depositPlayer(final String s, final String s1, final double v) {
    return depositPlayer(s, v);
  }

  @Override
  public EconomyResponse depositPlayer(final OfflinePlayer offlinePlayer, final String s, final double v) {
    return depositPlayer(offlinePlayer, v);
  }

  @Override
  public EconomyResponse createBank(final String s, final String s1) {
    return null;
  }

  @Override
  public EconomyResponse createBank(final String s, final OfflinePlayer offlinePlayer) {
    return null;
  }

  @Override
  public EconomyResponse deleteBank(final String s) {
    return null;
  }

  @Override
  public EconomyResponse bankBalance(final String s) {
    return null;
  }

  @Override
  public EconomyResponse bankHas(final String s, final double v) {
    return null;
  }

  @Override
  public EconomyResponse bankWithdraw(final String s, final double v) {
    return null;
  }

  @Override
  public EconomyResponse bankDeposit(final String s, final double v) {
    return null;
  }

  @Override
  public EconomyResponse isBankOwner(final String s, final String s1) {
    return null;
  }

  @Override
  public EconomyResponse isBankOwner(final String s, final OfflinePlayer offlinePlayer) {
    return null;
  }

  @Override
  public EconomyResponse isBankMember(final String s, final String s1) {
    return null;
  }

  @Override
  public EconomyResponse isBankMember(final String s, final OfflinePlayer offlinePlayer) {
    return null;
  }

  @Override
  public List<String> getBanks() {
    return null;
  }

  @Override
  public boolean createPlayerAccount(final String s) {
    return false;
  }

  @Override
  public boolean createPlayerAccount(final OfflinePlayer offlinePlayer) {
    return false;
  }

  @Override
  public boolean createPlayerAccount(final String s, final String s1) {
    return false;
  }

  @Override
  public boolean createPlayerAccount(final OfflinePlayer offlinePlayer, final String s) {
    return false;
  }
}
