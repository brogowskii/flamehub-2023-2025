package io.github.flamehub.commons.messenger.codec;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import io.github.flamehub.commons.json.JsonUtil;
import io.github.flamehub.commons.messenger.packet.Packet;
import io.lettuce.core.codec.RedisCodec;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

public final class PacketGsonCodec implements RedisCodec<String, Packet> {

  public static String serialize(Packet packet) {
    String json = JsonUtil.GSON.toJson(packet);
    JsonObject jsonObject = JsonParser.parseString(json).getAsJsonObject();
    jsonObject.addProperty("clazz", packet.getClass().getName());
    return jsonObject.toString();
  }

  public static Packet deserialize(String json) {
    JsonObject object = JsonParser.parseString(json).getAsJsonObject();
    Class<?> clazz = null;
    try {
      clazz = Class.forName(object.get("clazz").getAsString());
    } catch (ClassNotFoundException ignored) {
    }

    if (clazz != null) {
      return (Packet) JsonUtil.GSON.fromJson(object, clazz);
    }

    return null;
  }

  @Override
  public String decodeKey(ByteBuffer byteBuffer) {
    return StandardCharsets.UTF_8.decode(byteBuffer).toString();
  }

  @Override
  public Packet decodeValue(ByteBuffer byteBuffer) {
    try {
      byte[] bytes = new byte[byteBuffer.remaining()];
      byteBuffer.get(bytes);
      String json = new String(bytes, StandardCharsets.UTF_8);
      return deserialize(json);
    } catch (JsonSyntaxException ignored) {

    }
    return null;
  }

  @Override
  public ByteBuffer encodeKey(String key) {
    return StandardCharsets.UTF_8.encode(key);
  }

  @Override
  public ByteBuffer encodeValue(Packet value) {
    String json = serialize(value);
    return ByteBuffer.wrap(json.getBytes(StandardCharsets.UTF_8));
  }
}
