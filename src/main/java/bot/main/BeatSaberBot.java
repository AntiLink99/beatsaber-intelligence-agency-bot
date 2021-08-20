package bot.main;

import static net.dv8tion.jda.api.interactions.commands.OptionType.INTEGER;
import static net.dv8tion.jda.api.interactions.commands.OptionType.STRING;
import static net.dv8tion.jda.api.interactions.commands.OptionType.USER;

import java.io.FileNotFoundException;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import javax.security.auth.login.LoginException;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;

import bot.api.ApiConstants;
import bot.api.BeatSaver;
import bot.api.BeatSaviour;
import bot.api.ScoreSaber;
import bot.chart.PlayerChart;
import bot.chart.RadarStatsChart;
import bot.commands.AccGridImage;
import bot.commands.ClaimPPRole;
import bot.commands.HandlePlayerRegisteration;
import bot.commands.Improvement;
import bot.commands.ListPlayers;
import bot.commands.Qualified;
import bot.commands.RandomMeme;
import bot.commands.RandomQuote;
import bot.commands.Ranked;
import bot.commands.RecentSong;
import bot.commands.RegisterAll;
import bot.commands.SendStats;
import bot.commands.SetSkill;
import bot.commands.SongsCommands;
import bot.commands.UpdatePlayer;
import bot.db.DatabaseManager;
import bot.dto.MessageEventDTO;
import bot.dto.RandomQuotesContainer;
import bot.dto.beatsaviour.RankedMaps;
import bot.dto.beatsaviour.RankedMapsFilterRequest;
import bot.dto.player.Player;
import bot.dto.player.PlayerSkills;
import bot.utils.DiscordLogger;
import bot.utils.DiscordUtils;
import bot.utils.Format;
import bot.utils.ListValueUtils;
import bot.utils.Messages;
import bot.utils.NumberValidation;
import bot.utils.RoleManager;
import javafx.application.Platform;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Category;
import net.dv8tion.jda.api.entities.ChannelType;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.events.guild.GuildJoinEvent;
import net.dv8tion.jda.api.events.guild.GuildLeaveEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberRemoveEvent;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.requests.restaction.CommandListUpdateAction;
import net.dv8tion.jda.api.utils.ChunkingFilter;
import net.dv8tion.jda.api.utils.MemberCachePolicy;
import net.dv8tion.jda.api.utils.cache.CacheFlag;

public class BeatSaberBot extends ListenerAdapter {

	ScoreSaber ss = new ScoreSaber();
	BeatSaver bs = new BeatSaver();
	BeatSaviour bsaviour = new BeatSaviour();
	DatabaseManager db = new DatabaseManager();
	RankedMaps ranked = new RankedMaps();
	RandomQuotesContainer randomQuotes = new RandomQuotesContainer();

	Pattern scoreSaberIDPattern = Pattern.compile(ApiConstants.USER_ID_REGEX);

	public static void main(String[] args) {
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
			initCommandDefinitions(jda);

			TextChannel botChannel = jda.getTextChannelById(BotConstants.outputChannelId);
			Runnable runnable = new Runnable() {
				public void run() {
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
										DiscordLogger.sendLogInChannel("Changed role: " + ssPlayer.getPlayerName() + " New Rank: " + ssPlayer.getRank() + " - Old Rank: " + storedPlayer.getRank() + "   " + "(Top " + String.valueOf(ListValueUtils.findMilestoneForRank(ssPlayer.getRank()) + ")"),
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
				}
			};

			ScheduledExecutorService service = Executors.newSingleThreadScheduledExecutor();
			service.scheduleAtFixedRate(runnable, 0, 20, TimeUnit.MINUTES);
		} catch (LoginException e) {
			e.printStackTrace();
		}
	}

