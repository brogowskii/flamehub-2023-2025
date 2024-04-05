package io.github.flamehub.crates;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class CrateOpenEvent extends Event {
    private static final HandlerList handlers = new HandlerList();

    private final Player player;
    private final String crateId;

    public CrateOpenEvent(Player player, String crateId) {
        super(false);
        this.player = player;
        this.crateId = crateId;
    }

    public @NotNull HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    public Player getPlayer() {
        return player;
    }

    public String getCrateId() {
        return crateId;
    }
}