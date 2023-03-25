package bot.main;

import bot.utils.Format;
import org.apache.commons.collections4.map.LinkedMap;
import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class BotConstants {

    public static final String PLAYING = "Please use slash commands now!";
    public static final String RESOURCES_PATH = "src/main/resources/".replace("/", File.separator);
    public static final String donateUrl = "https://www.patreon.com/antilink";
    public static final String featureRequestUrl = "https://github.com/AntiLink99/beatsaber-intelligence-agency-bot/issues";
    public static final String bsiaServerInviteUrl = "https://discord.gg/HXbmf8Xefs";

    public static final long foaaServerId = Long.parseLong(System.getenv("foaa_server_id"));
    public static final long logServerId = Long.parseLong(System.getenv("log_server_id"));
    public static final long bsgServerId = Long.parseLong(System.getenv("bsg_server_id"));

    public static final long foaaOutputChannelId = Long.parseLong(System.getenv("foaa_channel_id"));
    public static final long bsgOutputChannelId = Long.parseLong(System.getenv("bsg_channel_id"));

    public static final String bsgImage = "https://i.imgur.com/PlKkLyM.png";

    // Role milestones
    public static final Integer[] foaaRankMilestones = {10, 50, 100, 200, 300, 400, 500, 600, 700, 800, 900, 1000, 2000, 3000, 4000, 5000};
    public static final Integer[] bsgCountryRankMilestones = {1, 5, 10, 25, 50, 75, 100, 200};
    public static final String topRolePrefix = "Top ";

    public static final Integer[] foaaPpRoleMilestones = {300, 350, 400, 450, 500};
    public static final String[] foaaPpRoles = {"smol pp", "average pp", "big pp", "huge pp", "jumbo pp"};
    public static final String ppRoleSuffix = " PP";

    public static final int milestoneEmoteCount = 10;
    public static final String defaultEmote = ":boom:";

    public static final String[] playlistDifficultyEmotes = {"🟩", "🟦", "🟧", "🟥", "🟪"};
    public static final int improvementReactionMinTimeDiff = 400000000;
    public static final String playlistImageFOAA = getImageBase64("playlistCover.png");
    public static final String playlistImageQualified = getImageBase64("qualified.png");
    public static final String playlistImageRanked = getImageBase64("ranked.png");
    public static final String playlistAuthor = "AntiLink#1337";

    public static final int entriesPerReactionPage = 5;

    // Radar chart
    public static final int chartWidth = 2000;
    public static final int radarWidth = 1000;
    public static final int radarHeight = 1000;
    public static final List<String> validSkills = Arrays.asList("accuracy", "speed", "stamina", "reading");

    // Recent Songs
    public static final String foaaMemberMessage = ":crossed_swords: " + Format.bold("Welcome to the FOAA server! Be sure to check out the channels #rules and #bot.roles for more information.") + " :crossed_swords:";
    public static final String bsgMemberMessage = ":crossed_swords: " + Format.bold("Welcome to the Beat Saber Germany server! Be sure to check out the channels #rules and #roles for more information.") + " :crossed_swords:";

    public static final String imageUrlDiffEasy = "https://i.imgur.com/dIHIsOH.png";
    public static final String imageUrlDiffNormal = "https://i.imgur.com/5BYNku9.png";
    public static final String imageUrlDiffHard = "https://i.imgur.com/o9spva8.png";
    public static final String imageUrlDiffExpert = "https://i.imgur.com/quEBkmC.png";
    public static final String imageUrlDiffExpertPlus = "https://i.imgur.com/9Om0Fyt.png";
    public static final String notOnBeatSaverImageUrl = "https://i.imgur.com/HlC6bM4.png";

    public static Map<String, String> getCommands() {
        Map<String, String> commands = new LinkedMap<>();
        commands.put(Format.underline("Important Links"), Format.bold(
                Format.link("❤ Support AntiLink#1337 and the bot ❤", donateUrl) + "\n"
                + Format.link("⭐ Request features and make suggestions ⭐", featureRequestUrl) + "\n"
                + Format.link(":man_mage: Join the Discord Server for updates :man_mage:", bsiaServerInviteUrl)));
        commands.put("bs register <ScoreSaber URL>", "Registers a player that will be tracked and updated by the bot.");
        commands.put("bs invite", "Shares the invite link for this bot. Feel free to invite it to other servers!");
        commands.put("bs unregister", "Removes a player from the database so that the account is not being updated anymore.");
        commands.put("bs recentsong (optional: <ScoreCount>) (optional: <@member>)", "Displays a player card for the recent score set on ScoreSaber. Use the BeatSavior mod to receive more data.");
        commands.put("bs setgridimage <Image URL>", "Sets a background image for the acc grid in 'bs recentsong'. Leave the URL empty to reset the image.");
        commands.put("bs recentsongs (optional: <PageID>) (optional: <@member>)", "Displays the recently set scores of a player.");
        commands.put("bs topsongs (optional: <PageID>) (optional: <@member>)", "Displays the best scores of a player.");
        commands.put("bs globalrank (optional: <@member>)", "Shows your global rank in comparison to the two players above and below you on ScoreSaber.");
        commands.put("bs localrank (optional: <@member>)", "Shows your local rank in comparison to the two players above and below you on ScoreSaber.");
        commands.put("bs improvement", "Lists the rank difference between the last seven days for all players.");
        commands.put("bs randommeme", "Displays a random meme using the Reddit API.");
        commands.put("bs chart (optional: <@member>)", "Displays a chart with your rank change over the last couple of days.");
        commands.put("bs chartall (optional: <highest> <lowest>)", "Displays a chart with the rank changes of all players over the last couple of days. Default range is #1 - #2000.");
        commands.put("bs playlist <filename> <map keys>", "Automatically creates a playlist with the given map keys and name.");
        commands.put("bs rplaylist <filename> <map keys>", "Creates a playlist just like \"bs playlist\" but asks for specific difficulties. An embed is shown afterwards with the given information and download links for all the maps.");
        commands.put("bs qualified", "Automatically creates a playlist with the current qualified maps from ScoreSaber.");
        commands.put("bs ranked <minStars> <maxStars>", "Automatically creates a playlist with ranked maps in the specified star range.");
        commands.put("bs ranked <amount>", "Automatically creates a playlist with the " + Format.bold("{amount}") + " latest ranked maps.");
        commands.put("bs stand (optional: <@member>)", "Displays a radar chart of the skills the player set for himself.");
        commands.put("bs setskill <skill> <1-10>", "Sets the skill value for the radar chart displayed with \"bs stand\".");
        commands.put("bs say <anything>", "Repeats the given phrase and deletes the message of the sender.");
        commands.put("bs seal", "Cute seal.");
        commands.put("bs help", "Lists all commands, duh.");
        return commands;
    }

    private static String getImageBase64(String imageName) {
        byte[] imageBytes = null;
        try {
            imageBytes = IOUtils.toByteArray(Objects.requireNonNull(BotConstants.class.getClassLoader().getResourceAsStream(imageName)));
        } catch (IOException e) {
            e.printStackTrace();
        }
        String base64 = Base64.getEncoder().encodeToString(imageBytes);
        return "data:image/png;base64," + base64;
    }
}
