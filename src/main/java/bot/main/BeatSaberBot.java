package bot.main;

import bot.api.ApiConstants;
import bot.api.BeatSaver;
import bot.api.BeatSaviour;
import bot.api.ScoreSaber;
import bot.chart.PlayerChart;
import bot.chart.RadarStatsChart;
import bot.commands.*;
import bot.db.DatabaseManager;
import bot.dto.MessageEventDTO;
import bot.dto.beatsaviour.RankedMaps;
import bot.dto.beatsaviour.RankedMapsFilterRequest;
import bot.dto.player.Player;
import bot.dto.player.PlayerSkills;
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
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.ChunkingFilter;
import net.dv8tion.jda.api.utils.MemberCachePolicy;
import net.dv8tion.jda.api.utils.cache.CacheFlag;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.jetbrains.annotations.NotNull;

import javax.security.auth.login.LoginException;
import java.io.FileNotFoundException;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class BeatSaberBot extends ListenerAdapter {

    final ScoreSaber ss = new ScoreSaber();
    final BeatSaver bs = new BeatSaver();
    final BeatSaviour saviour = new BeatSaviour();
    final DatabaseManager db = new DatabaseManager();
    RankedMaps ranked = new RankedMaps();

    final Pattern scoreSaberIDPattern = Pattern.compile(ApiConstants.USER_ID_REGEX);

    public static void main(String[] args) {
        //new Patreon().fetchPatreonSupporters().forEach(a -> System.out.println(a.getDiscordId()));
        DatabaseManager db = new DatabaseManager();
        ScoreSaber ss = new ScoreSaber();
        Platform.setImplicitExit(false);

        try {
            JDABuilder builder = JDABuilder.createDefault(System.getenv("bot_token")).setMemberCachePolicy(MemberCachePolicy.ALL).enableIntents(GatewayIntent.GUILD_MEMBERS).setChunkingFilter(ChunkingFilter.ALL).addEventListeners(new BeatSaberBot())
                    .disableCache(CacheFlag.VOICE_STATE, CacheFlag.EMOTE).setActivity(Activity.playing(BotConstants.PLAYING));

            JDA jda = builder.build();
            try {
                jda.awaitReady();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            DiscordLogger.setLogGuild(jda.getGuildById(BotConstants.logServerId));

            TextChannel botChannel = jda.getTextChannelById(BotConstants.outputChannelId);
            Runnable runnable = () -> {
                db.connectToDatabase();
                DiscordLogger.sendLogInChannel("----- Starting User Refresh... [" + LocalTime.now().getHour() + ":" + LocalTime.now().getMinute() + "]", DiscordLogger.FOAA_REFRESH);
                try {
                    int fetchCounter = 0;
                    List<Player> players = db.getAllStoredPlayers();
                    for (Player storedPlayer : players) {
                        Player ssPlayer = ss.getPlayerById(storedPlayer.getPlayerId());
                        if (ssPlayer == null) {
                            continue;
                        }
                        ssPlayer.setDiscordUserId(storedPlayer.getDiscordUserId());
                        ssPlayer.setCustomAccGridImage(storedPlayer.getCustomAccGridImage());
                        if (ssPlayer.getRank() != storedPlayer.getRank() && ssPlayer.getRank() != 0) {
                            db.updatePlayer(ssPlayer);
                            if (botChannel.getGuild().getIdLong() == BotConstants.foaaServerId) {
                                Member member = botChannel.getGuild().getMembers().stream().filter(m -> m.getUser().getIdLong() == ssPlayer.getDiscordUserId()).findFirst().orElse(null);
                                if (RoleManager.isNewMilestone(ssPlayer.getRank(), member)) {
                                    DiscordLogger.sendLogInChannel("Changed role: " + ssPlayer.getPlayerName() + " New Rank: " + ssPlayer.getRank() + " - Old Rank: " + storedPlayer.getRank() + "   " + "(Top " + ListValueUtils.findMilestoneForRank(ssPlayer.getRank()) + ")",
                                            DiscordLogger.FOAA_REFRESH);
                                    RoleManager.removeMemberRolesByName(member, BotConstants.topRolePrefix);
                                    RoleManager.assignMilestoneRole(ssPlayer.getRank(), member);
                                    Messages.sendMilestoneMessage(ssPlayer, botChannel);
                                }
                            }
                        }

                        try {
                            TimeUnit.MILLISECONDS.sleep(100);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        fetchCounter++;

                        if (fetchCounter % 20 == 0) {
                            TimeUnit.MINUTES.sleep(1);
                        }
                    }
                } catch (Exception e) {
                    DiscordLogger.sendLogInChannel("There was an exception in scheduled task: " + e.getMessage(), DiscordLogger.FOAA_REFRESH);
                }
                System.gc();
            };

            ScheduledExecutorService service = Executors.newSingleThreadScheduledExecutor();
            service.scheduleAtFixedRate(runnable, 0, 20, TimeUnit.MINUTES);
        } catch (LoginException e) {
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
        handleCommand(msgParts, new MessageEventDTO(event));
        event.reply("a").queue();
    }

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        if (event.isFromType(ChannelType.PRIVATE) || event.getAuthor().isBot()) {
            return;
        }

        String msg = event.getMessage().getContentRaw();
        if (!msg.toLowerCase().startsWith("ru ")) {
            return;
        }

        event.getChannel().sendTyping().queue();
        List<String> msgParts = Arrays.asList(msg.split(" ", 3));
        handleCommand(msgParts, new MessageEventDTO(event));
    }

    private void handleCommand(List<String> msgParts, MessageEventDTO event) {
        TextChannel channel = event.getChannel();
        Guild guild = event.getGuild();
        Member author = event.getAuthor();
        User authorUser = author.getUser();

        fetchRankedMapsIfNonExistant(channel);
        String msg = StringUtils.join(msgParts, " ");
        Player player = getPlayerDependingOnCommand(msgParts, channel, author.getUser()); // Refactor!!!
        db.connectToDatabase();

        DiscordLogger.sendLogInChannel(Format.code("Command: " + msg + "\nRequester: " + author.getEffectiveName() + "\nGuild: " + guild.getName()), "info");
        String command = msgParts.get(1).toLowerCase();
        switch (command) {
            case "register":
                boolean success = new HandlePlayerRegisteration(db).registerPlayer(player, channel);
                if (!success || guild.getIdLong() != BotConstants.foaaServerId) {
                    break;
                }
            case "update":
                new UpdatePlayer(db).updatePlayer(player, channel);
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
            case "updateall":
                if (DiscordUtils.isAdmin(authorUser)) {
                    new UpdatePlayer(db).updateAllPlayers(channel);
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
                long memberId = event.getAuthor().getIdLong();
                if (msgParts.size() == 3) {
                    try {
                        memberId = Long.parseLong(msgParts.get(2).replaceAll("[^0-9]", ""));
                    } catch (NumberFormatException e) {
                        Messages.sendMessage("Invalid user specified.", channel);
                        return;
                    }
                }
                Player storedPlayer = db.getPlayerByDiscordId(memberId);
                if (storedPlayer == null) {
                    Messages.sendMessage("The player is not registered. Try \"ru help\".", channel);
                    return;
                }
                new PlayerChart().sendChartImage(storedPlayer, event);
                break;
            }
            case "chartall":
                List<Player> storedPlayers = db.getAllStoredPlayers();
                storedPlayers.removeIf(p -> channel.getGuild().getMemberById(p.getDiscordUserId()) == null);
                new PlayerChart().sendChartImage(storedPlayers, event, (msgParts.size() >= 3 ? msgParts.get(2) : null));
                break;
            case "stand": {
                long memberId = event.getAuthor().getIdLong();
                if (msgParts.size() == 3) {
                    try {
                        memberId = Long.parseLong(msgParts.get(2).replaceAll("[^0-9]", ""));
                    } catch (NumberFormatException e) {
                        Messages.sendMessage("Invalid user specified.", channel);
                        return;
                    }
                }
                PlayerSkills skills = db.getPlayerSkillsByDiscordId(memberId);
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
                List<String> values = new LinkedList<>(Arrays.asList(msgParts.get(2).split(" ")));
                String playlistTitle = values.get(0);
                values.remove(playlistTitle);

                bs.sendPlaylistInChannelByKeys(values, playlistTitle, BotConstants.playlistImageFOAA, channel);
                break;
            }
            case "rplaylist":
                if (msgParts.size() < 3) {
                    Messages.sendMessage("Please provide at least one key.", channel);
                    break;
                }
                List<String> values = new LinkedList<>(Arrays.asList(msgParts.get(2).split(" ")));
                String playlistTitle = values.get(0);
                values.remove(playlistTitle);

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
                String[] inputValues = msgParts.get(2).split(" ");
                if (inputValues.length == 1) {
                    if (!NumberValidation.isInteger(inputValues[0])) {
                        Messages.sendMessage("The entered value has to be an integer.", channel);
                        return;
                    }
                    new Ranked(ranked).sendRecentRankedPlaylist(Integer.parseInt(inputValues[0]), event);
                } else if (inputValues.length == 2) {
                    String minString = inputValues[0].replaceAll(",", ".");
                    String maxString = inputValues[1].replaceAll(",", ".");
                    if (!NumberUtils.isNumber(minString) || !NumberUtils.isNumber(maxString)) {
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
                long memberId = event.getAuthor().getIdLong();
                int index = 1;
                if (msgParts.size() == 3) {
                    String[] arguments = msgParts.get(2).split(" ");
                    String indexOrMemberMention = arguments[0];
                    if (NumberUtils.isNumber(indexOrMemberMention)) {
                        index = Integer.parseInt(indexOrMemberMention);
                    }
                    String lastArgument = arguments[arguments.length - 1];
                    if (lastArgument.contains("@")) { // Mention
                        try {
                            memberId = Long.parseLong(lastArgument.replaceAll("[^0-9]", ""));
                        } catch (NumberFormatException e) {
                            Messages.sendMessage("Invalid user specified.", channel);
                            return;
                        }
                    }
                }
                Player storedPlayer = db.getPlayerByDiscordId(memberId);
                if (storedPlayer == null) {
                    Messages.sendMessage("Player could not be found. Please check if the user has linked his account.", channel);
                    return;
                } else {
                    DiscordLogger.sendLogInChannel(event.getAuthor() + " is requesting RecentSong for: " + storedPlayer.getPlayerName(), DiscordLogger.INFO);
                    new RecentSong(db).sendRecentSong(storedPlayer, ranked, index, event);
                }
                return;
            }
            case "topsong":
                Messages.sendMessage("Try \"ru topsongs\" to see your top plays! ✨", channel);
                break;
            case "recentsongs": {
                long memberId = event.getAuthor().getIdLong();
                int index = 1;
                if (msgParts.size() == 3) {
                    String[] arguments = msgParts.get(2).split(" ");
                    String indexOrMemberMention = arguments[0];
                    if (NumberUtils.isNumber(indexOrMemberMention)) {
                        index = Integer.parseInt(indexOrMemberMention);
                    }
                    String lastArgument = arguments[arguments.length - 1];
                    if (lastArgument.contains("@")) { // Mention
                        try {
                            memberId = Long.parseLong(lastArgument.replaceAll("[^0-9]", ""));
                        } catch (NumberFormatException e) {
                            Messages.sendMessage("Invalid user specified.", channel);
                            return;
                        }
                    }
                }
                Player storedPlayer = db.getPlayerByDiscordId(memberId);
                if (storedPlayer == null) {
                    Messages.sendMessage("Player could not be found. Please check if the user has linked his account.", channel);
                    return;
                }
                new SongsCommands(db, ranked).sendRecentSongs(storedPlayer, index, event);
                return;
            }
            case "topsongs": {
                long memberId = event.getAuthor().getIdLong();
                int index = 1;
                if (msgParts.size() == 3) {
                    String[] arguments = msgParts.get(2).split(" ");
                    String indexOrMemberMention = arguments[0];
                    if (NumberUtils.isNumber(indexOrMemberMention)) {
                        index = Integer.parseInt(indexOrMemberMention);
                    }
                    String lastArgument = arguments[arguments.length - 1];
                    if (lastArgument.contains("@")) { // Mention
                        try {
                            memberId = Long.parseLong(lastArgument.replaceAll("[^0-9]", ""));
                        } catch (NumberFormatException e) {
                            Messages.sendMessage("Invalid user specified.", channel);
                            return;
                        }
                    }
                }
                Player storedPlayer = db.getPlayerByDiscordId(memberId);
                if (storedPlayer == null) {
                    Messages.sendMessage("Player could not be found. Please check if the user has linked his account.", channel);
                    return;
                }
                new SongsCommands(db, ranked).sendTopSongs(storedPlayer, index, event);
                return;
            }
            case "localrank": {
                Player storedPlayer = db.getPlayerByDiscordId(event.getAuthor().getIdLong());
                if (storedPlayer == null) {
                    Messages.sendMessage("Player could not be found. Please check if the user has linked his account.", channel);
                    return;
                }
                new Rank().sendLocalRank(storedPlayer, event);
                break;
            }
            case "globalrank": {
                Player storedPlayer = db.getPlayerByDiscordId(event.getAuthor().getIdLong());
                if (storedPlayer == null) {
                    Messages.sendMessage("Player could not be found. Please check if the user has linked his account.", channel);
                    return;
                }
                new Rank().sendGlobalRank(storedPlayer, event);
                break;
            }
            case "dachrank": {
                Player storedPlayer = db.getPlayerByDiscordId(event.getAuthor().getIdLong());
                if (storedPlayer == null) {
                    Messages.sendMessage("Player could not be found. Please check if the user has linked his account.", channel);
                    return;
                }
                new Rank().sendDACHRank(storedPlayer, event);
                break;
            }
/*            case "profile": {
                Player storedPlayer = db.getPlayerByDiscordId(event.getAuthor().getIdLong());
                if (storedPlayer == null) {
                    Messages.sendMessage("Player could not be found. Please check if the user has linked his account.", channel);
                    return;
                }
                new Profile().sendProfileImage(storedPlayer, event);
                break;
            }*/
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
                Messages.sendImage(BotConstants.sealImageUrl, "Cute seal.jpg", channel);
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
        System.gc();

    }

    private void fetchRankedMapsIfNonExistant(TextChannel channel) {
        ranked = saviour.getCachedRankedMaps();
        if (ranked == null && System.getenv("disableRankedRequests") == null) {
            Messages.sendTempMessage("First command after startup, fetching ranked maps. Please wait... 🕒", 10, channel);
            RankedMapsFilterRequest request = new RankedMapsFilterRequest(0, 14, "date", "");
            ranked = saviour.fetchAllRankedMaps(request);
        }
    }

    @Override
    public void onGuildMemberRemove(GuildMemberRemoveEvent event) {
        DiscordLogger.sendLogInChannel("Member " + event.getUser().getName() + " left guild " + event.getGuild().getName(),  DiscordLogger.USERS);
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
            TextChannel botChannel = event.getJDA().getTextChannelById(Long.parseLong(System.getenv("channel_id")));
            Messages.sendPlainMessage(Format.bold(event.getMember().getAsMention() + ", welcome!") + " Be sure to register yourself here with " + Format.underline("\"ru register <ScoreSaber URL>\"") + " to obtain your roles.", botChannel);
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

    private Player getPlayerDependingOnCommand(List<String> msgParts, TextChannel channel, User author) {
        List<String> commandsWithPlayerInfo = BotConstants.getCommandsWithPlayerInfo();

        Player player = null;
        String command = msgParts.get(1);
        if (commandsWithPlayerInfo.contains(command)) {
            if (msgParts.size() == 3) {
                String urlOrPlayerName = msgParts.get(2);
                if (Format.isUrl(urlOrPlayerName)) {
                    try {
                        player = getScoreSaberPlayerFromUrl(msgParts.get(2));
                        player.setDiscordUserId(author.getIdLong());
                    } catch (FileNotFoundException e) {
                        Messages.sendMessage(e.getMessage(), channel);
                    }
                } else {
                    Messages.sendMessage("Could not find player \"" + urlOrPlayerName + "\".", channel);
                }
            } else {
                Messages.sendMessage("Player URL or name is missing. Please try again. Try \"ru help\" for more info.", channel);
            }
        }
        return player;
    }

    @Override
    public void onReady(@NotNull ReadyEvent event) {
        System.out.println("Ready!");
    }

    private Player getScoreSaberPlayerFromUrl(String profileUrl) throws FileNotFoundException {
        Matcher matcher = scoreSaberIDPattern.matcher(profileUrl);
        if (!matcher.find())
            throw new FileNotFoundException("Player could not be found, invalid link!");
        String playerId = matcher.group(1);
        Player player = ss.getPlayerById(playerId);
        if (player == null) {
            throw new FileNotFoundException("Player could not be found!");
        }
        return player;
    }

    private TextChannel getChannelByName(Guild guild, String name) {
        return guild.getTextChannels().stream().filter(c -> c.getName().equals(name)).findFirst().orElse(null);
    }
}
