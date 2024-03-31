package io.github.flamehub.commons.json;

import com.google.gson.*;

import java.time.Instant;

public final class JsonUtil {

    public final static Gson GSON = new GsonBuilder()
            .registerTypeAdapter(Instant.class, new InstantAdapter())
            .setLongSerializationPolicy(LongSerializationPolicy.STRING)
            .serializeNulls()
            .disableHtmlEscaping()
            .setPrettyPrinting()
            .create();

    public final static Gson DATABASE_GSON = new GsonBuilder()
            .registerTypeAdapter(Instant.class, new InstantAdapter())
            .setLongSerializationPolicy(LongSerializationPolicy.STRING)
            .serializeNulls()
            .disableHtmlEscaping()
            .setPrettyPrinting()
            .create();

}
