package io.github.flamehub.coinflip;

import java.util.UUID;
import org.jetbrains.annotations.Nullable;

public final class CoinFlipGame {

  private final UUID id = UUID.randomUUID();

  private final CoinFlipPlayer creator;

  @Nullable
  private CoinFlipPlayer opponent = null;

  private final int bet;


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
}
