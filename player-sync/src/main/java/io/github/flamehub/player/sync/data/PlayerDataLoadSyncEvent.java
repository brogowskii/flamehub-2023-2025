package io.github.flamehub.player.sync.data;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class PlayerDataLoadSyncEvent extends Event {

  private static final HandlerList handlers = new HandlerList();

  private final Player player;

  public PlayerDataLoadSyncEvent(Player player) {
    super(false);
    this.player = player;
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

}