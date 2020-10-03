package bot.main;

import java.io.FileNotFoundException;
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
import bot.commands.ClaimPPRole;
import bot.commands.HandlePlayerRegisteration;
import bot.commands.Improvement;
import bot.commands.ListPlayers;
import bot.commands.RandomQuote;
import bot.commands.RegisterAll;
import bot.commands.UpdatePlayer;
import bot.db.DBConstants;
import bot.db.DatabaseManager;
import bot.dto.Player;
import bot.utils.DiscordUtils;
import bot.utils.Format;
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
import net.dv8tion.jda.api.events.guild.member.GuildMemberLeaveEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class BeatSaberBot extends ListenerAdapter {

	ScoreSaber ss = new ScoreSaber();
	BeatSaver bs = new BeatSaver();
	DatabaseManager db = new DatabaseManager();

	public static void main(String[] args) {
		DatabaseManager db = new DatabaseManager();
		db.connectToDatabase(DBConstants.DB_HOST);
		ScoreSaber ss = new ScoreSaber();
		try {
			JDA jda = new JDABuilder(System.getenv("bot_token")).addEventListeners(new BeatSaberBot()).setActivity(Activity.playing(BotConstants.PLAYING)).build();
			try {
				jda.awaitReady();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

			TextChannel testChannel = jda.getTextChannelById(Long.valueOf(System.getenv("channel_id")));
			Runnable runnable = new Runnable() {
				public void run() {
					try {
						List<Player> players = db.getAllStoredPlayers();
						for (Player storedPlayer : players) {
							Player ssPlayer = ss.getPlayerById(storedPlayer.getPlayerId());
							if (ssPlayer == null) {
								continue;
							}
							ssPlayer.setDiscordUserId(storedPlayer.getDiscordUserId());
							if (ssPlayer.getRank() != storedPlayer.getRank()) {
								db.deletePlayer(storedPlayer);
								db.savePlayer(ssPlayer);
								Member member = testChannel.getMembers().stream().filter(m -> m.getIdLong() == ssPlayer.getDiscordUserId()).findFirst().orElse(null);
								if (RoleManager.isNewMilestone(ssPlayer.getRank(), member)) {
									System.out.println("Changed role: " + ssPlayer.getPlayerName() + " New Rank: " + ssPlayer.getRank() + " - Old Rank: " + storedPlayer.getRank() + "   " + "(Top " + String.valueOf(ListValueUtils.findMilestoneForRank(ssPlayer.getRank()) + ")"));
									RoleManager.removeMemberRolesByName(member, BotConstants.rolePrefix);
									RoleManager.assignMilestoneRole(ssPlayer.getRank(), member);
									Messages.sendMilestoneMessage(ssPlayer, testChannel);
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
				}
			};

			ScheduledExecutorService service = Executors.newSingleThreadScheduledExecutor();
			service.scheduleAtFixedRate(runnable, 0, 10, TimeUnit.MINUTES);
		} catch (LoginException e) {
			e.printStackTrace();
		}
	}

	public void onMessageReceived(MessageReceivedEvent event) {
		TextChannel channel = event.getTextChannel();
		String msg = event.getMessage().getContentRaw();
		if (!msg.startsWith("ru ") || event.getAuthor().isBot()) {
			return;
		}
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
		db.connectToDatabase(DBConstants.DB_HOST);
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
		case "chart":
			Player storedPlayer = db.getAllStoredPlayers().stream().filter(p -> p.getDiscordUserId() == event.getAuthor().getIdLong()).findFirst().orElseGet(null);
			if (storedPlayer == null) {
				Messages.sendMessage("You are not registered. Try \"ru help\".", channel);
			}
			PlayerChart.sendChartImage(storedPlayer, event, (msgParts.size() >= 3 ? msgParts.get(2) : null));
			break;
		case "chartall":
			List<Player> storedPlayers = db.getAllStoredPlayers();
			PlayerChart.sendChartImage(storedPlayers, event, (msgParts.size() >= 3 ? msgParts.get(2) : null));
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
			Messages.sendMessageStringMap(BotConstants.getCommands(), channel);
			break;
		default:
			Messages.sendMessage("Sorry, i don't speak wrong. 🤡  Try \"ru help\".", channel);
		}
		System.gc();
	}

	@Override
	public void onGuildMemberLeave(GuildMemberLeaveEvent event) {
		long userId = event.getUser().getIdLong();
		db.deletePlayerByDiscordUserId(userId);
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
