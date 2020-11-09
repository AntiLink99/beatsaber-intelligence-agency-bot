package bot.main;

import java.io.FileNotFoundException;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import javax.security.auth.login.LoginException;

import org.apache.commons.lang3.StringUtils;

import bot.api.ApiConstants;
import bot.api.BeatSaver;
import bot.api.ScoreSaber;
import bot.chart.PlayerChart;
import bot.chart.RadarStatsChart;
import bot.commands.ClaimPPRole;
import bot.commands.HandlePlayerRegisteration;
import bot.commands.Improvement;
import bot.commands.ListPlayers;
import bot.commands.RandomMeme;
import bot.commands.RandomQuote;
import bot.commands.RecentSongs;
import bot.commands.RegisterAll;
import bot.commands.SetSkill;
import bot.commands.UpdatePlayer;
import bot.db.DatabaseManager;
import bot.dto.Player;
import bot.dto.PlayerSkills;
import bot.utils.DiscordUtils;
import bot.utils.Format;
import bot.utils.JsonUtils;
import bot.utils.ListValueUtils;
import bot.utils.Messages;
import bot.utils.RoleManager;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberRemoveEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.ChunkingFilter;
import net.dv8tion.jda.api.utils.MemberCachePolicy;

public class BeatSaberBot extends ListenerAdapter {

	ScoreSaber ss = new ScoreSaber();
	BeatSaver bs = new BeatSaver();
	DatabaseManager db = new DatabaseManager();

