package io.github.flamehub.commons.bukkit.sidebar;

import fr.mrmicky.fastboard.FastBoard;
import java.util.List;

public interface SidebarUpdater {

  String getTitle(FastBoard fastBoard);

  List<String> getLines(FastBoard fastBoard);

}
