package bot.api;

import bot.dto.beatsavior.BeatSaviorPlayerScores;
import bot.utils.DiscordLogger;
import com.google.gson.Gson;
import com.google.gson.JsonArray;

public class BeatSavior {
    final HttpMethods http;
    final Gson gson;

    public BeatSavior() {
        http = new HttpMethods();
        gson = new Gson();
    }

    public BeatSaviorPlayerScores fetchPlayerMaps(Long playerId) {
        try {
            JsonArray playerMaps = http.fetchJsonArray(ApiConstants.bsavior_LIVESCORES_URL + playerId);
            return gson.fromJson("{\"playerMaps\": " + playerMaps + "}", BeatSaviorPlayerScores.class);
        } catch (Exception e) {
            DiscordLogger.sendLogInChannel(e.getMessage(), DiscordLogger.HTTP_ERRORS);
            return null;
        }
    }
}
