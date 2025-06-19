package io.github.flamehub.coinflip;

import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;
import org.jetbrains.annotations.Nullable;

public final class CoinFlipGame implements Serializable {

  private UUID id = UUID.randomUUID();

  private CoinFlipPlayer creator;

  @Nullable
  private CoinFlipPlayer opponent = null;

  private Instant createDate = Instant.now();

  private int bet;

  public CoinFlipGame() {
  }

  public CoinFlipGame(final CoinFlipPlayer creator, final int bet) {
    this.creator = creator;
    this.bet = bet;
  }

  public UUID getId() {
    return id;
  }

  public CoinFlipPlayer getCreator() {
    return creator;
  }

  public @Nullable CoinFlipPlayer getOpponent() {
    return opponent;
  }

  public int getBet() {
    return bet;
  }

  public void setOpponent(final @Nullable CoinFlipPlayer opponent) {
    this.opponent = opponent;
  }

  public Instant getCreateDate() {
    return createDate;
  }
}
