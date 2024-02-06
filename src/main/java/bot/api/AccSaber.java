package bot.api;

import bot.dto.PlayerScore;
import bot.dto.accsaber.AccSaberPlayer;
import bot.dto.accsaber.PlayerScoreACC;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Type;
import java.util.*;

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
        if (recentScores != null && recentScores.size() > 0) {
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

    public Map<String, Integer> getPlayerHistoryValues(String playerId) {
        String url = ApiConstants.getAccSaberUserHistoryURL(String.valueOf(playerId));
        Map<String, Integer> historyValues = getPlayerHistory(url);
        if (historyValues != null) {
            return historyValues;
        }
        return Collections.emptyMap();
    }

    private Map<String, Integer> getPlayerHistory(String url) {
        JsonObject response = http.fetchJsonObject(url);
        if (response != null) {
            Type listType = new TypeToken<Map<String, Integer>>() {}.getType();
            return gson.fromJson(response.toString(), listType);
        }
        return new HashMap<>();
    }

    public AccSaberPlayer getPlayerById(String playerId) {
        String url = ApiConstants.getAccSaberProfileURL(String.valueOf(playerId));
        JsonObject response = http.fetchJsonObject(url);
        if (response != null) {
            return gson.fromJson(response, AccSaberPlayer.class);
        }
        return null;
    }
}
