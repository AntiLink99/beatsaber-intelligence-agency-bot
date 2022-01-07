package bot.commands;

import bot.db.DatabaseManager;
import bot.dto.player.Player;
import bot.dto.player.PlayerImprovement;
import bot.utils.MapUtils;
import bot.utils.Messages;
import net.dv8tion.jda.api.entities.TextChannel;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Improvement {

    final DatabaseManager db;

    public Improvement(DatabaseManager db) {
        this.db = db;
    }

    public void sendImprovementMessage(TextChannel channel) {
        List<Player> storedPlayers = db.getAllStoredPlayers();
        List<PlayerImprovement> improvements = new ArrayList<>();
        for (Player storedPlayer : storedPlayers) {
            List<Integer> historyValues = storedPlayer.getHistoryValues();

            if (historyValues == null || historyValues.size() < 7) {
                continue;
            }
            int newRank = historyValues.get(historyValues.size() - 1);
            int oldRank = historyValues.get(historyValues.size() - 1 - 7);
            improvements.add(new PlayerImprovement(storedPlayer.getPlayerName(), oldRank, newRank));
        }
        Map<String, String> improvementsString = MapUtils.convertPlayerImprovements(improvements);
        Messages.sendMultiPageMessage(improvementsString, "Improvement over the last seven days", channel);
    }

}
