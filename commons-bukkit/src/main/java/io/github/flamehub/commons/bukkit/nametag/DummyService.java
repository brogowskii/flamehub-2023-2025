package io.github.flamehub.commons.bukkit.nametag;

import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.protocol.score.ScoreFormat;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerScoreboardObjective;
import io.github.flamehub.commons.bukkit.text.TextUtil;
import org.bukkit.entity.Player;

public final class DummyService {

    public static void create(Player player) {

        WrapperPlayServerScoreboardObjective wrapperPlayServerScoreboardObjective = new WrapperPlayServerScoreboardObjective(
                player.getName(),
                WrapperPlayServerScoreboardObjective.ObjectiveMode.CREATE,
                TextUtil.parse("&c❤️"),
                WrapperPlayServerScoreboardObjective.RenderType.HEARTS,
                ScoreFormat.blankScore()


        );

        PacketEvents.getAPI().getPlayerManager().sendPacket(player, wrapperPlayServerScoreboardObjective);

    }

}
