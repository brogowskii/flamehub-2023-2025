package io.github.flamehub.lobby;

import java.util.Comparator;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.model.group.Group;
import net.luckperms.api.model.user.User;
import net.luckperms.api.node.NodeType;
import net.luckperms.api.node.types.InheritanceNode;
import net.luckperms.api.node.types.PrefixNode;
import net.luckperms.api.util.Tristate;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class LobbyPlaceholder extends PlaceholderExpansion {

  private final LuckPerms lp = LuckPermsProvider.get();

  @Override
  public @NotNull String getIdentifier() {
    return "lobby";
  }

  @Override
  public @NotNull String getAuthor() {
    return "FlameHub";
  }

  @Override
  public @NotNull String getVersion() {
    return "1.0.0";
  }

  @Override
  public boolean persist() {
    return true;
  }

  @Override
  public boolean canRegister() {
    return true;
  }

  @Override
  public @Nullable String onRequest(final OfflinePlayer player, @NotNull final String params) {

    // Only "prefix" for now. You can add more params if needed.
    if (!"prefix".equalsIgnoreCase(params)) return "";

    final User user = lp.getUserManager().getUser(player.getUniqueId());
    if (user == null) {
      return "";
    }

    Group bestGroup = null;
    int bestWeight = Integer.MIN_VALUE;

    for (final InheritanceNode node : user.getNodes(NodeType.INHERITANCE)) {
      if (node.hasExpired()) continue;
      if (!node.getValue()) continue;
      final Group g = lp.getGroupManager().getGroup(node.getGroupName());
      if (g == null) continue;

      final int weight = g.getWeight().orElse(0);
      if (weight > bestWeight) {
        bestWeight = weight;
        bestGroup = g;
      }
    }

    if (bestGroup == null) {
      bestGroup = lp.getGroupManager().getGroup(user.getPrimaryGroup());
      if (bestGroup == null) return "";
    }

    final Group finalBestGroup = bestGroup;
    return bestGroup.getNodes(NodeType.PREFIX).stream()
        .filter(n -> !n.hasExpired())
        .max(Comparator.comparingInt(PrefixNode::getPriority))
        .map(PrefixNode::getMetaValue)
        .orElseGet(finalBestGroup::getDisplayName);

  }
}