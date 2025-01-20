package io.github.flamehub.tiktok;


import dev.rollczi.litecommands.annotations.command.Command;
import dev.rollczi.litecommands.annotations.permission.Permission;
import io.github.flamehub.commons.bukkit.config.FlameConfigRefresherCommand;
import io.github.flamehub.commons.config.FlameConfigService;
import io.github.flamehub.tiktok.shop.TikTokShopConfig;

@Command(name = "tiktokadmin")
@Permission("server.tiktok.commands.admin")
public final class TikTokCommandAdmin extends FlameConfigRefresherCommand {

  public TikTokCommandAdmin(
      final FlameConfigService flameConfigService) {
    super(flameConfigService, TikTokShopConfig.class);
  }


}
