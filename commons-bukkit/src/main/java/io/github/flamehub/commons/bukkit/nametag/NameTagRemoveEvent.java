package io.github.flamehub.commons.bukkit.nametag;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public final class NameTagRemoveEvent extends Event {
    private static final HandlerList handlers = new HandlerList();

    private final Player player;

    public NameTagRemoveEvent(Player player) {
        super(true);
        this.player = player;
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

}