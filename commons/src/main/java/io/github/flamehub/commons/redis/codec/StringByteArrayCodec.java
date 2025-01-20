package io.github.flamehub.commons.redis.codec;

import io.lettuce.core.codec.RedisCodec;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

public final class StringByteArrayCodec implements RedisCodec<String, byte[]> {

  @Override
  public String decodeKey(ByteBuffer bytes) {
    return StandardCharsets.UTF_8.decode(bytes).toString();
  }

  @Override
  public byte[] decodeValue(ByteBuffer bytes) {
    byte[] array = new byte[bytes.remaining()];
    bytes.get(array);
    return array;
  }

  @Override
  public ByteBuffer encodeKey(String key) {
    return StandardCharsets.UTF_8.encode(key);
  }

  @Override
  public ByteBuffer encodeValue(byte[] value) {
    return ByteBuffer.wrap(value);
  }
}
