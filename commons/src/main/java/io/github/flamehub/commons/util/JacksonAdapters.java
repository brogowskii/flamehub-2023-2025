package io.github.flamehub.commons.util;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import java.io.IOException;
import java.time.Duration;
import java.time.Instant;

public class JacksonAdapters {

  public static class DurationSerializer extends JsonSerializer<Duration> {

    @Override
    public void serialize(final Duration value, final JsonGenerator gen, final SerializerProvider serializers)
        throws IOException {
      gen.writeString(value.toString());
    }
  }

  public static class DurationDeserializer extends JsonDeserializer<Duration> {

    @Override
    public Duration deserialize(final JsonParser p, final DeserializationContext ctx) throws IOException {
      final String durationStr = p.getText();
      return Duration.parse(durationStr);
    }
  }

  public static class InstantSerializer extends JsonSerializer<Instant> {

    @Override
    public void serialize(final Instant value, final JsonGenerator gen, final SerializerProvider serializers)
        throws IOException {
      gen.writeString(value.toString());
    }
  }

  public static class InstantDeserializer extends JsonDeserializer<Instant> {

    @Override
    public Instant deserialize(final JsonParser p, final DeserializationContext ctx) throws IOException {
      final String durationStr = p.getText();
      return Instant.parse(durationStr);
    }
  }


}
