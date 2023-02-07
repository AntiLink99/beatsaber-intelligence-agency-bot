package bot.api;

import bot.dto.beatsavior.BeatSaviorPlayerScores;
import bot.utils.DiscordLogger;
import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import org.apache.commons.collections4.map.LinkedMap;

import java.lang.reflect.Type;

public class BeatSavior {
    final HttpMethods http;
    final Gson gson;

    public BeatSavior() {
        http = new HttpMethods();
        gson = new GsonBuilder()
                .registerTypeAdapter(BeatSaviorPlayerScores.class, new ContentDeserializer<BeatSaviorPlayerScores>())
                .create();
    }

    public BeatSaviorPlayerScores fetchPlayerMaps(Long playerId) {
        try {
            JsonArray playerMaps = http.fetchJsonArray(ApiConstants.BSAVIOR_LIVESCORES_URL + playerId);
            return gson.fromJson("{\"playerMaps\": " + playerMaps + "}", BeatSaviorPlayerScores.class);
        } catch (Exception e) {
            e.printStackTrace();
            DiscordLogger.sendLogInChannel(e.getMessage(), DiscordLogger.HTTP_ERRORS);
            return null;
        }
    }

    private static class ContentDeserializer<T> implements JsonDeserializer<T> {
        @Override
        public T deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            System.out.println("CALLED");
            final Gson gson = new Gson();
            final JsonObject object = json.getAsJsonObject();
            final JsonArray playerMaps = object.get("playerMaps").getAsJsonArray();
            for (int i = 0; i < playerMaps.size(); i++) {
                final JsonObject trackersJson = playerMaps.get(i).getAsJsonObject().get("trackers").getAsJsonObject();
                final JsonElement scoreGraph = trackersJson
                        .get("scoreGraphTracker").getAsJsonObject()
                        .get("graph");
                if (scoreGraph.isJsonObject()) {
                    System.out.println("object");
                    continue;
                }
                if (scoreGraph.isJsonArray()) {
                    System.out.println("array");
                    JsonArray scoreGraphArray = scoreGraph.getAsJsonArray();
                    LinkedMap<String, Double> newGraph = new LinkedMap<>();
                    for (int h = 0; h < scoreGraphArray.size(); h++) {
                        JsonElement elem = scoreGraphArray.get(h);
                        if (!elem.isJsonNull()) {
                            newGraph.put(String.valueOf(h), elem.getAsDouble());
                        }
                    }

                    Type listType = new TypeToken<LinkedMap<String, Double>>(){}.getType();
                    JsonElement newGraphElement =  gson.toJsonTree(newGraph, listType);
                    playerMaps.get(i).getAsJsonObject().get("trackers").getAsJsonObject()
                            .get("scoreGraphTracker").getAsJsonObject()
                            .add("graph", newGraphElement);
                }
            }
            object.add("playerMaps", playerMaps);
            return gson.fromJson(object, typeOfT);
        }
    }
}
