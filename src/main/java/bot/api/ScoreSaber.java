package bot.api;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import bot.dto.ScoreSaberMapData;
import bot.dto.SongScore;
import bot.dto.player.Player;
import bot.utils.DiscordLogger;

public class ScoreSaber {

	HttpMethods http;
	Gson gson;

	public ScoreSaber() {
		http = new HttpMethods();
		gson = new Gson();
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
		ssPlayer.setHistoryValues(Arrays.asList(ssPlayer.getHistory().split(",")).stream().map(val -> Integer.parseInt(val)).collect(Collectors.toList()));
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
		return new ArrayList<SongScore>();
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
		return new ArrayList<SongScore>();
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
		return new ArrayList<SongScore>();
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
		return new ArrayList<SongScore>();
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
		return new ArrayList<ScoreSaberMapData>();
	}

}
