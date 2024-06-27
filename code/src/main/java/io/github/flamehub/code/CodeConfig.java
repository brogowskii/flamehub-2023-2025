package io.github.flamehub.code;

import io.github.flamehub.commons.legacy.config.MongoConfig;

import java.util.ArrayList;
import java.util.List;

public final class CodeConfig extends MongoConfig {

    private String database = "boxpvp";
    private List<Code> codes = List.of(new Code("vip", List.of("lp user {PLAYER} parent addtemp vip 2d"), new ArrayList<>(), "30m"));

    public CodeConfig() {
    }

    public CodeConfig(String id) {
        super(id);
    }

    public Code findByName(String name) {

        for (Code code : this.codes) {
            if (code.getName().equalsIgnoreCase(name)) {
                return code;
            }
        }
        return null;

    }

    public List<Code> getCodes() {
        return codes;
    }

    public String getDatabase() {
        return database;
    }
}
