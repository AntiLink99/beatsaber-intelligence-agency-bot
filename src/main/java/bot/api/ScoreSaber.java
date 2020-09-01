package bot.api;

import java.io.IOException;

import org.json.JSONObject;

import com.google.gson.Gson;

import bot.foaa.dto.Player;

public class ScoreSaber {

	JsonHandler handler;
	Gson gson;

	public ScoreSaber() {
		handler = new JsonHandler();
		gson = new Gson();
	}

	public Player getPlayerById(String playerId) {
		JSONObject response;
		try {
			response = handler.getJsonFromUrl(ApiConstants.SS_PLAYER_PRE_URL + playerId + ApiConstants.SS_PLAYER_POST_URL);
		} catch (IOException e) {
			return null;
		}
		JSONObject playerInfo = response.getJSONObject("playerInfo");

		return gson.fromJson(playerInfo.toString(), Player.class);
	}
}
