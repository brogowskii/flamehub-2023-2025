package io.github.flamehub.commons.message;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import io.github.flamehub.commons.database.DatabaseConnector;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.bson.Document;

public final class MessagesRepository {

  private final DatabaseConnector databaseConnector;
  private final MessagesService messagesService;

  public MessagesRepository(final DatabaseConnector databaseConnector, final MessagesService messagesService) {
    this.databaseConnector = databaseConnector;
    this.messagesService = messagesService;
  }

  public void loadMessages() {
    final MongoDatabase database = databaseConnector.getMongoClient().getDatabase("global");
    final MongoCollection<Document> messages = database.getCollection("messages");

    final FindIterable<Document> documents = messages.find();
    for (final Document document : documents) {
      final JsonObject jsonObject = JsonParser.parseString(document.toJson()).getAsJsonObject();
      final Map<String, List<String>> stringListMap = parseMessages(jsonObject);
      for (final Map.Entry<String, List<String>> stringListEntry : stringListMap.entrySet()) {
        messagesService.getMessageMap()
            .put(stringListEntry.getKey(), stringListEntry.getValue());
      }
    }

  }

  private Map<String, List<String>> parseMessages(final JsonObject localeObject) {
    final Map<String, List<String>> messages = new HashMap<>();

    for (final Map.Entry<String, JsonElement> elementEntry : localeObject.entrySet()) {
      final String key = elementEntry.getKey();
      final JsonElement valueElement = elementEntry.getValue();
      final List<String> values = parseValueElement(valueElement);
      messages.put(key, values);
    }

    return messages;
  }

  private List<String> parseValueElement(final JsonElement valueElement) {
    final List<String> values = new ArrayList<>();

    if (valueElement.isJsonArray()) {
      final JsonArray valueArray = valueElement.getAsJsonArray();
      for (final JsonElement element : valueArray) {
        values.add(element.getAsString());
      }
    } else {
      values.add(valueElement.getAsString());
    }

    return values;
  }


}
