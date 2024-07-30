package io.github.flamehub.essentials.banitem;

import dev.rollczi.litecommands.LiteCommandsBuilder;
import dev.rollczi.litecommands.annotations.LiteCommandsAnnotations;
import dev.rollczi.litecommands.bukkit.LiteBukkitSettings;
import io.github.flamehub.commons.bukkit.BukkitConfigurator;
import io.github.flamehub.commons.config.FlameConfigService;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;

public final class BanItemConfigurator extends BukkitConfigurator {

  public BanItemFacade banItemFacade(
      final Plugin plugin,
      final LiteCommandsBuilder<CommandSender, LiteBukkitSettings, ?> liteCommandsBuilder,
      final FlameConfigService flameConfigService
  ) {

    final BanItemConfig banItemConfig = flameConfigService.getOrCreate(plugin.getDataFolder(),
        BanItemConfig.class);
    final BanItemFacade banItemFacade = new BanItemFacade(banItemConfig);

    liteCommandsBuilder.commands(LiteCommandsAnnotations.of(
        new BanItemCommand(banItemFacade, flameConfigService)
    ));

    registerListeners(plugin, new BanItemListener(banItemFacade));

    return banItemFacade;
  }

}
