package bot.main;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import javax.security.auth.login.LoginException;

import bot.api.ApiConstants;
import bot.api.ScoreSaber;
import bot.db.DBConstants;
import bot.db.DatabaseManager;
import bot.foaa.dto.Player;
import bot.foaa.dto.PlayerImprovement;
import bot.listeners.RegisterAllListener;
import bot.utils.DiscordUtils;
import bot.utils.Format;
import bot.utils.MapUtils;
import bot.utils.Messages;
import bot.utils.RoleManager;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class BeatSaberBot extends ListenerAdapter {

	ScoreSaber ss = new ScoreSaber();
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
					List<Player> players = db.getAllStoredPlayers();
					for (Player storedPlayer : players) {
						Player ssPlayer = ss.getPlayerById(storedPlayer.getPlayerId());
						ssPlayer.setDiscordUserId(storedPlayer.getDiscordUserId());
						if (ssPlayer.getRank() != storedPlayer.getRank()) {
							db.deletePlayer(storedPlayer);
							db.savePlayer(ssPlayer);
							Member member = testChannel.getMembers().stream().filter(m -> m.getIdLong() == ssPlayer.getDiscordUserId()).findFirst().orElse(null);
							if (RoleManager.isNewMilestone(ssPlayer.getRank(), member)) {
								System.out.println("Changed role: " + ssPlayer.getPlayerName() + " New Rank: " + ssPlayer.getRank() + " - Old Rank: " + storedPlayer.getRank() + "   " + "(Top " + String.valueOf(RoleManager.findMilestoneForRank(ssPlayer.getRank()) + ")"));
								RoleManager.removeAllMilestoneRoles(member);
								RoleManager.assignMilestoneRole(ssPlayer.getRank(), member);
								Messages.sendMilestoneMessage(ssPlayer, testChannel);
							}
						}
					}
					System.gc();
				}
			};

			ScheduledExecutorService service = Executors.newSingleThreadScheduledExecutor();
			service.scheduleAtFixedRate(runnable, 0, 5, TimeUnit.MINUTES);
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
		Player player = null;

		List<String> commandsWithPlayerInfo = new ArrayList<String>();
		commandsWithPlayerInfo.add("register");
		commandsWithPlayerInfo.add("unregister");
		commandsWithPlayerInfo.add("update");

		String command = msgParts.get(1);
		if (commandsWithPlayerInfo.contains(command)) {
			if (msgParts.size() == 3) {
				String urlOrPlayerName = msgParts.get(2);
				if (urlOrPlayerName.contains("https://")) {
					try {
						if (msgParts.size() < 3) {
							throw new FileNotFoundException("ScoreSaber URL is missing! Syntax: \"ru register <URL>\"");
						}
						player = getScoreSaberPlayerFromUrl(msgParts.get(2));
						player.setDiscordUserId(event.getAuthor().getIdLong());
					} catch (FileNotFoundException e) {
						Messages.sendMessage(e.getMessage(), channel);
						return;
					}
				} else if (commandsWithPlayerInfo.contains(command) && !command.equals("register")) {
					player = new Player();
					player.setPlayerName(urlOrPlayerName);
				} else {
					Messages.sendMessage("Could not find player \"" + urlOrPlayerName + "\".", channel);
					return;
				}
			} else {
				Messages.sendMessage("Player URL or name is missing. Please try again. Try \"ru help\" for more info.", channel);
				return;
			}
		}
		db.connectToDatabase(DBConstants.DB_HOST);
		switch (msgParts.get(1)) {
		case "register": {
			boolean successSave = db.savePlayer(player);
			if (!successSave) {
				Messages.sendMessage("The player \"" + player.getPlayerName() + "\" is already registered! Use \"ru unregister <URL / Username>\" to remove the player from the database.", channel);
				break;
			}
			Messages.sendMessage("Player \"" + player.getPlayerName() + "\" was registered successfully and will be tracked from now on.", channel);
		}
		case "update": {
			Player storedPlayer = db.getPlayerByName(player.getPlayerName());
			Player ssPlayer = player;

			if (storedPlayer == null) {
				Messages.sendMessage("Could not find player \"" + player.getPlayerName() + "\".", channel);
				break;
			}

			if (ssPlayer.getPlayerId() == null) {
				ssPlayer = ss.getPlayerById(storedPlayer.getPlayerId());
			}

			final long playerDiscordId = storedPlayer.getDiscordUserId();
			ssPlayer.setDiscordUserId(playerDiscordId);
			Member member = channel.getMembers().stream().filter(m -> m.getIdLong() == playerDiscordId).findFirst().orElse(null);

			if (RoleManager.isNewMilestone(ssPlayer.getRank(), member)) {
				db.deletePlayer(storedPlayer);
				db.savePlayer(ssPlayer);
				RoleManager.removeAllMilestoneRoles(member);
				RoleManager.assignMilestoneRole(ssPlayer.getRank(), member);
				Messages.sendMilestoneMessage(ssPlayer, channel);
			} else {
				Messages.sendMessage("Player \"" + ssPlayer.getPlayerName() + "\" was not updated since the milestone is still the same. (Top " + RoleManager.findMilestoneForRank(ssPlayer.getRank()) + ")", channel);
			}
			break;
		}
		case "updateall": {
			try {
				List<Player> storedPlayers = db.getAllStoredPlayers();
				for (Player storedPlayer : storedPlayers) {
					Player ssPlayer = ss.getPlayerById(storedPlayer.getPlayerId());
					ssPlayer.setDiscordUserId(storedPlayer.getDiscordUserId());
					try {
						TimeUnit.MILLISECONDS.sleep(125);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					boolean successDelete = db.deletePlayer(storedPlayer);
					if (!successDelete) {
						Messages.sendMessage("The player \"" + storedPlayer.getPlayerName() + "\" could not be removed from the database.", channel);
						break;
					}
	
					boolean successSave = db.savePlayer(ssPlayer);
					if (!successSave) {
						Messages.sendMessage("The player \"" + storedPlayer.getPlayerName() + "\" is already registered! Use \"ru unregister <URL / Username>\" to remove the player from the database.", channel);
						break;
					}
				}
				Messages.sendMessage("All players were updated successfully.", channel);
			} catch (Exception e) {
				Messages.sendMessage("Could not fetch players. I probably sent too many requests. Whoops.", channel);
				e.printStackTrace();
			}
			break;
		}
		case "unregister":
			boolean successDelete = db.deletePlayer(player);
			if (!successDelete) {
				Messages.sendMessage("The player \"" + player.getPlayerName() + "\" is already not registered.", channel);
				break;
			}
			Messages.sendMessage("Player \"" + player.getPlayerName() + "\" was unregistered successfully.", channel);
			break;

		case "list": {
			List<Player> storedPlayers = db.getAllStoredPlayers();
			List<Member> allMembers = channel.getGuild().getMembers();

			Collections.sort(storedPlayers, (p1, p2) -> String.CASE_INSENSITIVE_ORDER.compare(DiscordUtils.getMemberNameFromId(allMembers, p1.getDiscordUserId()), DiscordUtils.getMemberNameFromId(allMembers, p2.getDiscordUserId())));
			TreeMap<String, String> nameWithUrl = new TreeMap<String, String>();

			if (storedPlayers.isEmpty()) {
				Messages.sendMessage("No players are being tracked! Use \"ru register <URL>\".", channel);
				break;
			}

			for (int index = 0; index < storedPlayers.size(); index++) {
				if (index % 25 == 0 && index != 0) {
					Messages.sendMessageStringMap(nameWithUrl, channel);
					nameWithUrl.clear();
				}
				Player listPlayer = storedPlayers.get(index);
				// DEV: Kein Hinzufügen
				nameWithUrl.put(Format.bold(DiscordUtils.getMemberNameFromId(allMembers, listPlayer.getDiscordUserId())), ApiConstants.USER_PRE_URL + listPlayer.getPlayerId());
			}
			Messages.sendMessageStringMap(nameWithUrl, channel);
			break;
		}
		case "registerall":
			List<Member> guildMembers = channel.getGuild().getMembers();
			List<Long> storedUserIds = db.getAllStoredPlayers().stream().map(p -> p.getDiscordUserId()).collect(Collectors.toList());

			List<Member> missingMembers = guildMembers.stream().filter(m -> !storedUserIds.contains(m.getIdLong()) && !m.getUser().isBot()).collect(Collectors.toList());
			List<String> missingMemberNames = missingMembers.stream().map(m -> m.getEffectiveName()).sorted(String.CASE_INSENSITIVE_ORDER).collect(Collectors.toList());

			String message = Format.bold("These members are not registered yet:\n\n");
			message = message.concat(String.join("\n", missingMemberNames));
			Messages.sendMessage(message, channel);

			event.getJDA().addEventListener(new RegisterAllListener(channel, event.getAuthor(), missingMembers, db));
			break;
		case "improvement":
			List<Player> storedPlayers = db.getAllStoredPlayers();
			List<PlayerImprovement> improvements = new ArrayList<PlayerImprovement>();
			for (Player storedPlayer : storedPlayers) {
				List<Integer> historyValues = storedPlayer.getHistoryValues();

				int newRank = historyValues.get(historyValues.size() - 1);
				int oldRank = historyValues.get(historyValues.size() - 1 - 7);
				improvements.add(new PlayerImprovement(storedPlayer.getPlayerName(), oldRank, newRank));
			}
			Map<String, String> improvementsString = MapUtils.convertPlayerImprovements(improvements);
			Messages.sendMultiPageMessage(improvementsString, "Improvement over the last seven days", channel);
			break;
		case "randomquote":
			RandomQuote.sendRandomQuote(event, channel);
			break;
		case "seal":
			Messages.sendImage(BotConstants.sealImageUrl, "Cute seal.jpg", channel);
			break;
		case "say":
			System.out.println(event.getAuthor() + " said: " + msgParts.get(2));
			try {
				event.getMessage().delete().queue();
			} catch (Exception e) {
				System.out.println("Could not delete message because of lacking permissions.");
			}
			Messages.sendPlainMessage(msgParts.get(2), channel);
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
