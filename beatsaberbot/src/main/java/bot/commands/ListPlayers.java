package bot.commands;

import java.util.Collections;
import java.util.List;
import java.util.TreeMap;

import bot.api.ApiConstants;
import bot.db.DatabaseManager;
import bot.dto.player.Player;
import bot.utils.DiscordUtils;
import bot.utils.Format;
import bot.utils.Messages;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;

public class ListPlayers {
	public static void sendRegisteredPlayers(DatabaseManager db, TextChannel channel) {
		List<Player> storedPlayers = db.getAllStoredPlayers();
		List<Member> allMembers = channel.getGuild().getMembers();

		Collections.sort(storedPlayers, (p1, p2) -> String.CASE_INSENSITIVE_ORDER.compare(DiscordUtils.getMemberNameFromId(allMembers, p1.getDiscordUserId()), DiscordUtils.getMemberNameFromId(allMembers, p2.getDiscordUserId())));
		TreeMap<String, String> nameWithUrl = new TreeMap<String, String>();

		if (storedPlayers.isEmpty()) {
			Messages.sendMessage("No players are being tracked! Use \"ru register <URL>\".", channel);
			return;
		}

		for (int index = 0; index < storedPlayers.size(); index++) {
			if (index % 25 == 0 && index != 0) {
				Messages.sendMessageStringMap(nameWithUrl, nameWithUrl.size()+" players found.", channel);
				nameWithUrl.clear();
			}
			Player listPlayer = storedPlayers.get(index);
			nameWithUrl.put(Format.bold(DiscordUtils.getMemberNameFromId(allMembers, listPlayer.getDiscordUserId())), ApiConstants.USER_PRE_URL + listPlayer.getPlayerId());
		}
		Messages.sendMessageStringMap(nameWithUrl, nameWithUrl.size()+" players found.", channel);
	}
}
