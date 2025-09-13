package io.github.flamehub.wallet.log;

import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;
import java.util.Date;
import java.util.UUID;

@Entity("wallet_logs")
public final class WalletLog {

  @Id
  private final UUID id = UUID.randomUUID();

  private WalletLogAction action;
  private Date date;
  private String adminName;
  private double amount;

  private String buyerName;
  private String boughtItem;

  public WalletLog() {
  }

  public WalletLog(final WalletLogAction action, final String adminName,
      final double amount,
      final String buyerName, final String boughtItem) {
    this.action = action;
    date = new Date();
    this.adminName = adminName;
    this.amount = amount;
    this.buyerName = buyerName;
    this.boughtItem = boughtItem;
  }

  public WalletLogAction getAction() {
    return action;
  }

  public double getAmount() {
    return amount;
  }

  public void setAmount(final double amount) {
    this.amount = amount;
  }

  public String getBoughtItem() {
    return boughtItem;
  }

  public void setBoughtItem(final String boughtItem) {
    this.boughtItem = boughtItem;
  }

  public String getAdminName() {
    return adminName;
  }

  public void setAdminName(final String adminName) {
    this.adminName = adminName;
  }

  public String getBuyerName() {
    return buyerName;
  }

  public void setBuyerName(final String buyerName) {
    this.buyerName = buyerName;
  }

  public Date getDate() {
    return date;
  }

  @Override
  public String toString() {
    return "WalletLog{" +
        "action=" + action +
        ", adminName='" + adminName + '\'' +
        ", amount=" + amount +
        ", buyerName='" + buyerName + '\'' +
        ", boughtItem='" + boughtItem + '\'' +
        '}';
  }
}
