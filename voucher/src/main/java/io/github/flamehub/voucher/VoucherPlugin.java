package io.github.flamehub.voucher;

import dev.rollczi.litecommands.annotations.LiteCommandsAnnotations;
import dev.rollczi.litecommands.bukkit.LiteBukkitFactory;
import dev.rollczi.litecommands.bukkit.context.PlayerOnlyContextProvider;
import dev.rollczi.litecommands.message.MessageRegistry;
import dev.rollczi.litecommands.schematic.SchematicFormat;
import io.github.flamehub.commons.bukkit.BukkitModule;
import io.github.flamehub.commons.bukkit.command.argument.PlayerArgument;
import io.github.flamehub.commons.bukkit.command.handler.InvalidUsageHandlerImpl;
import io.github.flamehub.commons.bukkit.command.handler.MissingPermissionHandlerImpl;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;

public final class VoucherPlugin extends BukkitModule {

  private VoucherConfig voucherConfig;
  private VoucherService voucherService;

  @Override
  public void onEnable() {
    super.onEnable();

    voucherConfig = flameConfigService.getOrCreate(VoucherConfig.class);
    voucherService = new VoucherService(voucherConfig, this);

    final PluginManager pluginManager = getServer().getPluginManager();
    pluginManager.registerEvents(new VoucherListener(voucherService), this);
    LiteBukkitFactory.builder()
        .settings(settings -> settings
            .fallbackPrefix("codes")
            .nativePermissions(false)
        )
        .argument(Player.class, new PlayerArgument(messagesService))
        .context(Player.class, new PlayerOnlyContextProvider(new MessageRegistry<>()))

        .missingPermission(new MissingPermissionHandlerImpl(messagesService))
        .invalidUsage(new InvalidUsageHandlerImpl(messagesService))

        .commands(LiteCommandsAnnotations.of(
            new VoucherCommand(voucherService, voucherConfig, flameConfigService)
        ))

        .schematicGenerator(SchematicFormat.angleBrackets())
        .build();

  }
}
