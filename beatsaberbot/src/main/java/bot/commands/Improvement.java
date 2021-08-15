package bot.commands;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import bot.db.DatabaseManager;
import bot.dto.player.Player;
import bot.dto.player.PlayerImprovement;
import bot.utils.MapUtils;
import bot.utils.Messages;
import net.dv8tion.jda.api.entities.TextChannel;

public class Improvement {

	public static void sendImprovementMessage(DatabaseManager db, TextChannel channel) {
		List<Player> storedPlayers = db.getAllStoredPlayers();
		List<PlayerImprovement> improvements = new ArrayList<PlayerImprovement>();
		for (Player storedPlayer : storedPlayers) {
			List<Integer> historyValues = storedPlayer.getHistoryValues();

			int newRank = historyValues.get(historyValues.size() - 1);
			int oldRank = historyValues.get(historyValues.size() - 1 - 7);
			improvements.add(new PlayerImprovement(storedPlayer.getPlayerName(), oldRank, newRank));
		}
		Map<String, String> improvementsString = MapUtils.convertPlayerImprovements(improvements);
		Messages.sendMultiPageMessage(improvementsString, "Improvement over the last seven days", channel);
	}

}
