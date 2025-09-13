package io.github.flamehub.commons.redis.codec;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.ByteBufInputStream;
import io.netty.buffer.ByteBufOutputStream;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import org.apache.fury.Fury;
import org.apache.fury.ThreadSafeFury;
import org.apache.fury.config.FuryBuilder;
import org.apache.fury.config.Language;
import org.apache.fury.io.FuryStreamReader;
import org.apache.fury.memory.MemoryBuffer;
import org.apache.fury.memory.MemoryUtils;
import org.redisson.client.codec.BaseCodec;
import org.redisson.client.handler.State;
import org.redisson.client.protocol.Decoder;
import org.redisson.client.protocol.Encoder;

/**
 * <a href="https://github.com/apache/fury">Apache Fury</a> codec
 * <p>
 * Fully thread-safe.
 *
 * @author Nikita Koksharov
 */
public final class FuryCodec extends BaseCodec {

  private final ThreadSafeFury fury;
  // Używamy modyfikowalnego zbioru, aby umożliwić dynamiczne dodawanie klas
  private final Set<String> allowedClasses;
  private final Language language;
  private final Decoder<Object> decoder = new Decoder<>() {
    @Override
    public Object decode(final ByteBuf buf, final State state) {
      if (buf.nioBufferCount() == 1) {
        final MemoryBuffer furyBuffer = MemoryUtils.wrap(buf.nioBuffer());
        try {
          return fury.deserialize(furyBuffer);
        } finally {
          buf.readerIndex(buf.readerIndex() + furyBuffer.readerIndex());
        }
      }
      return fury.deserialize(FuryStreamReader.of(new ByteBufInputStream(buf)));
    }
  };
  private final Encoder encoder = new Encoder() {
    @Override
    public ByteBuf encode(final Object in) {
      final ByteBuf out = ByteBufAllocator.DEFAULT.buffer();
      MemoryBuffer furyBuffer = null;
      final int remainingSize = out.capacity() - out.writerIndex();
      if (out.hasArray()) {
        furyBuffer = MemoryUtils.wrap(out.array(), out.arrayOffset() + out.writerIndex(),
            remainingSize);
      } else if (out.hasMemoryAddress()) {
        furyBuffer = MemoryUtils.buffer(out.memoryAddress() + out.writerIndex(), remainingSize);
      }
      if (furyBuffer != null) {
        final int size = furyBuffer.size();
        fury.serialize(furyBuffer, in);
        if (furyBuffer.size() > size) {
          out.writeBytes(furyBuffer.getHeapMemory(), 0, furyBuffer.size());
        } else {
          out.writerIndex(out.writerIndex() + furyBuffer.writerIndex());
        }
        return out;
      }
      try {
        final ByteBufOutputStream baos = new ByteBufOutputStream(out);
        fury.serialize(baos, in);
        return baos.buffer();
      } catch (final Exception e) {
        out.release();
        throw e;
      }
    }
  };

  public FuryCodec() {
    this(null, Collections.emptySet(), Language.JAVA);
  }

  public FuryCodec(final Set<String> allowedClasses) {
    this(null, allowedClasses, Language.JAVA);
  }

  public FuryCodec(final Language language) {
    this(null, Collections.emptySet(), language);
  }

  public FuryCodec(final Set<String> allowedClasses, final Language language) {
    this(null, allowedClasses, language);
  }

  public FuryCodec(final ClassLoader classLoader, final FuryCodec codec) {
    this(classLoader, codec.allowedClasses, codec.language);
  }

  public FuryCodec(final ClassLoader classLoader) {
    this(classLoader, Collections.emptySet(), Language.JAVA);
  }

  public FuryCodec(final ClassLoader classLoader, final Set<String> allowedClasses, final Language language) {
    this.allowedClasses = new HashSet<>(allowedClasses);
    this.language = language;

    final FuryBuilder builder = Fury.builder();
    if (classLoader != null) {
      builder.withClassLoader(classLoader);
    }
    builder.withLanguage(language);
    builder.requireClassRegistration(false);
    fury = builder.buildThreadSafeFuryPool(10, 512);
  }

  @Override
  public Decoder<Object> getValueDecoder() {
    return decoder;
  }

  @Override
  public Encoder getValueEncoder() {
    return encoder;
  }
}
