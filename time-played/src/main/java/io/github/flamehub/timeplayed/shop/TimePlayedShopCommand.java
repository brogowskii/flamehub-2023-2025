package io.github.flamehub.timeplayed.shop;

import dev.rollczi.litecommands.annotations.command.Command;
import dev.rollczi.litecommands.annotations.context.Context;
import dev.rollczi.litecommands.annotations.execute.Execute;
import io.github.flamehub.timeplayed.user.TimePlayedUserCache;
import org.bukkit.entity.Player;

@Command(name = "timeplayedshop", aliases = {"sklepzaczas", "timeshop", "czas"})
public final class TimePlayedShopCommand {

  private final TimePlayedShopConfig timePlayedShopConfig;
  private final TimePlayedUserCache timePlayedUserCache;

  public TimePlayedShopCommand(TimePlayedShopConfig timePlayedShopConfig,
      TimePlayedUserCache timePlayedUserCache) {
    this.timePlayedShopConfig = timePlayedShopConfig;
    this.timePlayedUserCache = timePlayedUserCache;
  }

  @Execute
  void execute(@Context Player player) {
    TimePlayedShopGui timePlayedShopGui = new TimePlayedShopGui(timePlayedShopConfig,
        timePlayedUserCache);
    timePlayedShopGui.open(player);
  }

}
