package bot.commands;

import java.util.List;
import java.util.concurrent.TimeUnit;

import bot.api.ScoreSaber;
import bot.db.DatabaseManager;
import bot.dto.player.Player;
import bot.main.BotConstants;
import bot.utils.ListValueUtils;
import bot.utils.Messages;
import bot.utils.RoleManager;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;

public class UpdatePlayer {
	// TODO make non static for less params
	public static void updatePlayer(Player player, DatabaseManager db, ScoreSaber ss, TextChannel channel) {
		if (player == null) {
			Messages.sendMessage("Could not find player.", channel);
			return;
		}
		Player storedPlayer = db.getPlayerByName(player.getPlayerName());
		Player ssPlayer = player;

		if (storedPlayer == null) {
			Messages.sendMessage("Could not find player \"" + player.getPlayerName() + "\".", channel);
			return;
		}

		if (ssPlayer.getPlayerId() == null) {
			ssPlayer = ss.getPlayerById(storedPlayer.getPlayerId());
		}

		final long playerDiscordId = storedPlayer.getDiscordUserId();
		ssPlayer.setDiscordUserId(playerDiscordId);
		ssPlayer.setCustomAccGridImage(storedPlayer.getCustomAccGridImage());
		Member member = channel.getGuild().getMembers().stream().filter(m -> m.getUser().getIdLong() == playerDiscordId).findFirst().orElse(null);
		if (RoleManager.isNewMilestone(ssPlayer.getRank(), member)) {
			db.updatePlayer(ssPlayer);
			RoleManager.removeMemberRolesByName(member, BotConstants.topRolePrefix);
			RoleManager.assignMilestoneRole(ssPlayer.getRank(), member);
			Messages.sendMilestoneMessage(ssPlayer, channel);
		} else {
			Messages.sendMessage("Player \"" + ssPlayer.getPlayerName() + "\" was not updated since the milestone is still the same. (Top " + ListValueUtils.findMilestoneForRank(ssPlayer.getRank()) + ")", channel);
		}
	}

	public static void updateAllPlayers(DatabaseManager db, ScoreSaber ss, TextChannel channel) {
		try {
			List<Player> storedPlayers = db.getAllStoredPlayers();
			for (Player storedPlayer : storedPlayers) {
				Player ssPlayer = ss.getPlayerById(storedPlayer.getPlayerId());
				ssPlayer.setDiscordUserId(storedPlayer.getDiscordUserId());
				ssPlayer.setCustomAccGridImage(storedPlayer.getCustomAccGridImage());
				try {
					TimeUnit.MILLISECONDS.sleep(125);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				
				final long playerDiscordId = storedPlayer.getDiscordUserId();
				ssPlayer.setDiscordUserId(playerDiscordId);
				Member member = channel.getGuild().getMembers().stream().filter(m -> m.getIdLong() == playerDiscordId).findFirst().orElse(null);
				if (RoleManager.isNewMilestone(ssPlayer.getRank(), member)) {
					boolean success = db.updatePlayer(ssPlayer);
					if (!success) {
						Messages.sendMessage("The player \"" + storedPlayer.getPlayerName() + "\" could not be updated.", channel);
						break;
					}
					RoleManager.removeMemberRolesByName(member, BotConstants.topRolePrefix);
					RoleManager.assignMilestoneRole(ssPlayer.getRank(), member);
					Messages.sendMilestoneMessage(ssPlayer, channel);
				}
			}
			Messages.sendMessage("All players were updated successfully.", channel);
		} catch (Exception e) {
			Messages.sendMessage("Could not fetch players. I probably sent too many requests. Whoops.", channel);
			e.printStackTrace();
		}
	}
}
