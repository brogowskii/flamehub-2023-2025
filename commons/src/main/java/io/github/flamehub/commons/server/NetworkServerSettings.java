package io.github.flamehub.commons.server;


import java.util.ArrayList;
import java.util.List;

public final class NetworkServerSettings {

  private boolean frozen;
  private int playersLimit = 300;

  private boolean whitelist = false;
  private List<String> whitelistedPlayers = new ArrayList<>();

  public NetworkServerSettings() {
  }

  public boolean isFrozen() {
    return frozen;
  }

  public void setFrozen(final boolean frozen) {
    this.frozen = frozen;
  }

  public int getPlayersLimit() {
    return playersLimit;
  }

  public void setPlayersLimit(final int playersLimit) {
    this.playersLimit = playersLimit;
  }

  public boolean isWhitelist() {
    return whitelist;
  }

  public void setWhitelist(final boolean whitelist) {
    this.whitelist = whitelist;
  }

  public List<String> getWhitelistedPlayers() {
    return whitelistedPlayers;
  }

}
