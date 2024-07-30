package io.github.flamehub.proxy.core.auth;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.squareup.okhttp.Call;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
import com.squareup.okhttp.ResponseBody;
import java.io.IOException;
import java.util.Map;
import java.util.UUID;

public final class AuthorizationChecker {

  private final static OkHttpClient HTTP_CLIENT = new OkHttpClient();

  private AuthorizationChecker() {

  }

  public static void main(String[] args) {
    Map.Entry<UUID, Boolean> entry = getUUID("Nocekkkkkkk");
    System.out.println(entry.getKey() + " " + entry.getValue());
  }

  public static Map.Entry<UUID, Boolean> getUUID(String name) {
    Request request = new Request.Builder()
        .url("https://mcapi.cloudprotected.net/uuid/" + name)
        .build();

    Call call = HTTP_CLIENT.newCall(request);

    boolean premium = false;
    try {
      Response response = call.execute();

      try (ResponseBody responseBody = response.body()) {
        String string = responseBody.string();
        JsonObject jsonObject = JsonParser.parseString(string).getAsJsonObject();
        JsonArray jsonArray = jsonObject.getAsJsonArray("result");

        if (jsonArray != null && !jsonArray.isEmpty()) {
          JsonObject result = jsonArray.get(0).getAsJsonObject();
          JsonElement element = result.get("uuid-formatted");
          premium = result.get("success").getAsBoolean();
          if (element.isJsonNull()) {
            return Map.entry(UUID.randomUUID(), premium);
          }

          String asString = element.getAsString();
          return Map.entry(UUID.fromString(asString), premium);
        }

        return Map.entry(UUID.randomUUID(), premium);
      }
    } catch (IOException e) {
      return Map.entry(UUID.randomUUID(), premium);
    }
  }

}
