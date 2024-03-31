package io.github.flamehub.kits.kit;

import dev.rollczi.litecommands.annotations.command.Command;
import dev.rollczi.litecommands.annotations.context.Context;
import dev.rollczi.litecommands.annotations.execute.Execute;
import org.bukkit.entity.Player;
import io.github.flamehub.commons.bukkit.dispatcher.FlameDispatcher;
import io.github.flamehub.kits.KitsConfig;
import io.github.flamehub.kits.user.KitUserCache;
import io.github.flamehub.kits.user.KitUserRepository;

@Command(name = "kit", aliases = {"kits", "kity", "zestawy", "zestaw"})
public final class KitCommand {

    private final FlameDispatcher flameDispatcher;
    private final KitsConfig kitConfig;
    private final KitUserCache kitUserCache;
    private final KitUserRepository kitUserRepository;

    public KitCommand(FlameDispatcher flameDispatcher, KitsConfig kitConfig, KitUserCache kitUserCache, KitUserRepository kitUserRepository) {
        this.flameDispatcher = flameDispatcher;
        this.kitConfig = kitConfig;
        this.kitUserCache = kitUserCache;
        this.kitUserRepository = kitUserRepository;
    }

    @Execute
    void execute(@Context Player player) {
        KitGui kitGui = new KitGui(this.flameDispatcher, this.kitConfig, this.kitUserCache, this.kitUserRepository);
        kitGui.open(player);
    }

}
