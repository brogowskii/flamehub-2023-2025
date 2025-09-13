package io.github.flamehub.commons.database;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import org.bson.UuidRepresentation;

public final class DatabaseConnector {

  private final MongoClient mongoClient;

  public DatabaseConnector(final String mongoUri) {
    final var settings = MongoClientSettings.builder()
        .applyConnectionString(new ConnectionString(mongoUri))
        .uuidRepresentation(UuidRepresentation.STANDARD)
        .build();
    mongoClient = MongoClients.create(settings);
  }

  public MongoClient getMongoClient() {
    return mongoClient;
  }
}
