package io.github.flamehub.commons.bukkit.nametag;

import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerTeams;
import io.github.flamehub.commons.bukkit.dispatcher.FlameDispatcher;
import io.github.flamehub.commons.bukkit.text.TextUtil;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.model.group.Group;
import net.luckperms.api.model.user.User;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class NameTagService {

    private static final LuckPerms LUCK_PERMS = LuckPermsProvider.get();
    private final Map<UUID, String> teamMap = new ConcurrentHashMap<>();

    private final FlameDispatcher flameDispatcher;
    private final NameTagProvider nameTagProvider;

    public NameTagService(FlameDispatcher flameDispatcher, NameTagProvider nameTagProvider) {
        this.flameDispatcher = flameDispatcher;
        this.nameTagProvider = nameTagProvider;
    }

    public int getGroupWeight(Player player) {
        User user = LUCK_PERMS.getUserManager().getUser(player.getUniqueId());
        if (user == null) {
            return 1;
        }
        String primaryGroupId = user.getPrimaryGroup();
        Group group = LUCK_PERMS.getGroupManager().getGroup(primaryGroupId);
        if (group == null || group.getName().equals("default")) {
            return 1;
        }
        OptionalInt weight = group.getWeight();
        if (weight.isEmpty()) {
            return 1;
        }
        return weight.getAsInt() + 1;
    }

    int getWeight(Player player) {
        return 100 - this.getGroupWeight(player);
    }

    public void update(Player player) {

        this.flameDispatcher.dispatchAsync(() -> {
            for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                this.update(player, onlinePlayer);
            }
        });
        this.flameDispatcher.dispatchAsyncLater(() -> {
            for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                this.update(onlinePlayer, player);
            }
        }, 15L);

    }

    public void create(Player player) {
        String teamName = this.getTeamName(player);
        this.teamMap.put(player.getUniqueId(), teamName);

        WrapperPlayServerTeams.ScoreBoardTeamInfo teamInfo = new WrapperPlayServerTeams.ScoreBoardTeamInfo(
                TextUtil.parse(teamName),
                Component.empty(),
                Component.empty(),
                WrapperPlayServerTeams.NameTagVisibility.ALWAYS,
                WrapperPlayServerTeams.CollisionRule.NEVER,
                NamedTextColor.WHITE,
                WrapperPlayServerTeams.OptionData.NONE
        );

        WrapperPlayServerTeams wrapperPlayServerTeams = new WrapperPlayServerTeams(teamName, WrapperPlayServerTeams.TeamMode.CREATE, teamInfo, this.nameTagProvider.getName(player));
        PacketEvents.getAPI().getPlayerManager().sendPacket(player, wrapperPlayServerTeams);

        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            if (onlinePlayer.getUniqueId().equals(player.getUniqueId())) continue;

            PacketEvents.getAPI().getPlayerManager().sendPacket(onlinePlayer, wrapperPlayServerTeams);

            String onlineTeamName = this.teamMap.getOrDefault(onlinePlayer.getUniqueId(), getTeamName(onlinePlayer));
            WrapperPlayServerTeams.ScoreBoardTeamInfo onlineTeamInfo = new WrapperPlayServerTeams.ScoreBoardTeamInfo(
                    TextUtil.parse(onlineTeamName),
                    Component.empty(),
                    Component.empty(),
                    WrapperPlayServerTeams.NameTagVisibility.ALWAYS,
                    WrapperPlayServerTeams.CollisionRule.NEVER,
                    NamedTextColor.WHITE,
                    WrapperPlayServerTeams.OptionData.NONE
            );

            WrapperPlayServerTeams wrapperPlayServerTeamsOther = new WrapperPlayServerTeams(onlineTeamName, WrapperPlayServerTeams.TeamMode.CREATE, onlineTeamInfo, this.nameTagProvider.getName(onlinePlayer));
            PacketEvents.getAPI().getPlayerManager().sendPacket(player, wrapperPlayServerTeamsOther);
        }

    }

    public void update(Player player, Player receiver) {
        String teamName = this.teamMap.getOrDefault(player.getUniqueId(), getTeamName(player));
        WrapperPlayServerTeams.ScoreBoardTeamInfo teamInfo = new WrapperPlayServerTeams.ScoreBoardTeamInfo(
                TextUtil.parse(teamName),
                TextUtil.parse(this.nameTagProvider.getPrefix(player, receiver)),
                TextUtil.parse(this.nameTagProvider.getSuffix(player, receiver)),
                WrapperPlayServerTeams.NameTagVisibility.ALWAYS,
                WrapperPlayServerTeams.CollisionRule.NEVER,
                NamedTextColor.WHITE,
                WrapperPlayServerTeams.OptionData.NONE
        );
        WrapperPlayServerTeams wrapperPlayServerTeams = new WrapperPlayServerTeams(
                teamName,
                WrapperPlayServerTeams.TeamMode.UPDATE,
                teamInfo,
                player.getEntityId() == receiver.getEntityId() ? player.getName() : this.nameTagProvider.getName(player)
        );
        PacketEvents.getAPI().getPlayerManager().sendPacket(receiver, wrapperPlayServerTeams);
    }

    public void remove(Player player) {
        String teamName = this.teamMap.get(player.getUniqueId());
        WrapperPlayServerTeams wrapper = new WrapperPlayServerTeams(
                teamName,
                WrapperPlayServerTeams.TeamMode.REMOVE,
                (WrapperPlayServerTeams.ScoreBoardTeamInfo) null
        );
        PacketEvents.getAPI().getPlayerManager().sendPacket(player, wrapper);

        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            if (onlinePlayer.getUniqueId().equals(player.getUniqueId())) continue;

            PacketEvents.getAPI().getPlayerManager().sendPacket(onlinePlayer, wrapper);
            String teamNameOnline = this.teamMap.get(player.getUniqueId());
            WrapperPlayServerTeams wrapperOnline = new WrapperPlayServerTeams(teamNameOnline, WrapperPlayServerTeams.TeamMode.REMOVE, (WrapperPlayServerTeams.ScoreBoardTeamInfo) null);
            PacketEvents.getAPI().getPlayerManager().sendPacket(player, wrapperOnline);
        }

    }


    private String getTeamName(Player player) {
        String _team = this.nameTagProvider.getName(player);
        String teamName = this.getWeight(player) + _team;
        if (teamName.length() > 16) {
            teamName = teamName.substring(0, 16);
        }
        return teamName;
    }
}