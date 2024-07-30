package io.github.flamehub.commons.bukkit.user.event;

import io.github.flamehub.commons.user.User;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class UserQuitEvent extends Event {

  private static final HandlerList handlers = new HandlerList();

  private final Player player;
  private final User user;

  public UserQuitEvent(Player player, User user) {
    super(false);
    this.player = player;
    this.user = user;
  }

  public static HandlerList getHandlerList() {
    return handlers;
  }

  public @NotNull HandlerList getHandlers() {
    return handlers;
  }

  public Player getPlayer() {
    return player;
  }

  public User getUser() {
    return user;
  }
}