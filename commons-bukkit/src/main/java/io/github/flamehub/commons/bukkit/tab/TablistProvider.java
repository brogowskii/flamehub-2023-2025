package io.github.flamehub.commons.bukkit.tab;

import org.bukkit.entity.Player;

import java.util.List;

public interface TablistProvider {

    List<String> getHeader(Player player);
    List<String> getFooter(Player player);

}
