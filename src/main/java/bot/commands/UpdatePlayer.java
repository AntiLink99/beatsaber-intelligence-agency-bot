package bot.commands;

import bot.api.ScoreSaber;
import bot.db.DatabaseManager;
import bot.dto.MessageEventDTO;
import bot.dto.player.DataBasePlayer;
import bot.utils.Messages;

public class UpdatePlayer {

    final DatabaseManager db;

    public UpdatePlayer(DatabaseManager db) {
        this.db = db;
    }

    public void updatePlayer(DataBasePlayer player, MessageEventDTO event) {
        ScoreSaber ss = new ScoreSaber();
        if (player == null) {
            Messages.sendMessage("Could not find player.", event);
            return;
        }
        DataBasePlayer storedPlayer = db.getPlayerByName(player.getName());
        DataBasePlayer ssPlayer = player;

        if (storedPlayer == null) {
            Messages.sendMessage("Could not find player \"" + player.getName() + "\".", event);
            return;
        }

        if (ssPlayer.getId() == null) {
            ssPlayer = ss.getPlayerById(storedPlayer.getId());
        }

        final long playerDiscordId = storedPlayer.getDiscordUserId();
        ssPlayer.setDiscordUserId(playerDiscordId);
        ssPlayer.setCustomAccGridImage(storedPlayer.getCustomAccGridImage());
        Messages.sendMessage("Player \"" + ssPlayer.getName() + "\" updated successfully.", event);
    }
}
