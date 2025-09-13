package io.github.flamehub.wallet.user;

import dev.morphia.annotations.Entity;
import io.github.flamehub.commons.user.User;
import java.math.BigDecimal;
import java.util.UUID;

@Entity("wallet_users")
public final class WalletUser extends User {

  private BigDecimal money;

  public WalletUser() {
  }

  public WalletUser(final UUID uniqueId, final String name) {
    super(uniqueId, name);
    money = new BigDecimal("0.0");
  }

  public BigDecimal getMoney() {
    return money;
  }

  public void setMoney(final BigDecimal money) {
    this.money = money;
  }

  public boolean hasEnough(final BigDecimal amount) {
    return amount.compareTo(money) <= 0;
  }

  public void subtractMoney(final BigDecimal amount) {
    money = money.subtract(amount);
  }

  public void addMoney(final BigDecimal amount) {
    money = money.add(amount);
  }
}
