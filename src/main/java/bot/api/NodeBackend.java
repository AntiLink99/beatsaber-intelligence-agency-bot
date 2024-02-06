package bot.api;

import bot.dto.backend.ComparisonPlayerData;
import bot.dto.backend.ComparisonRequest;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public class NodeBackend {
    final HttpMethods http;
    final Gson gson;

    public NodeBackend() {
        http = new HttpMethods();
        gson = new Gson();
    }

    public String generateComparisonImage(ComparisonPlayerData player1, ComparisonPlayerData player2) {

        String requestBody = gson.toJson(new ComparisonRequest(player1, player2));
        JsonObject response = http.fetchJsonObjectFromPost(ApiConstants.COMPARISON_URL, requestBody);
        if (response != null) {
            JsonElement imageElement = response.get("image");
            return imageElement.getAsString();
        }
        return null;
    }
}
