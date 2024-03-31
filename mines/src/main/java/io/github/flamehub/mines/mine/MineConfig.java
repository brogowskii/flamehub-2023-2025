package io.github.flamehub.mines.mine;

import com.google.common.collect.Maps;
import io.github.flamehub.commons.config.MongoConfig;

import java.util.Map;

public final class MineConfig extends MongoConfig {

    private Map<String, Mine> minesById = Maps.newHashMap();

    public MineConfig() {
    }

    public MineConfig(String id) {
        super(id);
    }

    public void add(Mine mine) {
        this.minesById.put(mine.getId().toLowerCase(), mine);
    }

    public void remove(Mine mine) {
        this.minesById.remove(mine.getId().toLowerCase());
    }

    public Mine findById(String id) {
        return this.minesById.get(id.toLowerCase());
    }

    public Map<String, Mine> getMinesById() {
        return minesById;
    }
}
