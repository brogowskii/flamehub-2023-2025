package io.github.flamehub.timeplayed.user;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public final class SpendTimeIncrementEvent extends Event {
    private static final HandlerList handlers = new HandlerList();

    private final TimePlayedUser user;

    public SpendTimeIncrementEvent(TimePlayedUser user) {
        super(true);
        this.user = user;

    }

    public TimePlayedUser getUser() {
        return user;
    }

    public @NotNull HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
