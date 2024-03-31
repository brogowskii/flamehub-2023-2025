package io.github.flamehub.ranking.gui;

import dev.triumphteam.gui.guis.Gui;
import org.bukkit.entity.Player;
import io.github.flamehub.commons.bukkit.text.TextUtil;
import io.github.flamehub.commons.bukkit.util.FlameItemBuilder;
import io.github.flamehub.commons.bukkit.util.GuiHelper;
import io.github.flamehub.commons.bukkit.util.NumberConverter;
import io.github.flamehub.commons.util.TimeUtil;
import io.github.flamehub.ranking.RankingItem;
import io.github.flamehub.ranking.RankingWrapper;
import io.github.flamehub.ranking.info.RankingInfo;

import java.time.Duration;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public final class RankingGui {

    private final RankingGuiWrapper rankingGuiWrapper;
    private final List<RankingWrapper> rankingWrappers;

    public RankingGui(RankingGuiWrapper rankingGuiWrapper, List<RankingWrapper> rankingWrappers) {
        this.rankingGuiWrapper = rankingGuiWrapper;
        this.rankingWrappers = rankingWrappers;
    }

    public void open(Player player) {

        Gui gui = Gui.gui()
                .title(TextUtil.parse(this.rankingGuiWrapper.getGuiName()))
                .rows(5)
                .disableAllInteractions()
                .create();

        GuiHelper.fillGui5(gui);

        for (RankingWrapper rankingWrapper : this.rankingWrappers) {

            RankingInfo info = rankingWrapper.getInfo();
            RankingItem guiInfo = info.getItem();
            FlameItemBuilder itemBuilder = FlameItemBuilder.of(guiInfo.getMaterial());
            itemBuilder.name(guiInfo.getName());
            itemBuilder.appendLore("");

            AtomicInteger atomicInteger = new AtomicInteger(1);
            rankingWrapper.getEntries()
                    .stream()
                    .limit(17)
                    .forEach(rankingEntry -> {

                        Object value = rankingEntry.getValue();
                        if (info.getId().equalsIgnoreCase("spend-time")) {
                            long longValue = Long.parseLong(value.toString());
                            value = TimeUtil.formatTimeSimple(Duration.ofMillis(longValue));
                        }

                        if (info.getId().equalsIgnoreCase("money")) {
                            value = NumberConverter.convertNumber(Double.parseDouble(value.toString()));
                        }

                        itemBuilder.appendLore(info.getItem().getTemplate()
                                .replace("{POSITION}", String.valueOf(atomicInteger.getAndIncrement()))
                                .replace("{ENTRY}", rankingEntry.getName())
                                .replace("{VALUE}", String.valueOf(value)));

                    });

            if (guiInfo.getAdditionalLore() != null) {
                itemBuilder.appendLore("");
                itemBuilder.appendLore(guiInfo.getAdditionalLore());
            }

            gui.setItem(guiInfo.getSlot(), itemBuilder.asGuiItem());

        }

        gui.open(player);

    }

    public RankingGuiWrapper getRankingGuiWrapper() {
        return rankingGuiWrapper;
    }
}
