package io.github.flamehub.ranking;

import io.github.flamehub.commons.bukkit.util.NumberConverter;
import io.github.flamehub.commons.util.RoundUtil;
import io.github.flamehub.commons.util.TimeUtil;
import io.github.flamehub.ranking.info.RankingInfo;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;

public final class RankingPlaceholder extends PlaceholderExpansion {

  private final RankingCache rankingCache;

  public RankingPlaceholder(final RankingCache rankingCache) {
    this.rankingCache = rankingCache;
  }

  @Override
  public @NotNull String getIdentifier() {
    return "ranking";
  }

  @Override
  public @NotNull String getAuthor() {
    return "MarcinOpalka";
  }

  @Override
  public @NotNull String getVersion() {
    return "1.0";
  }

  @Override
  public String onRequest(final OfflinePlayer player, @NotNull final String params) {

    if (params.startsWith("place:")) {

      final String[] split = params.split(":");
      final RankingWrapper rankingWrapper = rankingCache.findByInfo(split[1]);
      if (rankingWrapper == null) {
        return "";
      }

      final int place = rankingWrapper.getPlace(player.getName());
      return String.valueOf(place == 0 ? rankingWrapper.getInfo().getLimit() + "+" : place);
    }

    if (params.startsWith("top:")) {
      final String[] split = params.split(":");
      final List<RankingEntry> entries = rankingCache.findByInfo(split[2]).getEntries();
      if (entries.isEmpty()) {
        return "";
      }

      try {
        final RankingEntry rankingEntry = entries.get(Integer.parseInt(split[1]));
        return rankingEntry.getName();
      } catch (final IndexOutOfBoundsException e) {
        return "Brak";
      }
    }

    if (params.startsWith("value:")) {
      final String[] split = params.split(":");

      final RankingWrapper rankingWrapper = rankingCache.findByInfo(split[2]);
      final RankingInfo info = rankingWrapper.getInfo();
      final List<RankingEntry> entries = rankingWrapper.getEntries();
      if (entries.isEmpty()) {
        return "";
      }

      final RankingEntry rankingEntry;
      try {
        rankingEntry = entries.get(Integer.parseInt(split[1]));
      } catch (final IndexOutOfBoundsException e) {
        return "0";
      }

      List<Object> values = rankingEntry.getValue();

      values = values.stream()
          .map(value -> switch (info.getId()) {
            case "spend-time" -> {
              final long longValue = Long.parseLong(value.toString());
              yield TimeUtil.formatTimeSimple(Duration.ofMillis(longValue));
            }
            case "money" -> {
              final double doubleValue = Double.parseDouble(value.toString());
              yield NumberConverter.convertNumber(doubleValue);
            }
            default -> {
              if (value instanceof Double) {
                yield RoundUtil.round((double) value, 2);
              }
              yield value;
            }
          })
          .toList();

      String s;
      try {
        s = split[3];
      } catch (final ArrayIndexOutOfBoundsException e) {
        s = "0";
      }

      final int i = Integer.parseInt(s);
      if (i == 0) {
        return String.valueOf(values.getFirst().toString());
      }

      return String.valueOf(values.get(i).toString());

    }

    return "";
  }
}