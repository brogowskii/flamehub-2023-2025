package io.github.flamehub.commons.bukkit.tab;

import java.util.List;
import org.bukkit.entity.Player;

public interface TablistProvider {

  List<String> getHeader(Player player);

  List<String> getFooter(Player player);

}
