package io.github.flamehub.commons.bukkit.setting;

import dev.rollczi.litecommands.annotations.command.Command;
import dev.rollczi.litecommands.annotations.context.Context;
import dev.rollczi.litecommands.annotations.execute.Execute;
import dev.rollczi.litecommands.annotations.permission.Permission;
import dev.triumphteam.gui.guis.Gui;
import io.github.flamehub.commons.bukkit.text.TextUtil;
import io.github.flamehub.commons.bukkit.util.FlameItemBuilder;
import io.github.flamehub.commons.config.FlameConfigService;
import java.util.ArrayList;
import org.bukkit.Material;
import org.bukkit.entity.Player;

@Command(name = "serversettings", aliases = "ss")
@Permission("server.commands.serversettings")
public final class ServerSettingCommand {

  private final FlameConfigService flameConfigService;
  private final ServerSettingConfig serverSettingConfig;

  public ServerSettingCommand(
      final FlameConfigService flameConfigService,
      final ServerSettingConfig serverSettingConfig
  ) {
    this.flameConfigService = flameConfigService;
    this.serverSettingConfig = serverSettingConfig;
  }

  @Execute
  void execute(final @Context Player player) {
    final Gui gui = Gui.gui()
        .rows(5)
        .title(TextUtil.parse("&8&lUstawienia serwera"))
        .disableAllInteractions()
        .create();

    for (final ServerSetting value : new ArrayList<>(serverSettingConfig.getSettings())) {
      final boolean isDisabled = serverSettingConfig.getDisabledSettings().contains(value.getId());
      gui.addItem(FlameItemBuilder.of(isDisabled ? Material.RED_CONCRETE : Material.LIME_CONCRETE)
          .name("&7" + value.getFriendlyName() + " " + (isDisabled ? "&c❌" : "&a✔"))
          .asGuiItem(event -> {

            if (isDisabled) {
              serverSettingConfig.getDisabledSettings().remove(value.getId());
            } else {
              serverSettingConfig.getDisabledSettings().add(value.getId());
            }

            try {
              flameConfigService.save(ServerSettingConfig.class);
              flameConfigService.refreshAndBroadcast(ServerSettingConfig.class);
            } catch (final IllegalAccessException e) {
              throw new RuntimeException(e);
            }
            execute(player);
          }));
    }

    gui.open(player);
  }

}
