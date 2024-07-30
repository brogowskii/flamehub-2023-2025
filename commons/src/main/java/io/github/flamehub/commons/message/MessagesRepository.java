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

  public MessagesRepository(DatabaseConnector databaseConnector, MessagesService messagesService) {
    this.databaseConnector = databaseConnector;
    this.messagesService = messagesService;
  }

  public void loadMessages() {
    MongoDatabase database = this.databaseConnector.getMongoClient().getDatabase("global");
    MongoCollection<Document> messages = database.getCollection("messages");

    FindIterable<Document> documents = messages.find();
    for (Document document : documents) {
      JsonObject jsonObject = JsonParser.parseString(document.toJson()).getAsJsonObject();
      Map<String, List<String>> stringListMap = parseMessages(jsonObject);
      for (Map.Entry<String, List<String>> stringListEntry : stringListMap.entrySet()) {
        this.messagesService.getMessageMap()
            .put(stringListEntry.getKey(), stringListEntry.getValue());
      }
    }

  }

  private Map<String, List<String>> parseMessages(JsonObject localeObject) {
    Map<String, List<String>> messages = new HashMap<>();

    for (Map.Entry<String, JsonElement> elementEntry : localeObject.entrySet()) {
      String key = elementEntry.getKey();
      JsonElement valueElement = elementEntry.getValue();
      List<String> values = parseValueElement(valueElement);
      messages.put(key, values);
    }

    return messages;
  }

  private List<String> parseValueElement(JsonElement valueElement) {
    List<String> values = new ArrayList<>();

    if (valueElement.isJsonArray()) {
      JsonArray valueArray = valueElement.getAsJsonArray();
      for (JsonElement element : valueArray) {
        values.add(element.getAsString());
      }
    } else {
      values.add(valueElement.getAsString());
    }

    return values;
  }


}
