package bot.main;

import java.util.Map;

import org.apache.commons.collections4.map.LinkedMap;

public class BotConstants {

	public static final String PLAYING = "with AntiLink's emotions 👨‍💻";
	
	public static final String sealImageUrl = "https://i.imgur.com/LcCzrxx.jpg";
	public static final long foaaServerId = Long.valueOf(System.getenv("server_id"));

	public static final Integer[] rankMilestones = { 50, 100, 200, 300, 400, 500, 600, 700, 800, 900, 1000, 2000, 3000, 4000, 5000 };
	public static final String[] rankEmotes = { "kekw", "pepela", "monkaspeed", "raphtaliapats", "hehe", "giggle", "ayaya", "umarud", "yumyum", "happyotaku", "peepohappy", "tohkahappy", "senkowhat", "smilew", "weirdchamp" };
	public static final String rolePrefix = "Top ";
	public static final int milestoneEmoteCount = 10;
	public static String defaultEmote = ":boom:";
	public final static int entriesPerReactionPage = 5;	

	public static Map<String, String> getCommands() {
		Map<String, String> commands = new LinkedMap<String, String>();
		commands.put("ru register <ScoreSaber URL>", "Registers a player that will be tracked and updated by the bot.");
		commands.put("ru registerall", "Lets you enter the ScoreSaber profiles for all unregistered members on the server.");
		commands.put("ru unregister <ScoreSaber URL or name>", "Removes a player from the database so that the account is not being updated anymore.");
		commands.put("ru update <ScoreSaber URL or name>", "Updates the player's role manually if a new milestone was reached.");
		commands.put("ru improvement", "Lists the rank difference between the last seven days for all players.");
		commands.put("ru list", "Lists all players that are being tracked and updated.");
		commands.put("ru randomquote", "Print out a random quote from the #quotes channel.");
		commands.put("ru say <anything>", "Repeats the given phrase and deletes the message of the sender.");
		commands.put("ru seal", "Cute seal.");
		commands.put("ru help", "Lists all commands, duh.");
		return commands;
	}
}
