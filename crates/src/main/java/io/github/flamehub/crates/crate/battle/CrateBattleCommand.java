package io.github.flamehub.crates.crate.battle;

import dev.rollczi.litecommands.annotations.command.Command;
import dev.rollczi.litecommands.annotations.context.Context;
import dev.rollczi.litecommands.annotations.execute.Execute;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import io.github.flamehub.crates.CratesConfig;

import java.util.Arrays;
import java.util.HashMap;

@Command(name = "casebattle", aliases = {"cratebattle"})
public final class CrateBattleCommand {

    private final Plugin plugin;
    private final CratesConfig cratesConfig;
    private final CrateBattleCache crateBattleCache;

    public CrateBattleCommand(Plugin plugin, CratesConfig cratesConfig, CrateBattleCache crateBattleCache) {
        this.plugin = plugin;
        this.cratesConfig = cratesConfig;
        this.crateBattleCache = crateBattleCache;
    }


    @Execute(name = "stworz")
    public void create(@Context Player player) {

        CrateBattle crateBattle = new CrateBattle(new HashMap<>(), Arrays.asList(this.cratesConfig.findById("flamebox"), this.cratesConfig.findById("flamebox"), this.cratesConfig.findById("flamebox")), player);
        this.crateBattleCache.add(player.getUniqueId(), crateBattle);

    }

    @Execute(name = "lista")
    public void list(@Context Player player) {
        new CrateBattleGui(this.plugin, player, this.crateBattleCache).openSearcher();
    }

}
