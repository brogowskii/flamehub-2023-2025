package io.github.flamehub.crates.crate.battle;

import io.github.flamehub.commons.cache.KeyValueCache;

import java.util.UUID;

public class CrateBattleCache extends KeyValueCache<UUID, CrateBattle> {

    public CrateBattleCache() {
        super(false);
    }


}
