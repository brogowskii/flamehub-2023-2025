package io.github.flamehub.wallet.log;

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

  public WalletLogBuilder action(final WalletLogAction action) {
    this.action = action;
    return this;
  }

  public WalletLogBuilder adminName(final String adminName) {
    this.adminName = adminName;
    return this;
  }

  public WalletLogBuilder amount(final double amount) {
    this.amount = amount;
    return this;
  }

  public WalletLogBuilder buyerName(final String buyerName) {
    this.buyerName = buyerName;
    return this;
  }

  public WalletLogBuilder boughtItem(final String boughtItem) {
    this.boughtItem = boughtItem;
    return this;
  }

  public WalletLog build() {
    return new WalletLog(action, adminName, amount, buyerName, boughtItem);
  }


}
