package io.github.flamehub.wallet.log;

import java.util.Date;

public final class WalletLogBuilder {

  private WalletLogAction action;
  private String adminName;
  private double amount;

  private String buyerName;
  private String boughtItem;

  public WalletLogBuilder() {
  }

  public static WalletLogBuilder create() {
    return new WalletLogBuilder();
  }

  public WalletLogBuilder action(WalletLogAction action) {
    this.action = action;
    return this;
  }

  public WalletLogBuilder adminName(String adminName) {
    this.adminName = adminName;
    return this;
  }

  public WalletLogBuilder amount(double amount) {
    this.amount = amount;
    return this;
  }

  public WalletLogBuilder buyerName(String buyerName) {
    this.buyerName = buyerName;
    return this;
  }

  public WalletLogBuilder boughtItem(String boughtItem) {
    this.boughtItem = boughtItem;
    return this;
  }

  public WalletLog build() {
    return new WalletLog(action, adminName, amount, buyerName, boughtItem);
  }


}
