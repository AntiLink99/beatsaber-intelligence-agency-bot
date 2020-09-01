package bot.main;

import java.util.HashMap;
import java.util.Map;

public class BotConstants {

	public static final String sealImageUrl = "https://i.imgur.com/LcCzrxx.jpg";
	public static final long foaaServerId = Long.valueOf(System.getenv("server_id"));

	public static Map<String, String> getCommands() {
		Map<String, String> commands = new HashMap<String, String>();
		commands.put("ru register <ScoreSaber URL>", "Registers a player that will be tracked and updated by the bot.");
		commands.put("ru registerall", "Lets you enter the ScoreSaber profiles for all unregistered members on the server.");
		commands.put("ru unregister <ScoreSaber URL or name>", "Removes a player from the database so that the account is not being updated anymore.");
		commands.put("ru update <ScoreSaber URL or name>", "Updates the player's role manually if a new milestone was reached.");
		commands.put("ru list", "Lists all players that are being tracked and updated.");
		commands.put("ru randomquote", "Print out a random quote from the #quotes channel.");
		commands.put("ru seal", "Cute seal.");
		commands.put("ru help", "Prints all commands, duh.");
		return commands;
	}
}
