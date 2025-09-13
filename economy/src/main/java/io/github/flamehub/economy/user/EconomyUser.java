package io.github.flamehub.economy.user;

import dev.morphia.annotations.Entity;
import io.github.flamehub.commons.user.UserUpdatable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.UUID;

@Entity("economy_users")
public final class EconomyUser extends UserUpdatable {

  private BigDecimal money = BigDecimal.ZERO;

  public EconomyUser() {

  }

  public EconomyUser(final UUID uniqueId, final String name) {
    super(uniqueId, name);
  }

  public boolean hasEnough(final BigDecimal amount) throws ArithmeticException {
    return amount.compareTo(money) <= 0;
  }

  public void addMoney(final double amount) {
    setMoney(money.add(BigDecimal.valueOf(amount)));
  }

  public void removeMoney(final double amount) {
    setMoney(money.subtract(BigDecimal.valueOf(amount)));
  }

  public BigDecimal getMoney() {
    return money;
  }

  public void setMoney(final BigDecimal money) {
    this.money = money.setScale(6, RoundingMode.CEILING);
  }
}
