package bot.api;

import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import bot.dto.Player;
import bot.dto.SongScore;

public class ScoreSaber {

	HttpMethods http;
	Gson gson;
	
	public ScoreSaber() {
		http = new HttpMethods();
		gson = new Gson();
	}

	public Player getPlayerById(String playerId) {
		String playerUrl = ApiConstants.SS_PLAYER_PRE_URL + playerId + ApiConstants.SS_PLAYER_POST_URL;
		JsonObject response = http.fetchJson(playerUrl);
		
		if (response == null) {
			System.out.println("Could not find player with url \""+playerUrl+"\".");
			return null;
		}
		JsonObject playerInfo = response.getAsJsonObject("playerInfo");

		Player ssPlayer = gson.fromJson(playerInfo.toString(), Player.class);
		ssPlayer.setHistoryValues(Arrays.asList(ssPlayer.getHistory().split(",")).stream().map(val -> Integer.parseInt(val)).collect(Collectors.toList()));
		return ssPlayer;
	}

	public List<SongScore> getTopScoresByPlayerId(long playerId) {
		String topScoresUrl = ApiConstants.SS_PLAYER_PRE_URL + playerId + ApiConstants.SS_PLAYER_TOP_SCORES_POST_URL;
		JsonObject response = http.fetchJson(topScoresUrl);
		JsonArray topScores = response.getAsJsonArray("scores");

		Type listType = new TypeToken<List<SongScore>>() {}.getType();
		return gson.fromJson(topScores.toString(), listType);
	}
}
