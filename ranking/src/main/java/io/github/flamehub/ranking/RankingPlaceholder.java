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

  public RankingPlaceholder(RankingCache rankingCache) {
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
  public String onRequest(OfflinePlayer player, @NotNull String params) {

    if (params.startsWith("place:")) {

      String[] split = params.split(":");
      RankingWrapper rankingWrapper = rankingCache.findByInfo(split[1]);
      if (rankingWrapper == null) {
        return "";
      }

      int place = rankingWrapper.getPlace(player.getName());
      return String.valueOf(place == 0 ? "+" + rankingWrapper.getInfo().getLimit() : place);
    }

    if (params.startsWith("top:")) {
      String[] split = params.split(":");
      List<RankingEntry> entries = new ArrayList<>(
          this.rankingCache.findByInfo(split[2]).getEntries());
      if (entries.isEmpty()) {
        return "";
      }

      try {
        RankingEntry rankingEntry = entries.get(Integer.parseInt(split[1]));
        return rankingEntry.getName();
      } catch (IndexOutOfBoundsException e) {
        return "Brak";
      }

    }

    if (params.startsWith("value:")) {
      String[] split = params.split(":");

      final RankingWrapper rankingWrapper = this.rankingCache.findByInfo(split[2]);
      final RankingInfo info = rankingWrapper.getInfo();
      List<RankingEntry> entries = new ArrayList<>(
          rankingWrapper.getEntries());
      if (entries.isEmpty()) {
        return "";
      }

      RankingEntry rankingEntry;
      try {
        rankingEntry = entries.get(Integer.parseInt(split[1]));
      } catch (IndexOutOfBoundsException e) {
        return "0";
      }

      List<Object> values = rankingEntry.getValue();

      values = values.stream()
          .map(value -> {
            switch (info.getId()) {
              case "spend-time": {
                long longValue = Long.parseLong(value.toString());
                return TimeUtil.formatTimeSimple(Duration.ofMillis(longValue));
              }
              case "money": {
                double doubleValue = Double.parseDouble(value.toString());
                return NumberConverter.convertNumber(doubleValue);
              }
              default: {
                if (value instanceof Double) {
                  return RoundUtil.round((double) value, 2);
                } else {
                  return value;
                }
              }
            }
          })
          .toList();

      String s = "0";
      try {
         s = split[3];
      } catch (ArrayIndexOutOfBoundsException e) {
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