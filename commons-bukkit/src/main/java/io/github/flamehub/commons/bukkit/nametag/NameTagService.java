package io.github.flamehub.commons.bukkit.nametag;

import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerTeams;
import io.github.flamehub.commons.bukkit.dispatcher.FlameDispatcher;
import io.github.flamehub.commons.bukkit.text.TextUtil;
import java.util.Map;
import java.util.OptionalInt;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.model.group.Group;
import net.luckperms.api.model.user.User;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public final class NameTagService {

  public static final Map<UUID, NameTagTeam> TEAM_MAP = new ConcurrentHashMap<>();
  private static final LuckPerms LUCK_PERMS = LuckPermsProvider.get();
  private final FlameDispatcher flameDispatcher;
  private final NameTagProvider nameTagProvider;

  public NameTagService(final FlameDispatcher flameDispatcher,
      final NameTagProvider nameTagProvider) {
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
    return 100 - getGroupWeight(player);
  }

  public void update(Player player) {

    flameDispatcher.dispatchAsync(() -> {
      for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
        update(onlinePlayer, player);
      }
    });

    flameDispatcher.dispatchAsyncLater(() -> {
      for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
        update(player, onlinePlayer);
      }
    }, 15L);

  }

  public void create(Player player) {
    String teamName = getTeamName(player);
    TEAM_MAP.put(player.getUniqueId(), new NameTagTeam(teamName));

    WrapperPlayServerTeams.ScoreBoardTeamInfo teamInfo = new WrapperPlayServerTeams.ScoreBoardTeamInfo(
        TextUtil.parse(teamName),
        Component.empty(),
        Component.empty(),
        WrapperPlayServerTeams.NameTagVisibility.ALWAYS,
        WrapperPlayServerTeams.CollisionRule.NEVER,
        NamedTextColor.WHITE,
        WrapperPlayServerTeams.OptionData.NONE
    );

    WrapperPlayServerTeams wrapperPlayServerTeams = new WrapperPlayServerTeams(teamName,
        WrapperPlayServerTeams.TeamMode.CREATE, teamInfo, nameTagProvider.getName(player));
    PacketEvents.getAPI().getPlayerManager().sendPacket(player, wrapperPlayServerTeams);

    for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
      if (onlinePlayer.getUniqueId().equals(player.getUniqueId())) {
        continue;
      }
      PacketEvents.getAPI().getPlayerManager().sendPacket(onlinePlayer, wrapperPlayServerTeams);

      NameTagTeam onlineTeamName = TEAM_MAP.getOrDefault(onlinePlayer.getUniqueId(),
          new NameTagTeam(getTeamName(onlinePlayer)));
      WrapperPlayServerTeams.ScoreBoardTeamInfo onlineTeamInfo = new WrapperPlayServerTeams.ScoreBoardTeamInfo(
          TextUtil.parse(onlineTeamName.getTeamName()),
          Component.empty(),
          Component.empty(),
          WrapperPlayServerTeams.NameTagVisibility.ALWAYS,
          WrapperPlayServerTeams.CollisionRule.NEVER,
          NamedTextColor.WHITE,
          WrapperPlayServerTeams.OptionData.NONE
      );

      WrapperPlayServerTeams wrapperPlayServerTeamsOther = new WrapperPlayServerTeams(
          onlineTeamName.getTeamName(), WrapperPlayServerTeams.TeamMode.CREATE, onlineTeamInfo,
          nameTagProvider.getName(onlinePlayer));
      PacketEvents.getAPI().getPlayerManager().sendPacket(player, wrapperPlayServerTeamsOther);
    }

  }

  public void update(Player player, Player receiver) {
    NameTagTeam team = TEAM_MAP.getOrDefault(player.getUniqueId(),
        new NameTagTeam(getTeamName(player)));
    String prefix = nameTagProvider.getPrefix(player, receiver);
    String suffix = nameTagProvider.getSuffix(player, receiver);

    team.setPrefix(prefix);
    team.setSuffix(suffix);

    WrapperPlayServerTeams.ScoreBoardTeamInfo teamInfo = new WrapperPlayServerTeams.ScoreBoardTeamInfo(
        TextUtil.parse(team.getTeamName()),
        TextUtil.parse(prefix),
        TextUtil.parse(suffix),
        WrapperPlayServerTeams.NameTagVisibility.ALWAYS,
        WrapperPlayServerTeams.CollisionRule.NEVER,
        NamedTextColor.WHITE,
        WrapperPlayServerTeams.OptionData.NONE
    );
    WrapperPlayServerTeams wrapperPlayServerTeams = new WrapperPlayServerTeams(
        team.getTeamName(),
        WrapperPlayServerTeams.TeamMode.UPDATE,
        teamInfo,
        player.getEntityId() == receiver.getEntityId() ? player.getName()
            : nameTagProvider.getName(player)
    );
    PacketEvents.getAPI().getPlayerManager().sendPacket(receiver, wrapperPlayServerTeams);
  }

  public void remove(Player player) {
    NameTagTeam team = TEAM_MAP.get(player.getUniqueId());
    WrapperPlayServerTeams wrapper = new WrapperPlayServerTeams(
        team.getTeamName(),
        WrapperPlayServerTeams.TeamMode.REMOVE,
        (WrapperPlayServerTeams.ScoreBoardTeamInfo) null
    );
    PacketEvents.getAPI().getPlayerManager().sendPacket(player, wrapper);

    for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
      if (onlinePlayer.getUniqueId().equals(player.getUniqueId())) {
        continue;
      }

      PacketEvents.getAPI().getPlayerManager().sendPacket(onlinePlayer, wrapper);
      NameTagTeam teamOnline = TEAM_MAP.get(player.getUniqueId());
      WrapperPlayServerTeams wrapperOnline = new WrapperPlayServerTeams(teamOnline.getTeamName(),
          WrapperPlayServerTeams.TeamMode.REMOVE, (WrapperPlayServerTeams.ScoreBoardTeamInfo) null);
      PacketEvents.getAPI().getPlayerManager().sendPacket(player, wrapperOnline);
    }

  }


  private String getTeamName(Player player) {
    String _team = nameTagProvider.getName(player);
    String teamName = getWeight(player) + _team;
    if (teamName.length() > 16) {
      teamName = teamName.substring(0, 16);
    }
    return teamName;
  }
}