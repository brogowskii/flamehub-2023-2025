package io.github.flamehub.missions;

import dev.rollczi.litecommands.annotations.command.Command;
import dev.rollczi.litecommands.annotations.context.Context;
import dev.rollczi.litecommands.annotations.execute.Execute;
import io.github.flamehub.commons.bukkit.dispatcher.FlameDispatcher;
import io.github.flamehub.missions.user.MissionUserCache;
import io.github.flamehub.missions.user.MissionUserRepository;
import org.bukkit.entity.Player;

@Command(name = "misje", aliases = "missions")
public final class MissionCommand {

    private final FlameDispatcher flameDispatcher;
    private final MissionUserCache missionUserCache;
    private final MissionUserRepository missionUserRepository;

    public MissionCommand(FlameDispatcher flameDispatcher, MissionUserCache missionUserCache, MissionUserRepository missionUserRepository) {
        this.flameDispatcher = flameDispatcher;
        this.missionUserCache = missionUserCache;
        this.missionUserRepository = missionUserRepository;
    }

    @Execute
    void exec(@Context Player player) {
        new MissionGui(this.missionUserCache, this.missionUserRepository).open(player);
    }

}
