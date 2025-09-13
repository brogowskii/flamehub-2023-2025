package io.github.flamehub.commons.bukkit.user.event;

import io.github.flamehub.commons.user.User;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class AsyncPlayerJoinEvent extends Event {

  private static final HandlerList handlers = new HandlerList();

  private final Player player;
  private final User user;
  private final boolean firstJoin;

  public AsyncPlayerJoinEvent(final Player player, final User user, final boolean firstJoin) {
    super(true);
    this.player = player;
    this.user = user;
    this.firstJoin = firstJoin;
  }

  public static HandlerList getHandlerList() {
    return handlers;
  }

  public boolean isFirstJoin() {
    return firstJoin;
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