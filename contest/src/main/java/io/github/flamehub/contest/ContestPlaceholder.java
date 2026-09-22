package io.github.flamehub.contest;

import io.github.flamehub.commons.bukkit.util.NumberConverter;
import io.github.flamehub.contest.user.ContestUser;
import io.github.flamehub.contest.user.ContestUserFacade;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

final class ContestPlaceholder extends PlaceholderExpansion {

  private final ContestUserFacade contestUserFacade;

  public ContestPlaceholder(final ContestUserFacade contestUserFacade) {
    this.contestUserFacade = contestUserFacade;
  }

  @Override
  public @NotNull String getIdentifier() {
    return "contest";
  }

  @Override
  public @NotNull String getAuthor() {
    return "opalkamarcin";
  }

  @Override
  public @NotNull String getVersion() {
    return "0.1";
  }

  @Override
  public @Nullable String onPlaceholderRequest(final Player player, @NotNull final String params) {

    final ContestUser contestUser = contestUserFacade.findByKey(player.getUniqueId());
    if (contestUser == null) {
      return "";
    }

    if ("points".equalsIgnoreCase(params)) {
      return NumberConverter.convertNumberLow(contestUser.getContestPoints());
    }

    return "";
  }
}
