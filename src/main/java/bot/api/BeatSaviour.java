package bot.api;

import bot.dto.beatsaviour.BeatSaviourPlayerScores;
import bot.utils.DiscordLogger;
import com.google.gson.Gson;
import com.google.gson.JsonArray;

public class BeatSaviour {
    final HttpMethods http;
    final Gson gson;

    public BeatSaviour() {
        http = new HttpMethods();
        gson = new Gson();
    }

    public BeatSaviourPlayerScores fetchPlayerMaps(Long playerId) {
        try {
            JsonArray playerMaps = http.fetchJsonArray(ApiConstants.BSAVIOUR_LIVESCORES_URL + playerId);
            return gson.fromJson("{\"playerMaps\": " + playerMaps + "}", BeatSaviourPlayerScores.class);
        } catch (Exception e) {
            DiscordLogger.sendLogInChannel(e.getMessage(), DiscordLogger.HTTP_ERRORS);
            return null;
        }
    }
}
