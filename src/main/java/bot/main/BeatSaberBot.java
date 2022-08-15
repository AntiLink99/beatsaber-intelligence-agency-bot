package bot.main;

import bot.api.ApiConstants;
import bot.api.BeatSaver;
import bot.api.ScoreSaber;
import bot.chart.PlayerChart;
import bot.chart.RadarStatsChart;
import bot.commands.*;
import bot.db.DatabaseManager;
import bot.dto.MessageEventDTO;
import bot.dto.player.Player;
import bot.dto.player.PlayerSkills;
import bot.dto.rankedmaps.RankedMaps;
import bot.utils.*;
import javafx.application.Platform;
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
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.utils.cache.CacheFlag;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.jetbrains.annotations.NotNull;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class BeatSaberBot extends ListenerAdapter {

    final ScoreSaber ss = new ScoreSaber();
    final BeatSaver bs = new BeatSaver();
    final DatabaseManager db = new DatabaseManager();
    RankedMaps ranked = new RankedMaps();

    static boolean hasStarted = false;

    final Pattern scoreSaberIDPattern = Pattern.compile(ApiConstants.USER_ID_REGEX);

    public static void main(String[] args) {
        DatabaseManager db = new DatabaseManager();
        ScoreSaber ss = new ScoreSaber();
        Platform.setImplicitExit(false);

        try {
            JDABuilder builder = JDABuilder.createDefault(System.getenv("bot_token"))
                    .addEventListeners(new BeatSaberBot())
                    .disableCache(CacheFlag.VOICE_STATE, CacheFlag.EMOTE)
                    .setActivity(Activity.playing(BotConstants.PLAYING));
            JDA jda = builder.build();
            System.out.println("Awaiting JDA ready status...");
            jda.awaitReady();
            hasStarted = true;

            Guild loggingGuild = jda.getGuildById(BotConstants.logServerId);
            if (loggingGuild != null) {
                DiscordLogger.setLogGuild(loggingGuild);
            } else {
                System.out.println("Continuing without logging guild.");
            }

            LeaderboardWatcher watcher = new LeaderboardWatcher(db, ss, jda);
            watcher.createNewLeaderboardWatcher();
            watcher.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onSlashCommand(SlashCommandEvent event) {
        if (!event.isFromGuild() || event.getUser().isBot()) {
            return;
        }
        String message = event.getOptions().stream().map(OptionMapping::getAsString).collect(Collectors.joining());
        if (message.isEmpty()) {
            return;
        }
        List<String> msgParts = Arrays.asList(("ru " + message).split(" "));
        event.getHook().setEphemeral(true);
        event.deferReply(true).queue();
        try {
            handleCommand(msgParts, new MessageEventDTO(event));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        try {
            if (event.isFromType(ChannelType.PRIVATE) || event.getAuthor().isBot() || !hasStarted) {
                return;
            }

            String msg = event.getMessage().getContentRaw();
            if (!msg.toLowerCase().startsWith("ru ") && !msg.toLowerCase().startsWith("bs ")) {
                return;
            }

            event.getChannel().sendTyping().queue();
            List<String> msgParts = Arrays.asList(msg.split(" "));
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
            String msg = StringUtils.join(msgParts, " ");
            Player commandPlayer = getCommandPlayer(msgParts, channel, author.getUser());

            DiscordLogger.sendLogInChannel(Format.code("Command: " + msg + "\nRequester: " + author.getEffectiveName() + "\nGuild: " + guild.getName()), "info");
            String command = msgParts.get(1).toLowerCase();
            switch (command) {
                case "register":
                    boolean success = new HandlePlayerRegisteration(db).registerPlayer(commandPlayer, channel);
                    if (!success || guild.getIdLong() != BotConstants.foaaServerId) {
                        break;
                    }
                case "update":
                    new UpdatePlayer(db).updatePlayer(commandPlayer, channel);
                case "claimpp":
                    if (guild.getIdLong() == BotConstants.foaaServerId) {
                        new ClaimPPRole(db).validateAndAssignRole(event.getAuthor(), channel, true);
                    }
                    break;
                case "claimppall":
                    if (DiscordUtils.isAdmin(event.getAuthor().getUser())) {
                        new ClaimPPRole(db).validateAndAssignRoleForAll(event);
                    }
                    break;
                case "unregister":
                    new HandlePlayerRegisteration(db).unregisterPlayer(event);
                    break;
                case "registerall":
                    new RegisterAll(db).registerAllMembers(event);
                    break;
                case "improvement":
                    new Improvement(db).sendImprovementMessage(channel);
                    break;
                case "chart": {
                    new PlayerChart().sendChartImage(commandPlayer, event);
                    break;
                }
                case "chartall":
                    List<Player> storedPlayers = db.getAllStoredPlayers();
                    storedPlayers.removeIf(p -> channel.getGuild().getMemberById(p.getDiscordUserId()) == null);
                    new PlayerChart().sendChartImage(storedPlayers, event, (msgParts.size() >= 3 ? msgParts.get(2) : null));
                    break;
                case "stand": {
                    PlayerSkills skills = db.getPlayerSkillsByDiscordId(commandPlayer.getDiscordUserId());
                    if (skills == null) {
                        Messages.sendMessage("No skill has been set yet. Try \"ru help\".", channel);
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
                        Messages.sendMessage("Please provide at least one key.", channel);
                        break;
                    }
                    String playlistTitle = msgParts.get(2);
                    List<String> values = msgParts.subList(3, msgParts.size());

                    bs.sendPlaylistInChannelByKeys(values, playlistTitle, BotConstants.playlistImageFOAA, channel);
                    break;
                }
                case "rplaylist":
                    if (msgParts.size() < 3) {
                        Messages.sendMessage("Please provide at least one key.", channel);
                        break;
                    }
                    String playlistTitle = msgParts.get(2);
                    List<String> values = msgParts.subList(3, msgParts.size());

                    bs.sendRecruitingPlaylistInChannel(values, playlistTitle, BotConstants.playlistImageFOAA, event);
                    break;
                case "qualified":
                    new Qualified().sendQualifiedPlaylist(event);
                    break;
                case "ranked":
                    if (msgParts.size() < 3) {
                        Messages.sendMessage("Please provide at least one parameter.", channel);
                        break;
                    }
                    List<String> inputValues = msgParts.subList(2, msgParts.size());
                    if (inputValues.size() == 1) {
                        if (!NumberValidation.isInteger(inputValues.get(0))) {
                            Messages.sendMessage("The entered value has to be an integer.", channel);
                            return;
                        }
                        new Ranked(ranked).sendRecentRankedPlaylist(Integer.parseInt(inputValues.get(0)), event);
                    } else if (inputValues.size() == 2) {
                        String minString = inputValues.get(0).replaceAll(",", ".");
                        String maxString = inputValues.get(1).replaceAll(",", ".");
                        if (!NumberUtils.isCreatable(minString) || !NumberUtils.isCreatable(maxString)) {
                            Messages.sendMessage("At least one of the entered values is not a number.", channel);
                            return;
                        }
                        float min = Float.parseFloat(minString);
                        float max = Float.parseFloat(maxString);
                        if (max < min) {
                            Messages.sendMessage("The min value has to be smaller than the max value.", channel);
                            return;
                        }
                        new Ranked(ranked).sendStarRangeRankedPlaylist(min, max, event);
                    } else {
                        Messages.sendMessage("Invalid number of parameters.", channel);
                    }
                    break;
                case "randommeme":
                    new RandomMeme().sendRandomMeme(channel);
                    break;
                case "recentsong": {
                    int index = getIndexFromMsgParts(msgParts);
                    DiscordLogger.sendLogInChannel(event.getAuthor() + " is requesting RecentSong for: " + commandPlayer.getName(), DiscordLogger.INFO);
                    new RecentSong(db).sendRecentSong(commandPlayer, ranked, index, event);
                    return;
                }
                case "topsong":
                    Messages.sendMessage("Try \"ru topsongs\" to see your top plays! ✨", channel);
                    break;
                case "recentsongs": {
                    int index = getIndexFromMsgParts(msgParts);
                    new SongsCommands(db, ranked).sendRecentSongs(commandPlayer, index, event);
                    return;
                }
                case "topsongs": {
                    int index = 1;
                    if (msgParts.size() == 3) {
                        String[] arguments = msgParts.get(2).split(" ");
                        String indexOrMemberMention = arguments[0];
                        if (NumberUtils.isCreatable(indexOrMemberMention)) {
                            index = Integer.parseInt(indexOrMemberMention);
                        }
                    }
                    new SongsCommands(db, ranked).sendTopSongs(commandPlayer, index, event);
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
                case "dachrank": {
                    new Rank(commandPlayer).sendDACHRank(event);
                    break;
                }
                case "setgridimage": {
                    if (msgParts.size() < 3) {
                        new AccGridImage(db).resetImage(event);
                        return;
                    }
                    String urlString = msgParts.get(2);
                    List<String> allowedFormats = Arrays.asList(".jpeg", ".jpg", ".png");
                    if (!Format.isUrl(urlString) || allowedFormats.stream().noneMatch(format -> urlString.toLowerCase().contains(format))) {
                        Messages.sendMessage("The given parameter is not an image URL. (Has to contain .png, .jpg or .jpeg)", channel);
                        return;
                    }
                    new AccGridImage(db).sendAccGridImage(urlString, event);
                    break;
                }
                case "seal":
                    int randomSealIndex = RandomUtils.getRandomNum(83);
                    String sealFileName = (randomSealIndex < 10 ? "0" : "") + "00" + randomSealIndex + ".jpg";
                    Messages.sendImageEmbed("https://focabot.github.io/random-seal/seals/" + sealFileName, ":seal: Ow, ow, ow! :seal:", channel);
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
                case "deletethat":
                    List<Message> latestMessages = event.getChannel().getHistory().retrievePast(100).complete();
                    latestMessages = latestMessages.stream().filter(message -> message.getAuthor().getIdLong() == event.getJDA().getSelfUser().getIdLong()).collect(Collectors.toList());

                    if (!latestMessages.isEmpty()) {
                        Message latestMessage = latestMessages.get(0);
                        latestMessage.delete().queue();
                        Messages.sendTempMessage("Message deleted.", 4, channel);
                    } else {
                        Messages.sendTempMessage("I could not find my latest message...", 4, channel);
                    }
                    break;
                case "invite": {
                    Messages.sendMessage(Format.bold("https://discord.com/api/oauth2/authorize?client_id=711323223891116043&permissions=0&scope=bot"), channel);
                    break;
                }
                case "leave":
                    if (DiscordUtils.isAdmin(authorUser)) {
                        if (msgParts.size() > 2) {
                            long id = Long.parseLong(msgParts.get(2));
                            Guild guildToLeave = event.getJDA().getGuildById(id);
                            if (guildToLeave != null) {
                                guildToLeave.leave().queue();
                                Messages.sendMessage("Left guild " + guildToLeave.getName() + " successfully.", channel);
                            } else {
                                Messages.sendMessage("Could not find guild.", channel);
                            }
                        }
                    }
                    break;
                case "help":
                    boolean isFOAA = guild.getIdLong() == BotConstants.foaaServerId;
                    Messages.sendMultiPageMessage(BotConstants.getCommands(isFOAA), "🔨 Bot commands 🔨", channel);
                    break;
                default:
                    Messages.sendMessage("Sorry, i don't speak wrong. 🤡  Try \"ru help\".\nIf you want to suggest something to the dev do it " + Format.link("here", BotConstants.featureRequestUrl) + ".", channel);
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
        if (event.getGuild().getIdLong() == BotConstants.foaaServerId) {
            Messages.sendPrivateMessage(BotConstants.newMemberMessage, event.getMember());
            TextChannel botChannel = event.getJDA().getTextChannelById(Long.parseLong(System.getenv("foaa_channel_id")));
            Messages.sendPlainMessage(Format.bold(event.getMember().getAsMention() + ", welcome!") + " Be sure to register yourself here with " + Format.underline("\"ru register <ScoreSaber URL>\"") + " to obtain your bot.roles.", botChannel);
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

    private Player getCommandPlayer(List<String> msgParts, TextChannel channel, User author) {
        Player player = null;
        String lastArgument = msgParts.get(msgParts.size() - 1);

        if (Format.isUrl(lastArgument)) {
            try {
                player = getScoreSaberPlayerFromUrl(lastArgument);
                if (player != null) {
                    player.setDiscordUserId(author.getIdLong());
                }
            } catch (FileNotFoundException e) {
                Messages.sendMessage(e.getMessage(), channel);
            }
        } else {
            long memberId = 0;
            if (lastArgument.contains("@")) {
                String mentionedMemberId = lastArgument.replaceAll("[^0-9]", "");
                if (NumberUtils.isCreatable(mentionedMemberId)) {
                    memberId = Long.parseLong(mentionedMemberId); //Mention
                }
            } else {
                memberId = author.getIdLong(); //None
            }
            player = db.getPlayerByDiscordId(memberId);
        }
        return player;
    }

    @Override
    public void onReady(@NotNull ReadyEvent event) {
        System.out.println("Ready!");
    }

    private Player getScoreSaberPlayerFromUrl(String profileUrl) throws FileNotFoundException {
        Matcher matcher = scoreSaberIDPattern.matcher(profileUrl);
        if (!matcher.find()) {
            return null;
        }
        String playerId = matcher.group(1);
        Player player = ss.getPlayerById(playerId);
        if (player == null) {
            throw new FileNotFoundException("Player could not be found!");
        }
        return player;
    }
}
