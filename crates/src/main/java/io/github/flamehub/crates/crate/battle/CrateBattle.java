package io.github.flamehub.crates.crate.battle;

import org.bukkit.entity.Player;
import io.github.flamehub.crates.crate.Crate;
import io.github.flamehub.crates.crate.CrateItem;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public class CrateBattle {

    private final Map<UUID, List<CrateItem>> itemsByUUID;

    private final List<Crate> crates;
    private final Player creator;
    private Player opponent;

    public CrateBattle(Map<UUID, List<CrateItem>> itemsByUUID, List<Crate> crates, Player creator) {
        this.itemsByUUID = itemsByUUID;
        this.crates = crates;
        this.creator = creator;
    }

    public Map<UUID, List<CrateItem>> getItemsByUUID() {
        return itemsByUUID;
    }

    public List<Crate> getCrates() {
        return crates;
    }

    public Player getCreator() {
        return creator;
    }

    public Player getOpponent() {
        return opponent;
    }

    public void setOpponent(Player opponent) {
        this.opponent = opponent;
    }
}
