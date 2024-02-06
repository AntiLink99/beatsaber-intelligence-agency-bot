package bot.commands;

import bot.db.DatabaseManager;
import bot.dto.MessageEventDTO;
import bot.dto.player.DataBasePlayer;
import bot.utils.Messages;

public class HandlePlayerRegisteration {

    final DatabaseManager db;

    public HandlePlayerRegisteration(DatabaseManager db) {
        this.db = db;
    }

    public boolean registerPlayer(DataBasePlayer player, MessageEventDTO event) {
        if (player == null) {
            Messages.sendMessage("No player given. Check for whitespace mistakes.", event);
            return false;
        }
        boolean successSave = db.savePlayer(player);
        if (!successSave) {
            Messages.sendMessage("You are already registered! Use \"ru unregister\" first to remove yourself from the database.", event);
            return false;
        }
        Messages.sendMessage("The player \"" + player.getName() + "\" was bound to you successfully and will be tracked from now on.", event);
        return true;
    }

    public void unregisterPlayer(MessageEventDTO event) {
        long discordUserId = event.getAuthor().getIdLong();
        boolean successDelete = db.deletePlayerByDiscordUserId(discordUserId);
        if (!successDelete) {
            Messages.sendMessage("You are already not registered.", event);
            return;
        }
        Messages.sendMessage("You were unregistered successfully.", event);
    }

}
