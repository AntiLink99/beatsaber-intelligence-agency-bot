package bot.api;

import java.io.IOException;
import java.util.Arrays;
import java.util.stream.Collectors;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import bot.foaa.dto.Player;

public class ScoreSaber {

	HttpMethods http;
	Gson gson;
	
	public ScoreSaber() {
		http = new HttpMethods();
		gson = new Gson();
	}

	public Player getPlayerById(String playerId) {
		JsonObject response;
		try {
			response = JsonParser.parseString((http.get(ApiConstants.SS_PLAYER_PRE_URL + playerId + ApiConstants.SS_PLAYER_POST_URL))).getAsJsonObject();
		} catch (IOException e) {
			return null;
		}
		JsonObject playerInfo = response.getAsJsonObject("playerInfo");

		Player ssPlayer = gson.fromJson(playerInfo.toString(), Player.class);
		ssPlayer.setHistoryValues(Arrays.asList(ssPlayer.getHistory().split(",")).stream().map(val -> Integer.parseInt(val)).collect(Collectors.toList()));
		return ssPlayer;
	}
}
