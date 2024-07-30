package io.github.flamehub.commons.config;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.ReplaceOptions;
import io.github.flamehub.commons.config.serializer.FlameConfigSerializer;
import org.bson.Document;
import org.bson.conversions.Bson;

public final class RemoteRepository {

  private final FlameConfigSerializer serializer;
  private final MongoClient mongoClient;
  private final String database;

  public RemoteRepository(
      final FlameConfigSerializer serializer,
      final MongoClient mongoClient,
      final String database
  ) {
    this.serializer = serializer;
    this.mongoClient = mongoClient;
    this.database = database;
  }

  public <C extends FlameConfig> C load(final Class<C> configClass) {
    EnableRemote remote = configClass.getAnnotation(EnableRemote.class);
    if (remote == null) {
      throw new IllegalArgumentException(
          "Config class must be annotated with @EnableRemote if you want to load it from database."
      );
    }

    MongoCollection<Document> mongoCollection = this.getCollection(remote.collection());
    FlameConfigProperties properties = configClass.getAnnotation(FlameConfigProperties.class);
    if (properties == null) {
      throw new IllegalArgumentException(
          "Config class must be annotated with @FlameConfigProperties");
    }

    Bson query = Filters.eq("_id", properties.name());
    Document document = mongoCollection.find(query).first();
    return document != null ? this.serializer.deserialize(document.toJson(), configClass) : null;
  }

  public <C extends FlameConfig> C save(final C config) {
    FlameConfigProperties properties = config.getProperties();
    if (properties == null) {
      throw new IllegalArgumentException(
          "Config class must be annotated with @FlameConfigProperties");
    }

    String json = this.serializer.serialize(config);
    Document document = Document.parse(json);
    if (document == null) {
      return null;
    }

    EnableRemote remote = config.getRemote();
    if (remote == null) {
      throw new IllegalArgumentException(
          "Config class must be annotated with EnableRemote if you want to save it to database."
      );
    }

    MongoCollection<Document> mongoCollection = this.getCollection(remote.collection());
    Bson filters = Filters.eq("_id", properties.name());
    ReplaceOptions upsert = new ReplaceOptions().upsert(true);
    mongoCollection.replaceOne(filters, document, upsert);
    System.out.println("should be saved " + config.getClass().getName());
    return config;
  }


  public MongoCollection<Document> getCollection(final String collection) {
    return this.mongoClient.getDatabase(this.database).getCollection(collection);
  }

}
