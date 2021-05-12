package bot.commands;

import bot.db.DatabaseManager;
import bot.dto.player.Player;
import bot.main.BotConstants;
import bot.utils.Messages;
import bot.utils.RoleManager;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class HandlePlayerRegisteration {

	public static boolean registerPlayer(Player player, DatabaseManager db, TextChannel channel) {
		boolean successSave = db.savePlayer(player);
		if (!successSave) {
			Messages.sendMessage("The player \"" + player.getPlayerName() + "\" is already registered! Use \"ru unregister <URL / Username>\" to remove the player from the database.", channel);
			return false;
		}
		Messages.sendMessage("Player \"" + player.getPlayerName() + "\" was bound to member successfully and will be tracked from now on.", channel);
		return true;
	}

	public static void unregisterPlayer(Player player, DatabaseManager db, MessageReceivedEvent event) {
		if (player == null) {
			Messages.sendMessage("ScoreSaber URL or name is missing. Try \"ru help\".", event.getChannel());
			return;
		}
		long discordUserId = db.getDiscordIdByPlayerId(player.getPlayerId());
		Member member = event.getGuild().getMemberById(discordUserId);
		if (member == null) {
			Messages.sendMessage("Member for player \"" + player.getPlayerName() + "\" could not be found.", event.getChannel());
			return;
		}
		RoleManager.removeMemberRolesByName(member, BotConstants.topRolePrefix);
		RoleManager.removeMemberRolesByName(member, BotConstants.ppRoleSuffix);
		
		boolean successDelete = db.deletePlayer(player);
		if (!successDelete) {
			Messages.sendMessage("The player \"" + player.getPlayerName() + "\" is already not registered.", event.getChannel());
			return;
		}
		Messages.sendMessage("Player \"" + player.getPlayerName() + "\" was unregistered successfully.", event.getChannel());
	}

}
