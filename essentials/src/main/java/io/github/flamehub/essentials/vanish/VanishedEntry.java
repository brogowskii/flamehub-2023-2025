package io.github.flamehub.essentials.vanish;

import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;

import java.util.UUID;

@Entity("vanished_entries")
final class VanishedEntry {

    @Id
    private UUID uniqueId;
    private String nickname;

    VanishedEntry() {
    }

    VanishedEntry(final UUID uniqueId, final String nickname) {
        this.uniqueId = uniqueId;
        this.nickname = nickname;
    }


}
