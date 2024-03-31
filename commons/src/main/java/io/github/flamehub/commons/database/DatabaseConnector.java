package io.github.flamehub.commons.database;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import org.bson.UuidRepresentation;

import java.util.Collections;

public final class DatabaseConnector {

    private final MongoClient mongoClient;

    public DatabaseConnector(String mongoUri) {
        var settings = MongoClientSettings.builder()
                .applyConnectionString(new ConnectionString(mongoUri))
                .uuidRepresentation(UuidRepresentation.STANDARD)
                .build();
        this.mongoClient = MongoClients.create(settings);
    }

    public MongoClient getMongoClient() {
        return mongoClient;
    }
}
