package io.github.flamehub.commons.bukkit.sidebar;

import fr.mrmicky.fastboard.FastBoard;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;

import java.util.List;

public interface SidebarUpdater {

    String getTitle(FastBoard fastBoard);

    List<String> getLines(FastBoard fastBoard);

}
