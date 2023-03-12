package bot.api;

import bot.dto.PlayerScore;
import bot.dto.ScoreSaberMapData;
import bot.dto.beatleader.scores.PlayerScoreBL;
import bot.dto.leaderboards.LeaderboardPlayer;
import bot.dto.player.DataBasePlayer;
import bot.utils.DiscordLogger;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class BeatLeader {

    final HttpMethods http;
    final Gson gson;

    public BeatLeader() {
        http = new HttpMethods();
        gson = new Gson();
    }

    public DataBasePlayer getPlayerById(String playerId) {
        String playerUrl = ApiConstants.SS_PLAYER_PRE_URL + playerId + ApiConstants.SS_PLAYER_POST_URL;
        JsonObject response = http.fetchJsonObject(playerUrl);

        if (response == null) {
            DiscordLogger.sendLogInChannel("Could not find player with url \"" + playerUrl + "\".", DiscordLogger.HTTP_ERRORS);
            return null;
        }
        JsonObject playerInfo = response.getAsJsonObject();

        DataBasePlayer ssPlayer = gson.fromJson(playerInfo.toString(), DataBasePlayer.class);
        if (ssPlayer.getHistories().startsWith(",")) {
            ssPlayer.setHistories(ssPlayer.getHistories().substring(1));
        }
        ssPlayer.setHistoryValues(Arrays.stream(ssPlayer.getHistories().split(",")).map(Integer::parseInt).collect(Collectors.toList()));
        return ssPlayer;
    }

    public List<PlayerScore> getTopScoresByPlayerId(long playerId) {
        String url = ApiConstants.getBeatLeaderRecentScoresURL(String.valueOf(playerId), 1);
        return getPlayerScores(url);
    }

    public List<PlayerScore> getTopScoresByPlayerIdAndPage(long playerId, int pageNr) {
        String url = ApiConstants.getBeatLeaderTopScoresURL(String.valueOf(playerId), pageNr);
        return getPlayerScores(url);
    }

    public List<PlayerScore> getRecentScoresByPlayerIdAndPage(long playerId, int pageNr) {
        String url = ApiConstants.getBeatLeaderRecentScoresURL(String.valueOf(playerId), pageNr);
        return getPlayerScores(url);
    }

    @Nullable
    private List<PlayerScore> getPlayerScores(String recentScoresUrl) {
        JsonObject response = http.fetchJsonObject(recentScoresUrl);
        if (response != null) {
            JsonArray topScores = response.getAsJsonArray("data");
            Type listType = new TypeToken<List<PlayerScoreBL>>() {}.getType();
            return gson.fromJson(topScores.toString(), listType);
        }
        return new ArrayList<>();
    }

    public List<ScoreSaberMapData> getQualifiedMaps() {
        String qualifiedUrl = ApiConstants.QUALIFIED_URL;
        JsonObject response = http.fetchJsonObject(qualifiedUrl);
        if (response != null) {
            JsonArray qualifiedSongs = response.getAsJsonArray("songs");
            Type listType = new TypeToken<List<ScoreSaberMapData>>() {}.getType();
            List<ScoreSaberMapData> qualifiedMaps = gson.fromJson(qualifiedSongs.toString(), listType);
            return qualifiedMaps.stream().filter(map -> !map.isRanked()).collect(Collectors.toList());
        }
        return new ArrayList<>();
    }

    public List<LeaderboardPlayer> findLeaderboardEntriesAroundPlayer(DataBasePlayer player, String countryCode, int startPage, int sizeLimit) {
        List<LeaderboardPlayer> entries = new ArrayList<>();
        boolean searchingPlayerData = true;
        for (int i = startPage; searchingPlayerData; i++) {
            if (i > 50) {
                return null;
            }
            String leaderboardUrl = getLeaderboardApiUrl(i, countryCode);
            JsonObject response = http.fetchJsonObject(leaderboardUrl);
            JsonArray responsePlayerList = response.getAsJsonArray("players");
            Type listType = new TypeToken<List<LeaderboardPlayer>>() {}.getType();
            entries.addAll(gson.fromJson(responsePlayerList, listType));

            if (entries.size() >= sizeLimit) {
                entries = entries.subList(100, sizeLimit);
            }

            LeaderboardPlayer playerEntry = entries.stream().filter(entry -> entry.getIdLong() == player.getPlayerIdLong()).findFirst().orElse(null);
            boolean playerIsNotInList = playerEntry == null; //Following Player necessary for ru rank
            boolean playerIsAtEndOfList = entries.indexOf(playerEntry) >= entries.size() - 3;
            searchingPlayerData = playerIsNotInList || playerIsAtEndOfList;
        }
        List<LeaderboardPlayer> finalEntries = entries;
        entries.forEach(entry -> entry.setCustomLeaderboardRank(finalEntries.indexOf(entry) + 1));
        return entries;
    }

    public String getLeaderboardApiUrl(int pageNr, String countryCode) {
        String url = ApiConstants.PLAYER_LEADERBOARDS_API_URL + pageNr;
        if (countryCode != null) {
            url += "&countries=" + countryCode;
        }
        return url;
    }
}