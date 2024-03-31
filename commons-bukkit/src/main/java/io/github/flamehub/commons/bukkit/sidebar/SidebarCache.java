package io.github.flamehub.commons.bukkit.sidebar;

import fr.mrmicky.fastboard.FastBoard;
import io.github.flamehub.commons.cache.KeyValueCache;

import java.util.UUID;

public class SidebarCache extends KeyValueCache<UUID, FastBoard> {
    public SidebarCache() {
        super(false);
    }
}