	public static void main(String[] args) {
		DatabaseManager db = new DatabaseManager();
		ScoreSaber ss = new ScoreSaber();
		
		System.out.println("Downloading new map data...");
		JsonUtils.downloadJsonMapFile();
		System.out.println("Reading map data...");
		JsonUtils.loadJsonMapFile();
		System.out.println("Done loading!");
		try {
			JDABuilder builder = JDABuilder.createDefault(System.getenv("bot_token")).setMemberCachePolicy(MemberCachePolicy.ALL).enableIntents(GatewayIntent.GUILD_MEMBERS).setChunkingFilter(ChunkingFilter.ALL).addEventListeners(new BeatSaberBot())
					.setActivity(Activity.playing(BotConstants.PLAYING));

			JDA jda = builder.build();
			try {
				jda.awaitReady();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

			TextChannel botChannel = jda.getTextChannelById(Long.valueOf(System.getenv("channel_id")));
			Runnable runnable = new Runnable() {
				public void run() {
					db.connectToDatabase();
					System.out.println("----- Starting User Refresh... [" + LocalTime.now().getHour() + ":" + LocalTime.now().getMinute() + "]");
					try {
						List<Player> players = db.getAllStoredPlayers();
						for (Player storedPlayer : players) {
							Player ssPlayer = ss.getPlayerById(storedPlayer.getPlayerId());
							if (ssPlayer == null) {
								continue;
							}
							ssPlayer.setDiscordUserId(storedPlayer.getDiscordUserId());
							if (ssPlayer.getRank() != storedPlayer.getRank() && ssPlayer.getRank() != 0) {
								db.deletePlayer(storedPlayer);
								db.savePlayer(ssPlayer);
								Member member = botChannel.getGuild().getMembers().stream().filter(m -> m.getUser().getIdLong() == ssPlayer.getDiscordUserId()).findFirst().orElse(null);
								if (RoleManager.isNewMilestone(ssPlayer.getRank(), member)) {
									System.out.println("Changed role: " + ssPlayer.getPlayerName() + " New Rank: " + ssPlayer.getRank() + " - Old Rank: " + storedPlayer.getRank() + "   " + "(Top " + String.valueOf(ListValueUtils.findMilestoneForRank(ssPlayer.getRank()) + ")"));
									RoleManager.removeMemberRolesByName(member, BotConstants.topRolePrefix);
									RoleManager.assignMilestoneRole(ssPlayer.getRank(), member);
									Messages.sendMilestoneMessage(ssPlayer, botChannel);
								}
							}

							try {
								TimeUnit.MILLISECONDS.sleep(100);
							} catch (InterruptedException e) {
								e.printStackTrace();
							}
						}
					} catch (Exception e) {
						e.printStackTrace();
						System.out.println("There was an exception in scheduled task: " + e.getMessage());
					}
					System.gc();
					System.out.println("----- Finished!");
				}
			};

			ScheduledExecutorService service = Executors.newSingleThreadScheduledExecutor();
			service.scheduleAtFixedRate(runnable, 0, 15, TimeUnit.MINUTES);
		} catch (LoginException e) {
			e.printStackTrace();
		}
	}

	public void onMessageReceived(MessageReceivedEvent event) {
		String msg = event.getMessage().getContentRaw();
		if (!msg.toLowerCase().startsWith("ru ") || event.getAuthor().isBot() && !event.isFromGuild()) {
			return;
		}
		TextChannel channel = event.getTextChannel();
		channel.sendTyping().queue();

		List<String> msgParts = Arrays.asList(msg.split(" ", 3));

		// Recruiting Server
		if (event.getGuild().getIdLong() == BotConstants.recruitingServerId) {
			if (!Arrays.asList(BotConstants.allowedRecruitingCommands).contains(msgParts.get(1).toLowerCase())) {
				Messages.sendMessage("Sorry, i can only do that in the main server.", channel);
				return;
			} else if (event.getMember().getRoles().stream().anyMatch(role -> role.getIdLong() == BotConstants.recruitRoleId)) {
				Messages.sendMessage("Sorry, only FOAA members can do that.", channel);
				return;
			}
		}
		//
		Player player = getPlayerDependingOnCommand(msgParts, event);
		db.connectToDatabase();
		switch (msgParts.get(1).toLowerCase()) {
		case "register":
			boolean success = HandlePlayerRegisteration.registerPlayer(player, db, channel);
			if (!success) {
				break;
			}
		case "update":
			UpdatePlayer.updatePlayer(player, db, ss, channel);
		case "claimpp":
			ClaimPPRole.validateAndAssignRole(event.getMember(), db, ss, channel, true);
			break;
		case "claimppall":
			if (DiscordUtils.isAdmin(event.getAuthor())) {
				ClaimPPRole.validateAndAssignRoleForAll(event, db, ss);
			}
			break;
		case "updateall":
			if (DiscordUtils.isAdmin(event.getAuthor())) {
				UpdatePlayer.updateAllPlayers(db, ss, channel);
			}
			break;
		case "unregister":
			HandlePlayerRegisteration.unregisterPlayer(player, db, event);
			break;
		case "list":
			ListPlayers.sendRegisteredPlayers(db, channel);
			break;
		case "registerall":
			RegisterAll.registerAllMembers(db, event);
			break;
		case "improvement":
			Improvement.sendImprovementMessage(db, channel);
			break;
		case "randomquote":
			RandomQuote.sendRandomQuote(event);
			break;
		case "chart": {
			List<Player> storedPlayers = db.getAllStoredPlayers().stream().filter(p -> p.getDiscordUserId() == event.getAuthor().getIdLong()).collect(Collectors.toList());
			if (storedPlayers == null || storedPlayers.size() == 0) {
				Messages.sendMessage("You are not registered. Try \"ru help\".", channel);
				return;
			}
			PlayerChart.sendChartImage(storedPlayers.get(0), event, (msgParts.size() >= 3 ? msgParts.get(2) : null));
			break;
		}
		case "chartall":
			List<Player> storedPlayers = db.getAllStoredPlayers();
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

			bs.sendPlaylistInChannel(values, playlistTitle, channel);
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

			bs.sendRecruitingPlaylistInChannel(values, playlistTitle, event);
			break;
		case "randommeme":
			RandomMeme.sendRandomMeme(channel);
			break;
		case "recentsongs": {
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
				Messages.sendMessage("Player could not be found. Please check if the user has linked his account.", channel);
				return;
			}
			RecentSongs.sendRecentSongsImage(storedPlayer, ss, bs, event);
			break;
		}
		case "topsongs": {
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
				Messages.sendMessage("Player could not be found. Please check if the user has linked his account.", channel);
				return;
			}
			RecentSongs.sendTopSongsImage(storedPlayer, ss, bs, event);
			break;
		}
		case "seal":
			Messages.sendImage(BotConstants.sealImageUrl, "Cute seal.jpg", channel);
			break;
		case "say":
			String phrase = msgParts.size() >= 3 ? msgParts.get(2) : "🤡";
			System.out.println(event.getAuthor() + " said: " + phrase);
			try {
				event.getMessage().delete().queue();
			} catch (Exception e) {
				System.out.println("Could not delete message because of lacking permissions.");
			}
			Messages.sendPlainMessage(phrase, channel);
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
		case "youdidnotseethat":
			event.getMessage().delete().queue();
			String filler = Format.bold(Format.bold(" ")) + "\n";
			Messages.sendPlainMessage(StringUtils.repeat(filler, 40), channel);
			break;
		case "help":
			Messages.sendMultiPageMessage(BotConstants.getCommands(), "🔨 Bot commands 🔨", channel);
			break;
		default:
			Messages.sendMessage("Sorry, i don't speak wrong. 🤡  Try \"ru help\".", channel);
		}
		System.gc();
	}

	@Override
	public void onGuildMemberRemove(GuildMemberRemoveEvent event) {
		System.out.println("DELETING USER: "+event.getUser().getName());
		long userId = event.getUser().getIdLong();
		db.deletePlayerByDiscordUserId(userId);
	}

	@Override
	public void onGuildMemberJoin(GuildMemberJoinEvent event) {
		if (event.getGuild().getIdLong() == BotConstants.foaaServerId) {
			Messages.sendPrivateMessage(BotConstants.newMemberMessage, event.getMember());
			TextChannel botChannel = event.getJDA().getTextChannelById(Long.valueOf(System.getenv("channel_id")));
			Messages.sendPlainMessage(Format.bold(event.getMember().getAsMention() + ", welcome!") + " Be sure to register yourself here with " + Format.underline("\"ru register <ScoreSaber URL>\"") + " to obtain your roles.", botChannel);
		}
	}

	private Player getPlayerDependingOnCommand(List<String> msgParts, MessageReceivedEvent event) {
		TextChannel channel = event.getTextChannel();
		List<String> commandsWithPlayerInfo = BotConstants.getCommandsWithPlayerInfo();

		Player player = null;
		String command = msgParts.get(1);
		if (commandsWithPlayerInfo.contains(command)) {
			if (msgParts.size() == 3) {
				String urlOrPlayerName = msgParts.get(2);
				if (Format.isUrl(urlOrPlayerName)) {
					try {
						player = getScoreSaberPlayerFromUrl(msgParts.get(2));
						player.setDiscordUserId(event.getAuthor().getIdLong());
					} catch (FileNotFoundException e) {
						Messages.sendMessage(e.getMessage(), channel);
					}
				} else if (commandDoesntRequireUrl(command)) {
					player = new Player();
					player.setPlayerName(urlOrPlayerName);
				} else {
					Messages.sendMessage("Could not find player \"" + urlOrPlayerName + "\".", channel);
				}
			} else {
				Messages.sendMessage("Player URL or name is missing. Please try again. Try \"ru help\" for more info.", channel);
			}
		}
		return player;
	}

	private boolean commandDoesntRequireUrl(String command) {
		return !command.equals("register");
	}

	@Override
	public void onReady(ReadyEvent event) {
		System.out.println("Ready!");
	}

	private Player getScoreSaberPlayerFromUrl(String profileUrl) throws FileNotFoundException {
		String playerId = profileUrl.replace(ApiConstants.USER_PRE_URL, "");
		Player player = ss.getPlayerById(playerId);
		if (player == null) {
			throw new FileNotFoundException("Player could not be found!");
		}
		return player;
	}
}
