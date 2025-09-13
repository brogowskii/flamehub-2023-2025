package io.github.flamehub.armor.upgrade;

import dev.rollczi.litecommands.annotations.LiteCommandsAnnotations;
import dev.rollczi.litecommands.bukkit.LiteBukkitFactory;
import dev.rollczi.litecommands.bukkit.context.PlayerOnlyContextProvider;
import dev.rollczi.litecommands.message.MessageRegistry;
import dev.rollczi.litecommands.schematic.SchematicFormat;
import io.github.flamehub.commons.bukkit.BukkitModule;
import io.github.flamehub.commons.bukkit.command.argument.PlayerArgument;
import io.github.flamehub.commons.bukkit.command.handler.InvalidUsageHandlerImpl;
import io.github.flamehub.commons.bukkit.command.handler.MissingPermissionHandlerImpl;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;

public final class ArmorUpgradePlugin extends BukkitModule {

  public static NamespacedKey CUSTOM_ARMOR_KEY;

  private ArmorUpgradeConfig armorUpgradeConfig;
  private ArmorUpgradeService armorUpgradeService;
  private Economy economy;

  @Override
  public void onEnable() {
    super.onEnable();

    CUSTOM_ARMOR_KEY = new NamespacedKey(this, "level");

    armorUpgradeConfig = flameConfigService.getOrCreate(ArmorUpgradeConfig.class);
    armorUpgradeService = new ArmorUpgradeService(armorUpgradeConfig, flameConfigService);
    economy = getService(Economy.class);

    LiteBukkitFactory.builder()
        .settings(settings -> settings
            .fallbackPrefix("armor-upgrade")
            .nativePermissions(false)
        )
        .argument(Player.class, new PlayerArgument(messagesService))
        .context(Player.class, new PlayerOnlyContextProvider(new MessageRegistry<>()))

        .missingPermission(new MissingPermissionHandlerImpl(messagesService))
        .invalidUsage(new InvalidUsageHandlerImpl(messagesService))

        .commands(LiteCommandsAnnotations.of(
            new ArmorUpgradeCommand(armorUpgradeService, armorUpgradeConfig, economy),
            new ArmorUpgradeCommandAdmin(flameConfigService, armorUpgradeConfig, armorUpgradeService)
        ))

        .schematicGenerator(SchematicFormat.angleBrackets())
        .build();


  }

  public ArmorUpgradeService getArmorUpgradeService() {
    return armorUpgradeService;
  }

  public ArmorUpgradeConfig getArmorUpgradeConfig() {
    return armorUpgradeConfig;
  }
}
