package bot.api;

import bot.dto.ScoreSaberMapData;
import bot.dto.SongScore;
import bot.dto.leaderboards.LeaderboardEntry;
import bot.dto.player.Player;
import bot.scraping.LeaderboardScraper;
import bot.utils.DiscordLogger;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class ScoreSaber {

    final HttpMethods http;
    final Gson gson;
    final LeaderboardScraper leaderboardScraper;

    public ScoreSaber() {
        http = new HttpMethods();
        gson = new Gson();
        leaderboardScraper = new LeaderboardScraper();
    }

    public Player getPlayerById(String playerId) {
        String playerUrl = ApiConstants.SS_PLAYER_PRE_URL + playerId + ApiConstants.SS_PLAYER_POST_URL;
        JsonObject response = http.fetchJsonObject(playerUrl);

        if (response == null) {
            DiscordLogger.sendLogInChannel("Could not find player with url \"" + playerUrl + "\".", DiscordLogger.HTTP_ERRORS);
            return null;
        }
        JsonObject playerInfo = response.getAsJsonObject("playerInfo");

        Player ssPlayer = gson.fromJson(playerInfo.toString(), Player.class);
        ssPlayer.setHistoryValues(Arrays.stream(ssPlayer.getHistory().split(",")).map(Integer::parseInt).collect(Collectors.toList()));
        return ssPlayer;
    }

    public List<SongScore> getTopScoresByPlayerId(long playerId) {
        String topScoresUrl = ApiConstants.SS_PLAYER_PRE_URL + playerId + ApiConstants.SS_PLAYER_TOP_SCORES_POST_URL;
        JsonObject response = http.fetchJsonObject(topScoresUrl);
        if (response != null) {
            JsonArray topScores = response.getAsJsonArray("scores");

            Type listType = new TypeToken<List<SongScore>>() {
            }.getType();
            return gson.fromJson(topScores.toString(), listType);
        }
        return new ArrayList<>();
    }

    public List<SongScore> getTopScoresByPlayerIdAndPage(long playerId, int pageNr) {
        String recentScoresUrl = ApiConstants.SS_PLAYER_PRE_URL + playerId + ApiConstants.SS_PLAYER_TOP_SCORES_POST_URL + "/" + pageNr;
        JsonObject response = http.fetchJsonObject(recentScoresUrl);
        if (response != null) {
            JsonArray topScores = response.getAsJsonArray("scores");
            Type listType = new TypeToken<List<SongScore>>() {
            }.getType();
            return gson.fromJson(topScores.toString(), listType);
        }
        return new ArrayList<>();
    }

    public List<SongScore> getRecentScoresByPlayerId(long playerId) {
        String recentScoresUrl = ApiConstants.SS_PLAYER_PRE_URL + playerId + ApiConstants.SS_PLAYER_RECENT_SCORES_POST_URL;
        JsonObject response = http.fetchJsonObject(recentScoresUrl);
        if (response != null) {
            JsonArray topScores = response.getAsJsonArray("scores");
            Type listType = new TypeToken<List<SongScore>>() {
            }.getType();
            return gson.fromJson(topScores.toString(), listType);
        }
        return new ArrayList<>();
    }

    public List<SongScore> getRecentScoresByPlayerIdAndPage(long playerId, int pageNr) {
        String recentScoresUrl = ApiConstants.SS_PLAYER_PRE_URL + playerId + ApiConstants.SS_PLAYER_RECENT_SCORES_POST_URL + "/" + pageNr;
        JsonObject response = http.fetchJsonObject(recentScoresUrl);
        if (response != null) {
            JsonArray topScores = response.getAsJsonArray("scores");
            Type listType = new TypeToken<List<SongScore>>() {
            }.getType();
            return gson.fromJson(topScores.toString(), listType);
        }
        return new ArrayList<>();
    }

    public List<ScoreSaberMapData> getQualifiedMaps() {
        String qualifiedUrl = ApiConstants.QUALIFIED_URL;
        JsonObject response = http.fetchJsonObject(qualifiedUrl);
        if (response != null) {
            JsonArray qualifiedSongs = response.getAsJsonArray("songs");
            Type listType = new TypeToken<List<ScoreSaberMapData>>() {
            }.getType();
            List<ScoreSaberMapData> qualifiedMaps = gson.fromJson(qualifiedSongs.toString(), listType);
            return qualifiedMaps.stream().filter(map -> !map.isRanked()).collect(Collectors.toList());
        }
        return new ArrayList<>();
    }

    public List<LeaderboardEntry> findLeaderboardEntriesAroundPlayer(Player player, String countryCode, int startPage, int sizeLimit) {
        List<LeaderboardEntry> entries = new ArrayList<>();
        boolean searchingPlayerData = true;
        for (int i = startPage; searchingPlayerData; i++) {
            if (i > 50) {
                return null;
            }
            String leaderboardUrl = getLeaderboardUrl(i, countryCode);
            try {
                Document doc = Jsoup.connect(leaderboardUrl).get();
                List<LeaderboardEntry> docEntries = leaderboardScraper.getLeaderboardEntriesFromDocument(doc);
                entries.addAll(docEntries);
            } catch (IOException e) {
                e.printStackTrace();
            }

            if (entries.size() >= sizeLimit) {
                entries = entries.subList(100, sizeLimit);
            }

            LeaderboardEntry playerEntry = entries.stream().filter(entry -> entry.getPlayerId() == player.getPlayerIdLong()).findFirst().orElse(null);
            boolean playerIsNotInList = playerEntry == null; //Following Player necessary for ru rank
            boolean playerIsAtEndOfList = entries.indexOf(playerEntry) >= entries.size() - 3;
            searchingPlayerData = playerIsNotInList || playerIsAtEndOfList;
        }
        return entries;
    }

    public String getLeaderboardUrl(int pageNr, String countryCode) {
        String url = "https://scoresaber.com/global/" + pageNr;
        if (countryCode != null) {
            url += "?country=" + countryCode;
        }
        return url;
    }
}
