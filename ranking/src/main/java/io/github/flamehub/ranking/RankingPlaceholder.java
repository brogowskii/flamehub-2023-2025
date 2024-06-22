package io.github.flamehub.ranking;

import io.github.flamehub.commons.util.TimeUtil;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;
import io.github.flamehub.commons.bukkit.util.NumberConverter;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

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
            List<RankingEntry> entries = new ArrayList<>(this.rankingCache.findByInfo(split[2]).getEntries());
            if (entries.isEmpty()) {
                return "";
            }

            try {
                RankingEntry rankingEntry = entries.get(Integer.parseInt(split[1]));
                return rankingEntry.getName();
            }
            catch (IndexOutOfBoundsException e) {
                return "Brak";
            }

        }

        if (params.startsWith("value:")) {
            String[] split = params.split(":");

            List<RankingEntry> entries = new ArrayList<>(this.rankingCache.findByInfo(split[2]).getEntries());
            if (entries.isEmpty()) {
                return "";
            }

            RankingEntry rankingEntry;
            try {
                rankingEntry = entries.get(Integer.parseInt(split[1]));
            }
            catch (IndexOutOfBoundsException e) {
                return "0";
            }

            Object value = rankingEntry.getValue();
            if (split[2].equalsIgnoreCase("money")) {
                return NumberConverter.convertNumber(Double.parseDouble(value.toString()));
            }

            if (split[2].equalsIgnoreCase("spend-time")) {
                long longValue = Long.parseLong(value.toString());
                return TimeUtil.formatTimeSimple(Duration.ofMillis(longValue));
            }

//            entries.sort((o1, o2) -> Integer.compare((int) o2.getValue(), (int) o1.getValue()));
            return String.valueOf(rankingEntry.getValue().toString());

        }

        return "";
    }
}