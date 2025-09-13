package io.github.flamehub.armor.upgrade;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.IOException;
import java.util.UUID;
import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

public final class ArmorHeadValueFetcher {

  private final static OkHttpClient OK_HTTP_CLIENT = new OkHttpClient();

//    public static void main(String[] args) {
//        String valueFromUUID = getValueFromUUID(UUID.fromString("d1629f6e-3d77-48b1-bd6b-d6370fa5848a"));
//        System.out.println(valueFromUUID);
//    }

  public static String getValueFromUUID(final UUID uuid) {
    final String url =
        "https://sessionserver.mojang.com/session/minecraft/profile/" + uuid.toString();

    final Request request = new Request.Builder()
        .url(url)
        .build();

    final Call call = OK_HTTP_CLIENT.newCall(request);
    try (
        final Response execute = call.execute();
        final ResponseBody body = execute.body()
    ) {

      final String string = body.string();
      final JsonObject jsonObject = JsonParser.parseString(string).getAsJsonObject();
      final JsonArray jsonArray = jsonObject.getAsJsonArray("properties");
      if (jsonArray != null && !jsonArray.isEmpty()) {
        final JsonObject result = jsonArray.get(0).getAsJsonObject();
        return result.get("value").getAsString();
      }

    } catch (final IOException e) {
      e.printStackTrace();
    }

    return null;
  }

}
