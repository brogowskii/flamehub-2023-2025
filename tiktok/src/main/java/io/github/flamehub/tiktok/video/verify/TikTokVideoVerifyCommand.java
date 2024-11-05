package io.github.flamehub.tiktok.video.verify;

import dev.rollczi.litecommands.annotations.command.Command;
import dev.rollczi.litecommands.annotations.context.Context;
import dev.rollczi.litecommands.annotations.execute.Execute;
import dev.rollczi.litecommands.annotations.permission.Permission;
import io.github.flamehub.commons.messenger.RedisMessenger;
import org.bukkit.entity.Player;

@Command(name = "tiktokverify", aliases = {"ttverify"})
@Permission("server.commands.tiktokverify")
public final class TikTokVideoVerifyCommand {

  private final RedisMessenger redisMessenger;
  private final TikTokVideoVerifyCache tikTokVideoVerifyCache;
  private final TikTokVideoVerifyRepository tikTokVideoVerifyRepository;

  public TikTokVideoVerifyCommand(final RedisMessenger redisMessenger, final TikTokVideoVerifyCache tikTokVideoVerifyCache,
      final TikTokVideoVerifyRepository tikTokVideoVerifyRepository) {
    this.redisMessenger = redisMessenger;
    this.tikTokVideoVerifyCache = tikTokVideoVerifyCache;
    this.tikTokVideoVerifyRepository = tikTokVideoVerifyRepository;
  }

  @Execute
  void exec(@Context final Player player) {
    TikTokVideoVerifyGui gui = new TikTokVideoVerifyGui(redisMessenger, tikTokVideoVerifyCache,  tikTokVideoVerifyRepository);
    gui.open(player);
  }

}
