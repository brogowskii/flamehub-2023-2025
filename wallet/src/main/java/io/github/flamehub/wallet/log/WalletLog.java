package io.github.flamehub.wallet.log;

import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;

import java.util.Date;
import java.util.UUID;

@Entity("wallet_logs")
public final class WalletLog {

    @Id
    private UUID id = UUID.randomUUID();

    private WalletLogAction action;
    private Date date;
    private String adminName;
    private double amount;

    private String buyerName;
    private String boughtItem;

    public WalletLog() {
    }

    public WalletLog(WalletLogAction action) {
        this.action = action;
        this.date = new Date();
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public void setBoughtItem(String boughtItem) {
        this.boughtItem = boughtItem;
    }

    public WalletLogAction getAction() {
        return action;
    }

    public double getAmount() {
        return amount;
    }

    public String getBoughtItem() {
        return boughtItem;
    }

    public String getAdminName() {
        return adminName;
    }

    public String getBuyerName() {
        return buyerName;
    }

    public void setAdminName(String adminName) {
        this.adminName = adminName;
    }

    public void setBuyerName(String buyerName) {
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
