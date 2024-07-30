package io.github.flamehub.commons.util;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.BeanDescription;
import com.fasterxml.jackson.databind.DeserializationConfig;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.databind.deser.BeanDeserializerModifier;
import com.fasterxml.jackson.databind.deser.std.DelegatingDeserializer;
import com.fasterxml.jackson.databind.module.SimpleModule;
import java.io.IOException;
import java.util.stream.StreamSupport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Class implementing the functionality of the {@link JsonPostDeserialize} annotation.
 */
public final class JacksonPostHookDeserializer extends DelegatingDeserializer {

  private static final Logger logger = LoggerFactory.getLogger(JacksonPostHookDeserializer.class);

  private final BeanDescription beanDescription;

  private JacksonPostHookDeserializer(JsonDeserializer<?> delegate,
      BeanDescription beanDescription) {
    super(delegate);
    this.beanDescription = beanDescription;
  }

  /**
   * Factory method to return a Module with this deserializer suitable for insertion into an
   * ObjectMapper
   *
   * @return a SimpleModule.
   */
  public static Module getSimpleModule() {
    logger.info("Registering JacksonPostHookDeserializer");
    return new SimpleModule().setDeserializerModifier(new BeanDeserializerModifier() {
      @Override
      public JsonDeserializer<?> modifyDeserializer(DeserializationConfig config,
          BeanDescription beanDescription,
          JsonDeserializer<?> originalDeserializer) {
        if (StreamSupport.stream(beanDescription.getClassInfo().memberMethods().spliterator(),
                true).
            anyMatch(m -> m.hasAnnotation(JsonPostDeserialize.class))) {
          logger.debug("BeanDescription {} ", beanDescription.getClassInfo());

          return new JacksonPostHookDeserializer(originalDeserializer, beanDescription);
        } else {
          return originalDeserializer;
        }
      }
    });
  }

  @Override
  protected JsonDeserializer<?> newDelegatingInstance(JsonDeserializer<?> newDelegatee) {
    return new JacksonPostHookDeserializer(newDelegatee, beanDescription);
  }

  @Override
  public Object deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
    Object deserializedObject = super.deserialize(p, ctxt);

    StreamSupport.stream(beanDescription.getClassInfo().memberMethods().spliterator(), true)
        .forEach(m -> {
          if (m.hasAnnotation(JsonPostDeserialize.class)) {
            logger.debug("Invoking method {} on {} ", m.getName(), beanDescription.getBeanClass());
            try {
              if (m.getAnnotation(JsonPostDeserialize.class).forceAccess()) {
                logger.debug("Modifying access for {}", m);
                m.fixAccess(true);
              }
              m.callOn(deserializedObject);
            } catch (Exception e) {
              logger.error("Caught exception calling method", e);
              throw new RuntimeException(
                  "Failed to call @JsonPostDeserialize annotated method in class " +
                      beanDescription.getClassInfo().getName(), e);
            }
          }
        });
    return deserializedObject;
  }
}