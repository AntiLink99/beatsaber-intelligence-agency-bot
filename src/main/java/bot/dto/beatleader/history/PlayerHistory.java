package bot.dto.beatleader.history;

import java.util.List;
import com.google.gson.annotations.SerializedName;

public class PlayerHistory{

	@SerializedName("PlayerHistory")
	private List<PlayerHistoryItem> playerHistory;

	public List<PlayerHistoryItem> getPlayerHistory(){
		return playerHistory;
	}
}