package bot.main;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections4.map.LinkedMap;
import org.apache.commons.io.IOUtils;

import bot.utils.Format;

public class BotConstants {

	public static final String PLAYING = "If you have an idea for a new feature, let AntiLink know: https://gitreports.com/issue/AntiLink99/foaa-bot‍ 💻";

	public static final String sealImageUrl = "https://i.imgur.com/LcCzrxx.jpg";
	public static final long foaaServerId = Long.valueOf(System.getenv("server_id"));
	public static final long outputChannelId = Long.valueOf(System.getenv("channel_id"));
	public static final long foaaCategoryId = 775129325443612693L;

	// Role milestones
	public static final Integer[] rankMilestones = { 10, 50, 100, 200, 300, 400, 500, 600, 700, 800, 900, 1000, 2000, 3000, 4000, 5000 };
	public static final String[] rankEmotes = { "noyourebreathtaking", "hahaball", "pepela", "monkaspeed", "raphtaliapats", "hehe", "giggle", "ayaya", "umarud", "yumyum", "happyotaku", "peepohappy", "tohkahappy", "senkowhat", "smilew", "weirdchamp" };
	public static final String topRolePrefix = "Top ";

	public static final Integer[] ppRoleMilestones = { 300, 350, 400, 450, 500 };
	public static final String[] ppRoles = { "smol pp", "average pp", "big pp", "huge pp", "jumbo pp" };
	public static final String ppRoleSuffix = " PP";

	public static final int milestoneEmoteCount = 10;
	public static final String defaultEmote = ":boom:";

	// Recruiting Server
	public static final long recruitingServerId = 719966895754969208L;
	public static final long recruitRoleId = 720746400614187079L;

	public static final String[] playlistDifficultyEmotes = { "🟩", "🟦", "🟧", "🟥", "🟪" };
	public static final int improvementReactionMinTimeDiff = 400000000;
	public static final String playlistImageFOAA = getImageBase64("foaa.png");
	public static final String playlistImageQualified = getImageBase64("qualified.png");
	public static final String playlistImageRanked = getImageBase64("ranked.png");
	public static final String playlistAuthor = "AntiLink#1337";

	public static final String[] allowedRecruitingCommands = { "rplaylist", "playlist", "deletethat" };

	public static final int entriesPerReactionPage = 5;

	// Radar chart
	public static final int chartWidth = 2000;
	public static final int radarWidth = 1000;
	public static final int radarHeight = 1000;
	public static final List<String> validSkills = Arrays.asList("accuracy", "speed", "stamina", "reading");

	// Recent Songs
	public static final String newMemberMessage = ":crossed_swords: " + Format.bold("Welcome to the FOAA server! Be sure to check out the channels #rules and #roles for more information.") + " :crossed_swords:";

	public static final String imageUrlDiffEasy = "https://i.imgur.com/dIHIsOH.png";
	public static final String imageUrlDiffNormal = "https://i.imgur.com/5BYNku9.png";
	public static final String imageUrlDiffHard = "https://i.imgur.com/o9spva8.png";
	public static final String imageUrlDiffExpert = "https://i.imgur.com/quEBkmC.png";
	public static final String imageUrlDiffExpertPlus = "https://i.imgur.com/9Om0Fyt.png";
	public static final String notOnBeatSaverImageUrl = "https://i.imgur.com/HlC6bM4.png";

	public static Map<String, String> getCommands(boolean isFOAA) {
		Map<String, String> commands = new LinkedMap<String, String>();
		commands.put("ru register <ScoreSaber URL>", "Registers a player that will be tracked and updated by the bot.");
		commands.put("ru registerall", "Lets you enter the ScoreSaber profiles for all unregistered members on the server.");
		commands.put("ru unregister <ScoreSaber URL or name>", "Removes a player from the database so that the account is not being updated anymore.");
		commands.put("ru update <ScoreSaber URL or name>", "Updates the player's role manually if a new milestone was reached.");
		commands.put("ru recentsong (optional: <PageID>) (optional: <@member>)", "Displays a player card for the recent score set on ScoreSaber. Use the BeatSaviour mod to receive more data.");
		commands.put("ru setgridimage <Image URL>", "Sets a background image for the acc grid in 'ru recentsong'. Leave the URL empty to reset the image.");
		commands.put("ru recentsongs (optional: <PageID>) (optional: <@member>)", "Displays the recently set scores of a player.");
		commands.put("ru topsongs (optional: <PageID>) (optional: <@member>)", "Displays the best scores of a player.");
		commands.put("ru improvement", "Lists the rank difference between the last seven days for all players.");
		commands.put("ru list", "Lists all players that are being tracked and updated.");
		if (isFOAA) {
			commands.put("ru claimpp", "Assigns a pp role to you depending on your top play.");
			commands.put("ru randomquote", "Print out a random quote from the #quotes channel.");
		}
		commands.put("ru randommeme", "Displays a random meme using the Reddit API.");
		commands.put("ru chart", "Displays a chart with your rank change over the last couple of days.");
		commands.put("ru chartall (optional: <highest> <lowest>)", "Displays a chart with the rank changes of all players over the last couple of days. Default range is #1 - #2000.");
		commands.put("ru playlist <filename> <map keys>", "Automatically creates a playlist with the given map keys and name.");
		commands.put("ru rplaylist <filename> <map keys>", "Creates a playlist just like \"ru playlist\" but asks for specific difficulties. An embed is shown afterwards with the given information and download links for all the maps.");
		commands.put("ru qualified", "Automatically creates a playlist with the current qualified maps from ScoreSaber.");
		commands.put("ru ranked <minStars> <maxStars>", "Automatically creates a playlist with ranked maps in the specified star range.");
		commands.put("ru ranked <amount>", "Automatically creates a playlist with the " + Format.bold("{amount}") + " latest ranked maps.");
		commands.put("ru stand (optional: <@member>)", "Displays a radar chart of the skills the player set for himself.");
		commands.put("ru setskill <skill> <1-10>", "Sets the skill value for the radar chart displayed with \"ru stand\".");
		commands.put("ru deletethat", "Deletes the latest message from the bot in the channel.");
		commands.put("ru youdidnotseethat", "You did " + Format.underline("not") + " see that.");
		commands.put("ru say <anything>", "Repeats the given phrase and deletes the message of the sender.");
		commands.put("ru seal", "Cute seal.");
		commands.put("ru help", "Lists all commands, duh.");
		return commands;
	}

	public static List<String> getCommandsWithPlayerInfo() {
		List<String> commandsWithPlayerInfo = new ArrayList<String>();
		commandsWithPlayerInfo.add("register");
		commandsWithPlayerInfo.add("unregister");
		commandsWithPlayerInfo.add("update");
		return commandsWithPlayerInfo;
	}

	private static String getImageBase64(String imageName) {
		byte[] imageBytes = null;
		try {
			imageBytes = IOUtils.toByteArray(BotConstants.class.getClassLoader().getResourceAsStream(imageName));
		} catch (IOException e) {
			e.printStackTrace();
		}
		String base64 = Base64.getEncoder().encodeToString(imageBytes);
		return "data:image/png;base64," + base64;
	}
}
