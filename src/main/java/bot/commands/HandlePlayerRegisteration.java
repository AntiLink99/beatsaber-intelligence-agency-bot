package bot.commands;

import bot.db.DatabaseManager;
import bot.dto.MessageEventDTO;
import bot.dto.player.DataBasePlayer;
import bot.main.BotConstants;
import bot.roles.RoleManager;
import bot.utils.Messages;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;

public class HandlePlayerRegisteration {

    final DatabaseManager db;

    public HandlePlayerRegisteration(DatabaseManager db) {
        this.db = db;
    }

    public boolean registerPlayer(DataBasePlayer player, TextChannel channel) {
        if (player == null) {
            Messages.sendMessage("No player given. Check for whitespace mistakes.", channel);
            return false;
        }
        boolean successSave = db.savePlayer(player);
        if (!successSave) {
            Messages.sendMessage("You are already registered! Use \"ru unregister\" first to remove yourself from the database.", channel);
            return false;
        }
        Messages.sendMessage("The player \"" + player.getName() + "\" was bound to you successfully and will be tracked from now on.", channel);
        return true;
    }

    public void unregisterPlayer(MessageEventDTO event) {
        long discordUserId = event.getAuthor().getIdLong();
        if (event.getGuild().getIdLong() == BotConstants.foaaServerId) {
            Member member = event.getGuild().getMemberById(discordUserId);
            if (member == null) {
                Messages.sendMessage("Member could not be found.", event.getChannel());
                return;
            }
            RoleManager.removeMemberRolesByName(member, BotConstants.topRolePrefix);
            RoleManager.removeMemberRolesByName(member, BotConstants.ppRoleSuffix);
        }

        boolean successDelete = db.deletePlayerByDiscordUserId(discordUserId);
        if (!successDelete) {
            Messages.sendMessage("You are already not registered.", event.getChannel());
            return;
        }
        Messages.sendMessage("You were unregistered successfully.", event.getChannel());
    }

}
