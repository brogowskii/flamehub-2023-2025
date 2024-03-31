package io.github.flamehub.commons.bukkit.tab;

import io.github.flamehub.commons.bukkit.text.TextUtil;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.JoinConfiguration;
import org.bukkit.entity.Player;

import java.util.List;

public class TablistService {

    private final TablistProvider tablistProvider;

    public TablistService(TablistProvider tablistProvider) {
        this.tablistProvider = tablistProvider;
    }

    public void send(Player player) {

        List<Component> header = TextUtil.parse(this.tablistProvider.getHeader(player));
        List<Component> footer = TextUtil.parse(this.tablistProvider.getFooter(player));

        JoinConfiguration separator = JoinConfiguration.separator(Component.newline());
        player.sendPlayerListHeaderAndFooter(Component.join(separator, header), Component.join(separator, footer));
    }

}
