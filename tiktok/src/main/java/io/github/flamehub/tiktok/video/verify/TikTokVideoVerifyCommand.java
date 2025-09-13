package io.github.flamehub.tiktok.video.verify;

import dev.rollczi.litecommands.annotations.command.Command;
import dev.rollczi.litecommands.annotations.context.Context;
import dev.rollczi.litecommands.annotations.execute.Execute;
import dev.rollczi.litecommands.annotations.permission.Permission;
import io.github.flamehub.commons.bukkit.dispatcher.FlameDispatcher;
import io.github.flamehub.commons.messenger.RedisMessenger;
import io.github.flamehub.tiktok.user.TikTokUserCache;
import org.bukkit.entity.Player;

@Command(name = "tiktokverify", aliases = "ttverify")
@Permission("server.commands.tiktokverify")
public final class TikTokVideoVerifyCommand {

  private final FlameDispatcher flameDispatcher;
  private final RedisMessenger redisMessenger;
  private final TikTokUserCache tikTokUserCache;
  private final TikTokVideoVerifyCache tikTokVideoVerifyCache;
  private final TikTokVideoVerifyRepository tikTokVideoVerifyRepository;

  public TikTokVideoVerifyCommand(
      final FlameDispatcher flameDispatcher,
      final RedisMessenger redisMessenger,
      final TikTokUserCache tikTokUserCache,
      final TikTokVideoVerifyCache tikTokVideoVerifyCache,
      final TikTokVideoVerifyRepository tikTokVideoVerifyRepository) {
    this.flameDispatcher = flameDispatcher;
    this.redisMessenger = redisMessenger;
    this.tikTokUserCache = tikTokUserCache;
    this.tikTokVideoVerifyCache = tikTokVideoVerifyCache;
    this.tikTokVideoVerifyRepository = tikTokVideoVerifyRepository;
  }

  @Execute
  void exec(@Context final Player player) {
    final TikTokVideoVerifyGui gui = new TikTokVideoVerifyGui(flameDispatcher, redisMessenger,
        tikTokUserCache, tikTokVideoVerifyCache,
        tikTokVideoVerifyRepository);
    gui.open(player, 1);
  }

}