	private static void initCommandDefinitions(JDA jda) {
		Map<String, String> botCommands = BotConstants.getCommands(false);
		CommandListUpdateAction commandsToDefine = jda.updateCommands();

		commandsToDefine.addCommands(new CommandData("register", botCommands.get("ru register <ScoreSaber URL>")).addOptions(new OptionData(STRING, "scoresaber_url", "Your ScoreSaber profile").setRequired(true)));
		commandsToDefine.addCommands(new CommandData("invite", botCommands.get("ru invite")));
		commandsToDefine.addCommands(new CommandData("unregister", botCommands.get("ru unregister")));

		commandsToDefine.addCommands(new CommandData("update", botCommands.get("ru update <ScoreSaber URL>")).addOptions(new OptionData(STRING, "scoresaber_url", "Your ScoreSaber profile").setRequired(true)));

		commandsToDefine.addCommands(new CommandData("recentsong", "Displays a player card for the recent score set on ScoreSaber.").addOptions(new OptionData(INTEGER, "score_count", "The x recent score on ScoreSaber").setRequired(false))
				.addOptions(new OptionData(USER, "member", "Another user you want to see the score from").setRequired(false)));

		commandsToDefine.addCommands(new CommandData("recentsongs", botCommands.get("ru recentsongs (optional: <PageID>) (optional: <@member>)")).addOptions(new OptionData(INTEGER, "page_id", "The page on ScoreSaber").setRequired(false))
				.addOptions(new OptionData(STRING, "member", "Another user you want to see the scores from").setRequired(false)));
		commandsToDefine.addCommands(new CommandData("topsongs", botCommands.get("ru topsongs (optional: <PageID>) (optional: <@member>)")).addOptions(new OptionData(INTEGER, "page_id", "The page on ScoreSaber").setRequired(false))
				.addOptions(new OptionData(USER, "member", "Another user you want to see the scores fro").setRequired(false)));

		commandsToDefine.addCommands(new CommandData("setgridimage", botCommands.get("ru setgridimage <Image URL>")).addOptions(new OptionData(STRING, "image_url", "The URL for your background image").setRequired(true)));

		commandsToDefine.addCommands(new CommandData("chart", botCommands.get("ru chart (optional: <@member>)")));
		commandsToDefine.addCommands(new CommandData("chartall", "Displays a chart with the rank changes of all players over the last couple of days.").addOptions(new OptionData(INTEGER, "highest", "The highest rank to show").setRequired(true))
				.addOptions(new OptionData(INTEGER, "lowest", "The lowest rank to show").setRequired(true)));
		commandsToDefine.addCommands(new CommandData("randommeme", botCommands.get("ru randommeme")));
		commandsToDefine.addCommands(new CommandData("improvement", botCommands.get("ru improvement")));
		commandsToDefine.addCommands(new CommandData("qualified", botCommands.get("ru qualified")));
		commandsToDefine.addCommands(new CommandData("ranked_by_stars", botCommands.get("ru ranked <minStars> <maxStars>")).addOptions(new OptionData(INTEGER, "min_stars", "The lowest star value to include").setRequired(true))
				.addOptions(new OptionData(INTEGER, "max_stars", "The highest star value to include").setRequired(true)));
		commandsToDefine.addCommands(new CommandData("ranked_by_latest", botCommands.get("ru ranked <amount>")).addOptions(new OptionData(STRING, "amount", "Amount for the latest x ranked maps.").setRequired(true)));
		commandsToDefine
				.addCommands(new CommandData("playlist", botCommands.get("ru playlist <filename> <map keys>")).addOptions(new OptionData(STRING, "filename", "The playlist name").setRequired(true)).addOptions(new OptionData(STRING, "map_keys", "Seperated map keys from BeatSaver").setRequired(true)));
		commandsToDefine.addCommands(new CommandData("say", botCommands.get("ru say <anything>")).addOptions(new OptionData(STRING, "anything", "The phrase to repeat").setRequired(true)));
		commandsToDefine.addCommands(new CommandData("youdidnotseethat", botCommands.get("ru youdidnotseethat")));
		commandsToDefine.addCommands(new CommandData("seal", botCommands.get("ru seal")));
		commandsToDefine.addCommands(new CommandData("help", botCommands.get("ru help")));

		commandsToDefine.queue();
	}

