package io.github.flamehub.essentials.vanish;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

final class VanishedEntryCache {

    private final Set<UUID> vanishedEntries = new HashSet<>();

    boolean isVanished(final UUID uniqueId) {
        return vanishedEntries.contains(uniqueId);
    }

    void addVanished(final UUID uniqueId) {
        vanishedEntries.add(uniqueId);
    }

    void removeVanished(final UUID uniqueId) {
        vanishedEntries.remove(uniqueId);
    }

}
