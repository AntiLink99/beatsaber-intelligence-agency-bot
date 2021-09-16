package bot.main;

import bot.utils.Format;
import org.apache.commons.collections4.map.LinkedMap;
import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class BotConstants {

    public static final String PLAYING = "-> ru help <-";
    public static final String RESOURCES_PATH = "src/main/resources/".replace("/", File.separator);
    public static final String sealImageUrl = "https://i.imgur.com/LcCzrxx.jpg";
    public static final String donateUrl = "https://www.patreon.com/antilink";
    public static final String featureRequestUrl = "https://gitreports.com/issue/AntiLink99/beatsaber-intelligence-agency-bot";
    public static final String bsiaServerInviteUrl = "https://discord.gg/HXbmf8Xefs";
    public static final long foaaServerId = Long.parseLong(System.getenv("server_id"));
    public static final long logServerId = Long.parseLong(System.getenv("log_server_id"));
    public static final long outputChannelId = Long.parseLong(System.getenv("channel_id"));

    // Role milestones
    public static final Integer[] rankMilestones = {10, 50, 100, 200, 300, 400, 500, 600, 700, 800, 900, 1000, 2000, 3000, 4000, 5000};
    public static final String[] rankEmotes = {"noyourebreathtaking", "hahaball", "pepela", "monkaspeed", "raphtaliapats", "hehe", "giggle", "ayaya", "umarud", "yumyum", "happyotaku", "peepohappy", "tohkahappy", "senkowhat", "smilew", "weirdchamp"};
    public static final String topRolePrefix = "Top ";

    public static final Integer[] ppRoleMilestones = {300, 350, 400, 450, 500};
    public static final String[] ppRoles = {"smol pp", "average pp", "big pp", "huge pp", "jumbo pp"};
    public static final String ppRoleSuffix = " PP";

    public static final int milestoneEmoteCount = 10;
    public static final String defaultEmote = ":boom:";

    public static final String[] playlistDifficultyEmotes = {"🟩", "🟦", "🟧", "🟥", "🟪"};
    public static final int improvementReactionMinTimeDiff = 400000000;
    public static final String playlistImageFOAA = getImageBase64("foaa.png");
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
    public static final String newMemberMessage = ":crossed_swords: " + Format.bold("Welcome to the FOAA server! Be sure to check out the channels #rules and #roles for more information.") + " :crossed_swords:";

    public static final String imageUrlDiffEasy = "https://i.imgur.com/dIHIsOH.png";
    public static final String imageUrlDiffNormal = "https://i.imgur.com/5BYNku9.png";
    public static final String imageUrlDiffHard = "https://i.imgur.com/o9spva8.png";
    public static final String imageUrlDiffExpert = "https://i.imgur.com/quEBkmC.png";
    public static final String imageUrlDiffExpertPlus = "https://i.imgur.com/9Om0Fyt.png";
    public static final String notOnBeatSaverImageUrl = "https://i.imgur.com/HlC6bM4.png";

    public static Map<String, String> getCommands(boolean isFOAA) {
        Map<String, String> commands = new LinkedMap<>();
        commands.put(Format.underline("Important Links"), Format.bold(
                Format.link("❤ Support AntiLink#1337 and the bot ❤", donateUrl) + "\n"
                + Format.link("⭐ Request features and make suggestions ⭐", featureRequestUrl) + "\n"
                + Format.link(":man_mage: Join the Discord Server for updates :man_mage:", bsiaServerInviteUrl)));
        commands.put("ru register <ScoreSaber URL>", "Registers a player that will be tracked and updated by the bot.");
        commands.put("ru invite", "Shares the invite link for this bot. Feel free to invite it to other servers!");
        commands.put("ru unregister", "Removes a player from the database so that the account is not being updated anymore.");
        commands.put("ru update <ScoreSaber URL>", "Updates the player's role manually if a new milestone was reached.");
        commands.put("ru recentsong (optional: <ScoreCount>) (optional: <@member>)", "Displays a player card for the recent score set on ScoreSaber. Use the BeatSaviour mod to receive more data.");
        commands.put("ru setgridimage <Image URL>", "Sets a background image for the acc grid in 'ru recentsong'. Leave the URL empty to reset the image.");
        commands.put("ru recentsongs (optional: <PageID>) (optional: <@member>)", "Displays the recently set scores of a player.");
        commands.put("ru topsongs (optional: <PageID>) (optional: <@member>)", "Displays the best scores of a player.");
        commands.put("ru globalrank", "Shows your global rank in comparison to the two players above and below you on ScoreSaber.");
        commands.put("ru localrank", "Shows your local rank in comparison to the two players above and below you on ScoreSaber.");
        commands.put("ru improvement", "Lists the rank difference between the last seven days for all players.");
        if (isFOAA) {
            commands.put("ru claimpp", "Assigns a pp role to you depending on your top play.");
            commands.put("ru randomquote", "Print out a random quote from the #quotes channel.");
        }
        commands.put("ru randommeme", "Displays a random meme using the Reddit API.");
        commands.put("ru chart (optional: <@member>)", "Displays a chart with your rank change over the last couple of days.");
        commands.put("ru chartall (optional: <highest> <lowest>)", "Displays a chart with the rank changes of all players over the last couple of days. Default range is #1 - #2000.");
        commands.put("ru playlist <filename> <map keys>", "Automatically creates a playlist with the given map keys and name.");
        commands.put("ru rplaylist <filename> <map keys>", "Creates a playlist just like \"ru playlist\" but asks for specific difficulties. An embed is shown afterwards with the given information and download links for all the maps.");
        commands.put("ru qualified", "Automatically creates a playlist with the current qualified maps from ScoreSaber.");
        commands.put("ru ranked <minStars> <maxStars>", "Automatically creates a playlist with ranked maps in the specified star range.");
        commands.put("ru ranked <amount>", "Automatically creates a playlist with the " + Format.bold("{amount}") + " latest ranked maps.");
        commands.put("ru stand (optional: <@member>)", "Displays a radar chart of the skills the player set for himself.");
        commands.put("ru setskill <skill> <1-10>", "Sets the skill value for the radar chart displayed with \"ru stand\".");
        commands.put("ru deletethat", "Deletes the latest message from the bot in the channel.");
        commands.put("ru say <anything>", "Repeats the given phrase and deletes the message of the sender.");
        commands.put("ru seal", "Cute seal.");
        commands.put("ru help", "Lists all commands, duh.");
        return commands;
    }

    public static List<String> getCommandsWithPlayerInfo() {
        List<String> commandsWithPlayerInfo = new ArrayList<>();
        commandsWithPlayerInfo.add("register");
        commandsWithPlayerInfo.add("update");
        return commandsWithPlayerInfo;
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
