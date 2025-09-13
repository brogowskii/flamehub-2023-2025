package io.github.flamehub.commons.config;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.ReplaceOptions;
import io.github.flamehub.commons.config.serializer.FlameConfigSerializer;
import org.bson.Document;
import org.bson.conversions.Bson;

public final class RemoteRepository {

  private final static String DEFAULT_COLLECTION = "configs";

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

    final MongoCollection<Document> mongoCollection = getCollection(DEFAULT_COLLECTION);
    final FlameConfigProperties properties = configClass.getAnnotation(FlameConfigProperties.class);
    if (properties == null) {
      throw new IllegalArgumentException(
          "Config class must be annotated with @FlameConfigProperties");
    }

    final Bson query = Filters.eq("_id", properties.name());
    final Document document = mongoCollection.find(query).first();
    return document != null ? serializer.deserialize(document.toJson(), configClass) : null;
  }

  public <C extends FlameConfig> C save(final C config) {
    final FlameConfigProperties properties = config.getProperties();
    if (properties == null) {
      throw new IllegalArgumentException(
          "Config class must be annotated with @FlameConfigProperties");
    }

    final String json = serializer.serialize(config);
    final Document document = Document.parse(json);
    if (document == null) {
      return null;
    }

    final MongoCollection<Document> mongoCollection = getCollection(DEFAULT_COLLECTION);
    final Bson filters = Filters.eq("_id", properties.name());
    final ReplaceOptions upsert = new ReplaceOptions().upsert(true);
    mongoCollection.replaceOne(filters, document, upsert);
    System.out.println("should be saved " + config.getClass().getName());
    return config;
  }


  public MongoCollection<Document> getCollection(final String collection) {
    return mongoClient.getDatabase(database).getCollection(collection);
  }

}
