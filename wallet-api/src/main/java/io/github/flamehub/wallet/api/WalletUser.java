package io.github.flamehub.wallet.api;

import dev.morphia.annotations.Entity;
import io.github.flamehub.commons.user.User;
import java.math.BigDecimal;
import java.util.UUID;

@Entity("wallet_users")
public final class WalletUser extends User {

  private BigDecimal money;

  public WalletUser() {
  }

  public WalletUser(UUID uniqueId, String name) {
    super(uniqueId, name);
    this.money = new BigDecimal("0.0");
  }

  public BigDecimal getMoney() {
    return money;
  }

  public void setMoney(BigDecimal money) {
    this.money = money;
  }

  public boolean hasEnough(BigDecimal amount) {
    return amount.compareTo(this.money) <= 0;
  }

  public void subtractMoney(BigDecimal amount) {
    money = money.subtract(amount);
  }

  public void addMoney(BigDecimal amount) {
    money = money.add(amount);
  }
}
