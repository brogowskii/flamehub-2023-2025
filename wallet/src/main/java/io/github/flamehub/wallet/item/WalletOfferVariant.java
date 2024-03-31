package io.github.flamehub.wallet.item;

import java.io.Serializable;
import java.util.List;

public final class WalletOfferVariant implements Serializable {

    private final String name;
    private final List<String> lore;
    private final double cost;
    private final int amount;
    private final List<String> broadcast;
    private final String command;

    public WalletOfferVariant(String name, List<String> lore, double cost, int amount, List<String> broadcast, String command) {
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