package io.github.flamehub.essentials;

import io.github.flamehub.commons.bukkit.BukkitBootstrap;
import io.github.flamehub.commons.bukkit.BukkitBootstrapFactory;

public final class EssentialsBootstrap extends BukkitBootstrap<EssentialsModule> {
    public EssentialsBootstrap() {
        super(new BukkitBootstrapFactory<>(EssentialsModule::new));
    }
}
