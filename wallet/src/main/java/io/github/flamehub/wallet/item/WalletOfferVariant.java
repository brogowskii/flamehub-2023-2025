package io.github.flamehub.wallet.item;

import java.io.Serializable;
import java.util.List;

public final class WalletOfferVariant implements Serializable {

  private String name;
  private List<String> lore;
  private double cost;
  private int amount;
  private List<String> broadcast;
  private String command;

  public WalletOfferVariant() {
  }

  public WalletOfferVariant(String name, List<String> lore, double cost, int amount,
      List<String> broadcast, String command) {
    this.name = name;
    this.lore = lore;
    this.cost = cost;
    this.amount = amount;
    this.broadcast = broadcast;
    this.command = command;
  }

  public String getName() {
    return name;
  }

  public double getCost() {
    return cost;
  }

  public int getAmount() {
    return amount;
  }

  public List<String> getLore() {
    return lore;
  }

  public List<String> getBroadcast() {
    return broadcast;
  }

  public String getCommand() {
    return command;
  }
}