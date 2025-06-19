package io.github.flamehub.coinflip.user;

import dev.morphia.annotations.Entity;
import io.github.flamehub.commons.user.User;
import java.util.UUID;

@Entity("coinflip_users")
public final class CoinFlipUser extends User {

  private int deposit;

  public CoinFlipUser() {
  }

  public CoinFlipUser(final UUID uniqueId, final String name) {
    super(uniqueId, name);
  }


  public int getDeposit() {
    return deposit;
  }

  public void setDeposit(final int deposit) {
    this.deposit = deposit;
  }
}