	@Override
	public void onSlashCommand(SlashCommandEvent event) {
		if (!event.isFromGuild() || event.getUser().isBot()) {
			return;
		}
		String message = event.getOptions().stream().map(option -> option.getAsString()).collect(Collectors.joining());
		System.out.println(message);
		if (message == null || message.isEmpty()) {
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
		handleRandomQuotes(event);
		String msg = StringUtils.join(msgParts, " ");
		Player player = getPlayerDependingOnCommand(msgParts, channel, author.getUser()); // Refactor!!!
		db.connectToDatabase();

		DiscordLogger.sendLogInChannel(Format.code("Command: " + msg + "\nRequester: " + author.getEffectiveName() + "\nGuild: " + guild.getName()), "info");
		String command = msgParts.get(1).toLowerCase();
		switch (command) {
		case "register":
			boolean success = HandlePlayerRegisteration.registerPlayer(player, db, channel);
			if (!success || guild.getIdLong() != BotConstants.foaaServerId) {
				break;
			}
		case "update":
			UpdatePlayer.updatePlayer(player, db, ss, channel);
		case "claimpp":
			if (guild.getIdLong() == BotConstants.foaaServerId) {
				ClaimPPRole.validateAndAssignRole(event.getAuthor(), db, ss, channel, true);
			}
			break;
		case "claimppall":
			if (DiscordUtils.isAdmin(event.getAuthor().getUser())) {
				ClaimPPRole.validateAndAssignRoleForAll(event, db, ss);
			}
			break;
		case "updateall":
			if (DiscordUtils.isAdmin(authorUser)) {
				UpdatePlayer.updateAllPlayers(db, ss, channel);
			}
			break;
		case "unregister":
			HandlePlayerRegisteration.unregisterPlayer(db, event);
			break;
		case "list":
			if (DiscordUtils.isAdmin(authorUser)) {
				ListPlayers.sendRegisteredPlayers(db, channel);
			}
			break;
		case "registerall":
			RegisterAll.registerAllMembers(db, event);
			break;
		case "improvement":
			Improvement.sendImprovementMessage(db, channel);
			break;
		case "randomquote":
			if (isFOAACategory(channel.getParent())) {
				initRandomQuotesIfNonExistant(event);
				RandomQuote.sendRandomQuote(randomQuotes, event);
			} else {
				Messages.sendMessage("Sorry, this command is only allowed in FOAA channels.", channel);
			}
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
			final long memID = memberId;
			List<Player> storedPlayers = db.getAllStoredPlayers().stream().filter(p -> p.getDiscordUserId() == memID).collect(Collectors.toList());
			if (storedPlayers == null || storedPlayers.size() == 0) {
				Messages.sendMessage("The player is not registered. Try \"ru help\".", channel);
				return;
			}
			PlayerChart.sendChartImage(storedPlayers.get(0), event);
			break;
		}
		case "chartall":
			List<Player> storedPlayers = db.getAllStoredPlayers();
			storedPlayers.removeIf(p -> channel.getGuild().getMemberById(p.getDiscordUserId()) == null);
			PlayerChart.sendChartImage(storedPlayers, event, (msgParts.size() >= 3 ? msgParts.get(2) : null));
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
			RadarStatsChart.sendChartImage(skills, memberId, event);
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
			Qualified.sendQualifiedPlaylist(event, ss, bs);
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
				Ranked.sendRecentRankedPlaylist(ranked, Integer.parseInt(inputValues[0]), bs, event);
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
				Ranked.sendStarRangeRankedPlaylist(ranked, min, max, bs, event);
			} else {
				Messages.sendMessage("Invalid number of parameters.", channel);
			}
			break;
		case "randommeme":
			RandomMeme.sendRandomMeme(channel);
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
				RecentSong.sendRecentSong(storedPlayer, ranked, index, db, ss, bs, event);
			}
			return;
		}
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
			SongsCommands.sendRecentSongs(storedPlayer, ranked, index, db, ss, bs, event);
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
			SongsCommands.sendTopSongs(storedPlayer, ranked, index, db, ss, bs, event);
			return;
		}
		case "setgridimage": {
			if (msgParts.size() < 3) {
				AccGridImage.resetImage(db, event);
				return;
			}
			String urlString = msgParts.get(2);
			List<String> allowedFormats = Arrays.asList(".jpeg", ".jpg", ".png");
			if (!Format.isUrl(urlString) || !allowedFormats.stream().anyMatch(format -> urlString.toLowerCase().contains(format))) {
				Messages.sendMessage("The given parameter is not an image URL. (Has to contain .png, .jpg or .jpeg)", channel);
				return;
			}
			AccGridImage.sendAccGridImage(urlString, db, event);
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
				SendStats.sendStats(event);
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
		case "youdidnotseethat":
			event.getMessage().delete().queue();
			String filler = Format.bold(Format.bold(" ")) + "\n";
			Messages.sendPlainMessage(StringUtils.repeat(filler, 40), channel);
			break;
		case "leave":
			if (DiscordUtils.isAdmin(authorUser)) {
				if (msgParts.size() > 2) {
					long id = Long.valueOf(msgParts.get(2));
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
			Messages.sendMessage("Sorry, i don't speak wrong. 🤡  Try \"ru help\".\nIf you want to suggest something to the dev do it "+Format.link("here", BotConstants.featureRequestUrl)+".", channel);
		}
		System.gc();

	}

	private void fetchRankedMapsIfNonExistant(TextChannel channel) {
		ranked = bsaviour.getCachedRankedMaps();
		if (ranked == null && System.getenv("disableRankedRequests") == null) {
			Messages.sendTempMessage("First command after startup, fetching ranked maps. Please wait... 🕒", 10, channel);
			RankedMapsFilterRequest request = new RankedMapsFilterRequest(0, 14, "date", "");
			ranked = bsaviour.fetchAllRankedMaps(request);
		}
	}

	private boolean isFOAACategory(Category parent) {
		return parent != null && parent.getIdLong() == BotConstants.foaaCategoryId;
	}

	@Override
	public void onGuildMemberRemove(GuildMemberRemoveEvent event) {
		DiscordLogger.sendLogInChannel("Member " + event.getUser().getName() + " left guild " + event.getGuild().getName(), "INFO");
		long userId = event.getUser().getIdLong();

		List<Guild> guilds = event.getJDA().getGuilds();
		List<Member> members = guilds.stream().flatMap(guild -> guild.getMembers().stream()).collect(Collectors.toList());
		boolean memberNotOnOtherServer = members.stream().noneMatch(m -> m.getUser().getIdLong() == userId);
		if (memberNotOnOtherServer) {
			DiscordLogger.sendLogInChannel("Deleting user if exists: " + event.getUser().getName(), DiscordLogger.INFO);
			db.deletePlayerByDiscordUserId(userId);
		}
	}

	@Override
	public void onGuildMemberJoin(GuildMemberJoinEvent event) {
		if (event.getGuild().getIdLong() == BotConstants.foaaServerId) {
			Messages.sendPrivateMessage(BotConstants.newMemberMessage, event.getMember());
			TextChannel botChannel = event.getJDA().getTextChannelById(Long.valueOf(System.getenv("channel_id")));
			Messages.sendPlainMessage(Format.bold(event.getMember().getAsMention() + ", welcome!") + " Be sure to register yourself here with " + Format.underline("\"ru register <ScoreSaber URL>\"") + " to obtain your roles.", botChannel);
		}
	}

	@Override
	public void onGuildJoin(GuildJoinEvent event) {
		DiscordLogger.sendLogInChannel(Format.code("Joined guild \"" + event.getGuild().getName() + "\""), "guilds");
	}

	@Override
	public void onGuildLeave(GuildLeaveEvent event) {
		DiscordLogger.sendLogInChannel(Format.code("Left guild \"" + event.getGuild().getName() + "\""), "guilds");
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
	public void onReady(ReadyEvent event) {
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

	private void initRandomQuotesIfNonExistant(MessageEventDTO event) {
		if (randomQuotes.getRandomQuoteImages() == null || randomQuotes.getRandomQuoteImages().size() == 0) {
			Messages.sendTempMessage("Loading quotes from channel...", 15, event.getChannel());
			TextChannel quotesChannel = getChannelByName(event.getGuild(), "quotes");
			randomQuotes.initialize(quotesChannel);
			Messages.sendTempMessage(Format.bold(randomQuotes.getRandomQuoteImages().size() + " quotes loaded!"), 15, event.getChannel());
		}
	}

	private void handleRandomQuotes(MessageEventDTO dto) {
		TextChannel channel = dto.getChannel();
		if (dto.getGuild().getIdLong() == BotConstants.foaaServerId && channel.getName() != null && channel.getName().equals("quotes")) {
			if (randomQuotes.getRandomQuoteImages() == null) {
				TextChannel quotesChannel = getChannelByName(channel.getGuild(), "quotes");
				randomQuotes.initialize(quotesChannel);
			}
			randomQuotes.addRandomQuoteImages(dto.getMessage());
			return;
		}
	}

	private TextChannel getChannelByName(Guild guild, String name) {
		return guild.getTextChannels().stream().filter(c -> c.getName().equals(name)).findFirst().orElse(null);
	}
}
