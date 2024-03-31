package io.github.flamehub.commons.bukkit.user.event;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;
import io.github.flamehub.commons.user.User;

public final class PlayerChangeNameEvent extends Event {

    private static final HandlerList handlers = new HandlerList();
    private final User user;
    private final String oldName;
    private final String newName;

    public PlayerChangeNameEvent(User user, String oldName, String newName) {
        super(true);
        this.user = user;
        this.oldName = oldName;
        this.newName = newName;
    }


    public @NotNull HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    public User getUser() {
        return user;
    }

    public String getOldName() {
        return oldName;
    }

    public String getNewName() {
        return newName;
    }
}
