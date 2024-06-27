package io.github.flamehub.commons.legacy.config;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;

public class MongoConfig implements Serializable {

    @JsonProperty("_id")
    private String id;

    public MongoConfig() {

    }

    public MongoConfig(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }
}
