package bot.main;

import bot.api.BeatLeader;
import bot.api.BeatSaver;
import bot.api.ScoreSaber;
import bot.commands.*;
import bot.commands.chart.PlayerChart;
import bot.commands.chart.RadarStatsChart;
import bot.commands.scoresaber.Qualified;
import bot.commands.scoresaber.Rank;
import bot.commands.scoresaber.Ranked;
import bot.db.DatabaseManager;
import bot.dto.MessageEventDTO;
import bot.dto.beatleader.player.BeatLeaderPlayer;
import bot.dto.player.DataBasePlayer;
import bot.dto.player.PlayerSkills;
import bot.dto.rankedmaps.RankedMaps;
import bot.dto.supporters.SupporterInfo;
import bot.graphics.AccGridImage;
import bot.listeners.PlayerChartListener;
import bot.listeners.RecentSongListener;
import bot.listeners.SongsCommandsListener;
import bot.listeners.SongsCommandsType;
import bot.utils.*;
import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.events.guild.GuildJoinEvent;
import net.dv8tion.jda.api.events.guild.GuildLeaveEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberRemoveEvent;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.MemberCachePolicy;
import net.dv8tion.jda.api.utils.cache.CacheFlag;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.jetbrains.annotations.NotNull;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class BeatSaberBot extends ListenerAdapter {
    static {
        new JFXPanel();
    }

    JDA jda;
    private final ScoreSaber ss;
    private final BeatLeader bl;
    private final BeatSaver bs;
    private final DatabaseManager db;
    private RankedMaps ranked;
    private static boolean hasStarted = false;

    public BeatSaberBot() {
        ss = new ScoreSaber();
        bl = new BeatLeader();
        bs = new BeatSaver();
        db = new DatabaseManager();
        ranked = new RankedMaps();
    }

    public static void main(String[] args) {
        Platform.setImplicitExit(false);

        try {
            initializeBot();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void initializeBot() {
        try {
            BeatSaberBot bot = new BeatSaberBot();
            bot.setupJDA();
            bot.setupLoggingAndLeaderboard();
            bot.registerSlashCommands();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setupJDA() throws Exception {
        JDABuilder builder = JDABuilder.createDefault(System.getenv("bot_token"))
                .addEventListeners(this)
                .enableIntents(
                        GatewayIntent.DIRECT_MESSAGES,
                        GatewayIntent.GUILD_MESSAGES,
                        GatewayIntent.GUILD_MEMBERS,
                        GatewayIntent.GUILD_PRESENCES)
                .setMemberCachePolicy(MemberCachePolicy.ALL)
                .disableCache(CacheFlag.VOICE_STATE, CacheFlag.EMOTE)
                .setActivity(Activity.playing(BotConstants.PLAYING));

        this.jda = builder.build();
        System.out.println("Awaiting JDA ready status...");
        jda.awaitReady();

        hasStarted = true;
    }

    private void setupLoggingAndLeaderboard() {
        Guild loggingGuild = jda.getGuildById(BotConstants.logServerId);
        if (loggingGuild != null) {
            DiscordLogger.setLogGuild(loggingGuild);
        } else {
            System.out.println("Continuing without logging guild.");
        }

        LeaderboardWatcher watcher = new LeaderboardWatcher(db, ss, jda);
        watcher.createNewLeaderboardWatcher();
        watcher.start();
    }

    private void registerSlashCommands() {
        SlashCommands.getCurrentCommands().forEach(c -> jda.upsertCommand(c).queue());
    }

    @Override
    public void onSlashCommand(SlashCommandEvent event) {
        if (!event.isFromGuild() || event.getUser().isBot()) {
            return;
        }
        String message = parseCommandsForHandler(event);
        List<String> msgParts = Arrays.asList(("ru " + message).split(" "));
        event.getHook().setEphemeral(true);
        try {
            handleCommand(msgParts, new MessageEventDTO(event));
        } catch (IOException e) {
            DiscordLogger.sendLogInChannel(ExceptionUtils.getStackTrace(e), DiscordLogger.ERRORS);
            e.printStackTrace();
            event.getHook().sendMessage("An error occurred.").queue();
        }
    }

    private static String parseCommandsForHandler(SlashCommandEvent event) {
        String messageParams = event.getOptions().stream().map(o -> {
            if (o.getType() == OptionType.USER) {
                return "@" + o.getAsString();
            }
            return o.getAsString();
        }).collect(Collectors.joining(" "));
        String message = event.getName() + " " + messageParams;
        return message
                .replaceAll("ranked_by_latest", "ranked")
                .replaceAll("ranked_by_stars", "ranked")
                .replaceAll("playlist_embed", "rplaylist");
    }

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        try {
            if (event.isFromType(ChannelType.PRIVATE) || event.getAuthor().isBot() || !hasStarted) {
                return;
            }
            db.connectToDatabase();
            String msg = event.getMessage().getContentRaw();

            List<String> msgParts = Arrays.asList(msg.split(" "));
            if (!msg.toLowerCase().startsWith("ru ") && !msg.toLowerCase().startsWith("bs ")) {
                return;
            }
            handleCommand(msgParts, new MessageEventDTO(event));
        } catch (Exception e) {
            DiscordLogger.sendLogInChannel(ExceptionUtils.getStackTrace(e), DiscordLogger.ERRORS);
            e.printStackTrace();
        }
    }

    private void handleCommand(List<String> msgParts, MessageEventDTO event) throws IOException {
            TextChannel channel = event.getChannel();
            Guild guild = event.getGuild();
            Member author = event.getAuthor();
            User authorUser = author.getUser();

            db.connectToDatabase();
            fetchRankedMapsIfNonExistent(channel);

            UserInteractionCounter.increment(event.getAuthor().getId());

            if (UserInteractionCounter.shouldSendReminder(event.getAuthor().getId())) {
                Messages.sendReminderMessage(event.getChannel());
            }

            String msg = StringUtils.join(msgParts, " ");
            DataBasePlayer commandPlayer = getCommandPlayer(msgParts, author.getUser());

            event.getChannel().sendTyping().queue();
            DiscordLogger.sendLogInChannel(Format.code("Command: " + msg + "\nRequester: " + author.getEffectiveName() + "\nGuild: " + guild.getName()), "info");
            String command = msgParts.get(1).toLowerCase();
            switch (command) {
                case "register":
                    boolean success = new HandlePlayerRegisteration(db).registerPlayer(commandPlayer, event);
                    if (!success) {
                        break;
                    }
                case "update":
                    new UpdatePlayer(db).updatePlayer(commandPlayer, event);
                    break;
                case "unregister":
                    new HandlePlayerRegisteration(db).unregisterPlayer(event);
                    break;
                case "improvement":
                    new Improvement(db).sendImprovementMessage(channel);
                    break;
                case "chart": {
                    event.getJDA().addEventListener(new PlayerChartListener(commandPlayer, event));
                    break;
                }
                case "chartall":
                    List<DataBasePlayer> storedPlayers = db.getAllStoredPlayers();
                    storedPlayers.removeIf(p -> channel.getGuild().getMemberById(p.getDiscordUserId()) == null);
                    new PlayerChart().sendChartImage(storedPlayers, event, (msgParts.size() >= 3 ? msgParts.get(2) : null));
                    break;
                case "stand": {
                    PlayerSkills skills = db.getPlayerSkillsByDiscordId(commandPlayer.getDiscordUserId());
                    if (skills == null) {
                        Messages.sendMessage("No skill has been set yet. Try \"ru help\".", event);
                        return;
                    }
                    new RadarStatsChart().sendChartImage(skills, event);
                    break;
                }
                case "setskill":
                    SetSkill.setSkill(msgParts, db, event);
                    break;
                case "playlist": {
                    if (msgParts.size() < 3) {
                        Messages.sendMessage("Please provide at least one key.", event);
                        break;
                    }
                    String playlistTitle = msgParts.get(2);
                    List<String> values = msgParts.subList(3, msgParts.size());

                    bs.sendPlaylistInChannelByKeys(values, playlistTitle, BotConstants.playlistImage, channel);
                    break;
                }
                case "rplaylist":
                    if (msgParts.size() < 3) {
                        Messages.sendMessage("Please provide at least one key.", event);
                        break;
                    }
                    String playlistTitle = msgParts.get(2);
                    List<String> values = msgParts.subList(3, msgParts.size());

                    bs.sendRecruitingPlaylistInChannel(values, playlistTitle, BotConstants.playlistImage, event);
                    break;
                case "qualified":
                    new Qualified().sendQualifiedPlaylist(event);
                    break;
                case "ranked":
                    if (msgParts.size() < 3) {
                        Messages.sendMessage("Please provide at least one parameter.", event);
                        break;
                    }
                    List<String> inputValues = msgParts.subList(2, msgParts.size());
                    if (inputValues.size() == 1) {
                        if (!NumberValidation.isInteger(inputValues.get(0))) {
                            Messages.sendMessage("The entered value has to be an integer.", event);
                            return;
                        }
                        new Ranked(ranked).sendRecentRankedPlaylist(Integer.parseInt(inputValues.get(0)), event);
                    } else if (inputValues.size() == 2) {
                        String minString = inputValues.get(0).replaceAll(",", ".");
                        String maxString = inputValues.get(1).replaceAll(",", ".");
                        if (!NumberUtils.isCreatable(minString) || !NumberUtils.isCreatable(maxString)) {
                            Messages.sendMessage("At least one of the entered values is not a number.", event);
                            return;
                        }
                        float min = Float.parseFloat(minString);
                        float max = Float.parseFloat(maxString);
                        if (max < min) {
                            Messages.sendMessage("The min value has to be smaller than the max value.", event);
                            return;
                        }
                        new Ranked(ranked).sendStarRangeRankedPlaylist(min, max, event);
                    } else {
                        Messages.sendMessage("Invalid number of parameters.", event);
                    }
                    break;
                case "randommeme":
                    new RandomMeme().sendRandomMeme(channel);
                    break;
                case "recentsong": {
                    int index = getIndexFromMsgParts(msgParts);
                    if (commandPlayer == null) {
                        Messages.sendMessage("Player was not found.", event);
                        return;
                    }
                    DiscordLogger.sendLogInChannel(event.getAuthor() + " is requesting RecentSong for: " + commandPlayer.getName(), DiscordLogger.INFO);
                    event.getJDA().addEventListener(new RecentSongListener(commandPlayer, index, event, db, ranked));
                    return;
                }
                case "topsong":
                    Messages.sendMessage("Try \"ru topsongs\" to see your top plays! ✨", event);
                    break;
                case "recentsongs": {
                    SupporterInfo supportInfo = db.updateAndRetrieveSupporterInfoByDiscordId(authorUser);
                    int index = getIndexFromMsgParts(msgParts);
                    event.getJDA().addEventListener(new SongsCommandsListener(SongsCommandsType.RECENT, commandPlayer, supportInfo, index, event, db, ranked));
                    return;
                }
                case "topsongs": {
                    SupporterInfo supportInfo = db.updateAndRetrieveSupporterInfoByDiscordId(authorUser);
                    int index = getIndexFromMsgParts(msgParts);
                    event.getJDA().addEventListener(new SongsCommandsListener(SongsCommandsType.TOP, commandPlayer, supportInfo, index, event, db, ranked));
                    return;
                }
                case "localrank": {
                    new Rank(commandPlayer).sendLocalRank(event);
                    break;
                }
                case "globalrank": {
                    new Rank(commandPlayer).sendGlobalRank(event);
                    break;
                }
                case "dachrank":
                    new Rank(commandPlayer).sendDACHRank(event);
                    break;
                case "compare":
                    if (msgParts.size() < 4) {
                        Messages.sendMessage("Please mention two players to compare.", event);
                        break;
                    }
                    String player1DiscordId = msgParts.get(2).replaceAll("<@!?|>", "").replace("@", "");
                    String player2DiscordId = msgParts.get(3).replaceAll("<@!?|>", "").replace("@", "");

                    if (!NumberUtils.isCreatable(player1DiscordId) || !NumberUtils.isCreatable(player2DiscordId)) {
                        Messages.sendMessage("At least one ID is not a valid number.", event);
                        return;
                    }
                    DataBasePlayer player1 = db.getPlayerByDiscordId(Long.parseLong(player1DiscordId));
                    DataBasePlayer player2 = db.getPlayerByDiscordId(Long.parseLong(player2DiscordId));

                    if (player1 == null) {
                        player1 = ss.getPlayerById(player1DiscordId);
                        if (player1 == null) {
                            Messages.sendMessage("The first player was not found in the database or on the leaderboards.", event);
                            break;
                        }
                    }
                    if (player2 == null) {
                        player2 = ss.getPlayerById(player2DiscordId);
                        if (player2 == null) {
                            Messages.sendMessage("The second player was not found in the database or on the leaderboards.", event);
                            break;
                        }
                    }

                    new Compare().generateAndSendCompareImage(player1, player2, event);
                    break;
                case "setgridimage": {
                    if (msgParts.size() < 3) {
                        new AccGridImage(db).resetImage(event);
                        return;
                    }
                    String urlString = msgParts.get(2);
                    List<String> allowedFormats = Arrays.asList(".jpeg", ".jpg", ".png");
                    if (!Format.isUrl(urlString) || allowedFormats.stream().noneMatch(format -> urlString.toLowerCase().contains(format))) {
                        Messages.sendMessage("The given parameter is not an image URL. (Has to contain .png, .jpg or .jpeg)", event);
                        return;
                    }
                    new AccGridImage(db).sendAccGridImage(urlString, event);
                    break;
                }
                case "seal":
                    int randomSealIndex = RandomUtils.getRandomNum(83);
                    String sealFileName = (randomSealIndex < 10 ? "0" : "") + "00" + randomSealIndex + ".jpg";
                    Messages.sendImageEmbed("https://focabot.github.io/random-seal/seals/" + sealFileName, ":seal: Ow, ow, ow! :seal:", event);
                    break;
                case "say":
                    String phrase = msgParts.size() >= 3 ? msgParts.get(2) : "🤡";
                    phrase = StringUtils.replaceChars(phrase, "<!>", "");
                    DiscordLogger.sendLogInChannel(event.getAuthor() + " said: " + phrase, DiscordLogger.INFO);
                    try {
                        event.getMessage().delete().queue();
                    } catch (Exception e) {
                        DiscordLogger.sendLogInChannel("Could not delete message \"" + event.getMessage() + "\"because of lacking permissions.", DiscordLogger.INFO);
                    }
                    Messages.sendPlainMessage(phrase, channel);
                    break;
                case "stats":
                    if (DiscordUtils.isAdmin(authorUser)) {
                        new SendStats().sendStats(event);
                    }
                    break;
                case "setstatus":
                    if (DiscordUtils.isAdmin(authorUser)) {
                        String status = msgParts.size() >= 3 ? msgParts.get(2) : "🤡";
                        event.getJDA().getPresence().setActivity(Activity.playing(status));
                        Messages.sendTempMessage("Status updated to: \"" + status + "\".", 10, channel);
                    }
                    break;
                case "refreshranked":
                    if (DiscordUtils.isAdmin(authorUser)) {
                        ranked = bs.fetchAllRankedMaps();
                    }
                    break;
                case "invite": {
                    Messages.sendMessage(Format.bold("https://discord.com/api/oauth2/authorize?client_id=711323223891116043&permissions=0&scope=bot"), event);
                    break;
                }
                case "leave":
                    if (DiscordUtils.isAdmin(authorUser)) {
                        if (msgParts.size() > 2) {
                            long id = Long.parseLong(msgParts.get(2));
                            Guild guildToLeave = event.getJDA().getGuildById(id);
                            if (guildToLeave != null) {
                                guildToLeave.leave().queue();
                                Messages.sendMessage("Left guild " + guildToLeave.getName() + " successfully.", event);
                            } else {
                                Messages.sendMessage("Could not find guild.", event);
                            }
                        }
                    }
                    break;
                case "help":
                    Messages.sendMultiPageMessage(BotConstants.getCommands(), "🔨 Bot commands 🔨", channel);
                    break;
                default:
                    Messages.sendMessage("Sorry, i don't speak wrong. 🦜  Try \"ru help\".\nIf you want to suggest something to the dev do it " + Format.link("here", BotConstants.featureRequestUrl) + ".", event);
            }
    }

    private int getIndexFromMsgParts(List<String> msgParts) {
        if (msgParts.size() >= 3 && NumberUtils.isCreatable(msgParts.get(2))) {
            return Integer.parseInt(msgParts.get(2));
        }
        return 1;
    }

    private void fetchRankedMapsIfNonExistent(TextChannel channel) {
        ranked = bs.getCachedRankedMaps();
        if (ranked.getRankedMaps() == null && System.getenv("disableRankedRequests") == null) {
            Messages.sendTempMessage("First command after startup, fetching ranked maps. Please wait... 🕒", 10, channel);
            ranked = bs.fetchAllRankedMaps();
        }
    }

    @Override
    public void onGuildMemberRemove(GuildMemberRemoveEvent event) {
        DiscordLogger.sendLogInChannel("Member " + event.getUser().getName() + " left guild " + event.getGuild().getName(), DiscordLogger.USERS);
        long userId = event.getUser().getIdLong();

        List<Guild> guilds = event.getJDA().getGuilds();
        List<Member> members = guilds.stream().flatMap(guild -> guild.getMembers().stream()).collect(Collectors.toList());
        boolean memberNotOnOtherServer = members.stream().noneMatch(m -> m.getUser().getIdLong() == userId);
        if (memberNotOnOtherServer) {
            DiscordLogger.sendLogInChannel("Deleting user if exists: " + event.getUser().getName(), DiscordLogger.USERS);
            db.deletePlayerByDiscordUserId(userId);
        }
    }

    @Override
    public void onGuildMemberJoin(GuildMemberJoinEvent event) {
        if (event.getGuild().getIdLong() == BotConstants.bsgServerId) {
            Messages.sendPrivateMessage(BotConstants.bsgMemberMessage, event.getMember());
            TextChannel welcomeChannel = event.getJDA().getTextChannelById(Long.parseLong(System.getenv("bsg_welcome_channel_id")));
            Messages.sendPlainMessage(Format.bold(event.getMember().getEffectiveName() + ", welcome! 👏👏👏 Member Count: " + event.getGuild().getMemberCount()), welcomeChannel);
            Messages.sendImage(BotConstants.bsgImage, "welcomeImage.png", welcomeChannel);
        }
    }

    @Override
    public void onGuildJoin(GuildJoinEvent event) {
        DiscordLogger.sendLogInChannel(Format.code("Joined guild \"" + event.getGuild().getName() + "\""), DiscordLogger.GUILDS);
    }

    @Override
    public void onGuildLeave(GuildLeaveEvent event) {
        DiscordLogger.sendLogInChannel(Format.code("Left guild \"" + event.getGuild().getName() + "\""), DiscordLogger.GUILDS);
    }

    private DataBasePlayer getCommandPlayer(List<String> msgParts, User author) {
        String lastArg = msgParts.get(msgParts.size() - 1);
        long memberId;

        if (Format.isUrl(lastArg) || (NumberUtils.isCreatable(lastArg) && lastArg.length() > 8)) {
            return getPlayerFromUrlOrDiscordId(lastArg, author.getIdLong());
        }

        memberId = lastArg.contains("@") && NumberUtils.isCreatable(lastArg.replaceAll("[^0-9]", ""))
                ? Long.parseLong(lastArg.replaceAll("[^0-9]", ""))
                : author.getIdLong();

        return getPlayerByDiscordId(memberId);
    }

    private DataBasePlayer getPlayerFromUrlOrDiscordId(String url, long discordUserId) {
        DataBasePlayer player = null;
        try {
            player = getPlayerFromUrl(url);
            if (player != null) {
                player.setDiscordUserId(discordUserId);
            }
        } catch (FileNotFoundException ignored) {
        }
        return player;
    }

    private DataBasePlayer getPlayerByDiscordId(long memberId) {
        DataBasePlayer player = db.getPlayerByDiscordId(memberId);
        if (player == null) {
            BeatLeaderPlayer blPlayer = bl.getPlayerByDiscordID(memberId);
            if (blPlayer != null) {
                player = blPlayer.getAsDatabasePlayer();
            }
        }
        return player;
    }

    @Override
    public void onReady(@NotNull ReadyEvent event) {
        System.out.println("READY!");
    }

    private DataBasePlayer getPlayerFromUrl(String profileUrl) throws FileNotFoundException {
        String id = profileUrl
                .replace("https://scoresaber.com/u/", "")
                .replace("https://www.beatleader.xyz/u/", "");
        if (id.isEmpty()) {
            throw new FileNotFoundException("Player ID is empty!");
        }
        return ss.getPlayerById(id);
    }
}
