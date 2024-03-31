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

    public EconomyUser(UUID uniqueId, String name) {
        super(uniqueId, name);
    }

    public boolean hasEnough(BigDecimal amount) throws ArithmeticException {
        return amount.compareTo(this.money) <= 0;
    }

    public void addMoney(double amount) {
        this.setMoney(this.money.add(BigDecimal.valueOf(amount)));
    }

    public void removeMoney(double amount) {
        this.setMoney(this.money.subtract(BigDecimal.valueOf(amount)));
    }

    public void setMoney(BigDecimal money) {
        this.money = money.setScale(6, RoundingMode.CEILING);
    }

    public BigDecimal getMoney() {
        return money;
    }
}
