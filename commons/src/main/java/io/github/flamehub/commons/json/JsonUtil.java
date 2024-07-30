package io.github.flamehub.commons.json;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.LongSerializationPolicy;
import java.time.Instant;

public final class JsonUtil {

  public final static Gson GSON = new GsonBuilder()
      .enableComplexMapKeySerialization()
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
