package io.github.flamehub.commons.config;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.ReplaceOptions;
import org.bson.Document;
import org.bson.conversions.Bson;

public class MongoConfigRepository {

    private final MongoClient mongoClient;
    private final ObjectMapper objectMapper;

    private final String database;
    private final String collection;

    public MongoConfigRepository(MongoClient mongoClient, ObjectMapper objectMapper, String database, String collection) {
        this.mongoClient = mongoClient;
        this.objectMapper = objectMapper;
        this.database = database;
        this.collection = collection;
    }

    public <C extends MongoConfig> C load(Class<C> configClass, String key) {
        MongoCollection<Document> mongoCollection = this.getCollection();
        Bson query = Filters.eq("_id", key);
        Document document = mongoCollection.find(query).first();
        try {
            return document != null ? this.objectMapper.readValue(document.toJson(), configClass) : null;
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    public <C extends MongoConfig> C save(C entity) {
        String json;
        try {
            json = this.objectMapper.writeValueAsString(entity);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        Document document = Document.parse(json);
        if (document == null) {
            return null;
        }

        MongoCollection<Document> mongoCollection = this.getCollection();
        Bson filters = Filters.eq("_id", entity.getId());
        ReplaceOptions upsert = new ReplaceOptions().upsert(true);
        mongoCollection.replaceOne(filters, document, upsert);
        return entity;
    }

    public MongoCollection<Document> getCollection() {
        return this.mongoClient.getDatabase(this.database).getCollection(this.collection);
    }

}
