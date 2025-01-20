package io.github.flamehub.ranking.gui;

import dev.triumphteam.gui.guis.Gui;
import io.github.flamehub.commons.bukkit.text.TextUtil;
import io.github.flamehub.commons.bukkit.util.FlameItemBuilder;
import io.github.flamehub.commons.bukkit.util.GuiHelper;
import io.github.flamehub.commons.bukkit.util.NumberConverter;
import io.github.flamehub.commons.util.RoundUtil;
import io.github.flamehub.commons.util.TimeUtil;
import io.github.flamehub.ranking.RankingItem;
import io.github.flamehub.ranking.RankingWrapper;
import io.github.flamehub.ranking.info.RankingInfo;
import java.time.Duration;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;

public final class RankingGui {

  private final RankingGuiWrapper rankingGuiWrapper;
  private final List<RankingWrapper> rankingWrappers;

  public RankingGui(RankingGuiWrapper rankingGuiWrapper, List<RankingWrapper> rankingWrappers) {
    this.rankingGuiWrapper = rankingGuiWrapper;
    this.rankingWrappers = rankingWrappers;
  }

  public void open(Player player) {
    Gui gui = Gui.gui()
        .title(TextUtil.parse(rankingGuiWrapper.getGuiName()))
        .rows(6)
        .disableAllInteractions()
        .create();

    GuiHelper.fillGui6(gui);

    for (RankingWrapper rankingWrapper : rankingWrappers) {
      RankingInfo info = rankingWrapper.getInfo();
      RankingItem guiInfo = info.getItem();
      FlameItemBuilder itemBuilder = FlameItemBuilder.of(guiInfo.getMaterial());
      itemBuilder.name(guiInfo.getName());
      itemBuilder.appendLore("");

      AtomicInteger atomicInteger = new AtomicInteger(1);

      rankingWrapper.getEntries().stream()
          .limit(17)
          .forEach(rankingEntry -> {
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

            String template = info.getItem().getTemplate()
                .replace("{POSITION}", String.valueOf(atomicInteger.getAndIncrement()))
                .replace("{ENTRY}", rankingEntry.getName());

            // Zastępowanie {VALUEX} w szablonie
            for (int index = 0; index < values.size(); index++) {
              template = template.replace("{VALUE" + (index + 1) + "}",
                  values.get(index).toString());
            }

            itemBuilder.appendLore(template);
          });

      if (guiInfo.getAdditionalLore() != null && !guiInfo.getAdditionalLore().isEmpty()) {
        itemBuilder.appendLore(PlaceholderAPI.setPlaceholders(player, guiInfo.getAdditionalLore()));
      }

      itemBuilder.flag(ItemFlag.HIDE_ATTRIBUTES);
      gui.setItem(guiInfo.getSlot(), itemBuilder.asGuiItem());
    }

    gui.open(player);
  }

  public RankingGuiWrapper getRankingGuiWrapper() {
    return rankingGuiWrapper;
  }
}
