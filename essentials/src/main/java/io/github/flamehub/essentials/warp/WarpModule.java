package io.github.flamehub.essentials.warp;

import io.github.flamehub.commons.bukkit.BukkitModule;
import io.github.flamehub.commons.bukkit.dispatcher.FlameDispatcher;
import io.github.flamehub.commons.bukkit.teleport.TeleporterService;
import io.github.flamehub.commons.bukkit.util.ServiceUtil;
import org.bukkit.plugin.Plugin;

public final class WarpModule extends BukkitModule {

    public WarpModule(final Plugin plugin, final FlameDispatcher flameDispatcher) {
        super(plugin, flameDispatcher);

        var warpConfig = super.mongoConfigService.findOrCreate(WarpConfig.class, "warps", WarpConfig::new);
        super.addCommands(
                new WarpCommandAdmin(super.mongoConfigService, warpConfig),
                new WarpCommand(warpConfig, ServiceUtil.getService(TeleporterService.class))
        );

    }

    

}
