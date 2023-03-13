package bot.api;

import bot.dto.PlayerScore;
import bot.dto.accsaber.PlayerScoreACC;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class AccSaber {

    final HttpMethods http;
    final Gson gson;

    public AccSaber() {
        http = new HttpMethods();
        gson = new Gson();
    }

    public List<PlayerScore> getTopScoresByPlayerIdAndPage(long playerId, int pageNr) {
        int page = 1; //TODO Change this when AccSaber allows Limit, 40 Scores
        String url = ApiConstants.getAccSaberTopScoresURL(String.valueOf(playerId), page);
        List<PlayerScore> recentScores = getPlayerScores(url);
        if (recentScores != null) {
            int offset = pageNr - 1;
            return recentScores.subList(8 * offset, 8 + 8 * offset);
        }
        return Collections.emptyList();
    }

    public List<PlayerScore> getRecentScoresByPlayerIdAndPage(long playerId, int pageNr) {
        int page = 1; //TODO Change this when AccSaber allows Limit, 40 Scores
        String url = ApiConstants.getAccSaberRecentScoresURL(String.valueOf(playerId), page);
        List<PlayerScore> recentScores = getPlayerScores(url);
        if (recentScores != null) {
            int offset = pageNr - 1;
            return recentScores.subList(8 * offset, 8 + 8 * offset);
        }
        return Collections.emptyList();
    }

    @Nullable
    private List<PlayerScore> getPlayerScores(String recentScoresUrl) {
        JsonObject response = http.fetchJsonObject(recentScoresUrl);
        if (response != null) {
            JsonArray topScores = response.getAsJsonArray("scores");
            Type listType = new TypeToken<List<PlayerScoreACC>>() {
            }.getType();
            return gson.fromJson(topScores.toString(), listType);
        }
        return new ArrayList<>();
    }
}
