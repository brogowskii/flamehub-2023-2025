package io.github.flamehub.proxy.core.auth.util;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.squareup.okhttp.*;

import java.io.IOException;

public final class AuthorizationChecker {

    private final static OkHttpClient HTTP_CLIENT = new OkHttpClient();

    private AuthorizationChecker() {

    }

    public static boolean isPremiumAccount(String name) {
        Request request = new Request.Builder()
                .url("https://mcapi.cloudprotected.net/uuid/" + name)
                .build();

        Call call = HTTP_CLIENT.newCall(request);

        try {
            Response response = call.execute();
            try (ResponseBody responseBody = response.body()) {
                String string = responseBody.string();
                JsonObject jsonObject = JsonParser.parseString(string).getAsJsonObject();
                JsonArray jsonArray = jsonObject.getAsJsonArray("result");

                if (jsonArray != null && jsonArray.size() > 0) {
                    JsonObject result = jsonArray.get(0).getAsJsonObject();
                    return result.get("success").getAsBoolean();
                } else {
                    return false;
                }
            }
        } catch (IOException e) {
            return false;
        }
    }
}
