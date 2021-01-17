package bot.api;

import com.google.gson.Gson;
import com.google.gson.JsonArray;

import bot.dto.beatsaviour.BeatSaviourPlayerScores;
import bot.dto.beatsaviour.RankedMaps;

public class BeatSaviour {
	HttpMethods http;
	Gson gson;
	private RankedMaps rankedMaps;

	public BeatSaviour() {
		http = new HttpMethods();
		gson = new Gson();
	}

	public RankedMaps fetchAllRankedMaps() {
		JsonArray rankedMapsJson = http.fetchJsonArray(ApiConstants.BSAVIOUR_RANKED_MAPS_URL);
		rankedMaps = gson.fromJson("{\"rankedMaps\": " + rankedMapsJson + "}", RankedMaps.class);
		return rankedMaps;
	}

	public BeatSaviourPlayerScores fetchPlayerMaps(Long playerId) {
		JsonArray playerMaps = http.fetchJsonArray(ApiConstants.BSAVIOUR_LIVESCORES_URL + playerId);
		return gson.fromJson("{\"playerMaps\": " + playerMaps + "}", BeatSaviourPlayerScores.class);
	}

	public RankedMaps getCachedRankedMaps() {
		return rankedMaps;
	}
}
