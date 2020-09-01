package bot.main;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
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
import bot.listeners.RegisterAllListener;
import bot.utils.Format;
import bot.utils.Messages;
import bot.utils.RandomUtils;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.Message.Attachment;
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
			JDA jda = new JDABuilder(System.getenv("bot_token")).addEventListeners(new BeatSaberBot()).setActivity(Activity.playing("Beat Saber")).build();
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
								Messages.sendMessage("💥 " + ssPlayer.getPlayerName() + "'s milestone role was updated! " + Format.bold("(Top " + String.valueOf(RoleManager.findMilestoneForRank(ssPlayer.getRank())) + ") 💥"), testChannel);
							}
						}
					}
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
		channel.sendTyping();

		List<String> msgParts = Arrays.asList(msg.split(" ", 3));
		Player player = null;

		List<String> commandsWithPlayerInfo = new ArrayList<String>();
		commandsWithPlayerInfo.add("register");
		commandsWithPlayerInfo.add("unregister");
		commandsWithPlayerInfo.add("update");

		if (commandsWithPlayerInfo.contains(msgParts.get(1))) {
			if (msgParts.size() == 3) {
				String urlOrPlayerName = msgParts.get(2);
				if (urlOrPlayerName.contains("https://")) {
					try {
						player = getScoreSaberPlayerFromMessage(msgParts);
						player.setDiscordUserId(event.getAuthor().getIdLong());
					} catch (FileNotFoundException e) {
						Messages.sendMessage(e.getMessage(), channel);
						return;
					}
				} else if (commandsWithPlayerInfo.contains(msgParts.get(1)) && !msgParts.get(1).equals("register")) {
					player = new Player();
					player.setPlayerName(urlOrPlayerName);
				} else {
					Messages.sendMessage("Could not find player \"" + msgParts.get(2) + "\".", channel);
					return;
				}
			} else {
				Messages.sendMessage("Player URL or name is missing. Please try again. Try \"ru help\" for more info.", channel);
				return;
			}
		}
		db.connectToDatabase(DBConstants.DB_HOST);
		switch (msgParts.get(1)) {
		case "register":
			boolean successSave = db.savePlayer(player);
			if (!successSave) {
				Messages.sendMessage("The player \"" + player.getPlayerName() + "\" is already registered! Use \"ru unregister <URL / Username>\" to remove the player from the database.", channel);
				break;
			}
			Messages.sendMessage("Player \"" + player.getPlayerName() + "\" was registered successfully and will be tracked from now on.", channel);

		case "update":
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
				Messages.sendMessage("💥 " + ssPlayer.getPlayerName() + "'s milestone role was updated! " + Format.bold("(Top " + String.valueOf(RoleManager.findMilestoneForRank(ssPlayer.getRank())) + ") 💥"), channel);
			} else {
				Messages.sendMessage("Player \"" + ssPlayer.getPlayerName() + "\" was not updated since the milestone is still the same. (Top " + RoleManager.findMilestoneForRank(ssPlayer.getRank()) + ")", channel);
			}
			break;

		case "unregister":
			boolean successDelete = db.deletePlayer(player);
			if (!successDelete) {
				Messages.sendMessage("The player \"" + player.getPlayerName() + "\" is already not registered.", channel);
				break;
			}
			Messages.sendMessage("Player \"" + player.getPlayerName() + "\" was unregistered successfully.", channel);
			break;

		case "list":
			List<Player> players = db.getAllStoredPlayers();
			Map<String, String> nameWithUrl = new HashMap<String, String>();

			if (players.isEmpty()) {
				Messages.sendMessage("No players are being tracked! Use \"ru register <URL>\".", channel);
				break;
			}

			for (Player p : players) {
				nameWithUrl.put(Format.bold(p.getPlayerName()), ApiConstants.USER_PRE_URL + p.getPlayerId());
			}
			Messages.sendMessage(nameWithUrl, channel);
			break;

		case "registerall":
			List<Member> guildMembers = channel.getGuild().getMembers();
			List<Long> storedUserIds = db.getAllStoredPlayers().stream().map(p -> p.getDiscordUserId()).collect(Collectors.toList());

			List<Member> missingMembers = guildMembers.stream().filter(m -> !storedUserIds.contains(m.getIdLong()) && !m.getUser().isBot()).collect(Collectors.toList());
			List<String> missingMemberNames = missingMembers.stream().map(m -> m.getEffectiveName()).collect(Collectors.toList());

			String message = Format.bold("These members are not registered yet:\n\n");
			message = message.concat(String.join("\n", missingMemberNames));
			Messages.sendMessage(message, channel);

			event.getJDA().addEventListener(new RegisterAllListener(channel, event.getAuthor(), missingMembers, db));
			break;
		case "randomquote":
			Guild foaaGuild = event.getJDA().getGuildById(BotConstants.foaaServerId);
			if (foaaGuild == null) {
				Messages.sendMessage("Could not find guild which contains the quote channel.", channel);
				break;
			}
			TextChannel quoteChannel = foaaGuild.getTextChannels().stream().filter(c -> c.getName().equals("quotes")).findFirst().orElse(null);
			try {
				List<Message> quotes = quoteChannel.getIterableHistory().takeAsync(10000).get();
				List<Attachment> attachments = quotes.stream().map(quote -> quote.getAttachments().size() > 0 ? quote.getAttachments().get(0) : null).filter(attachment -> attachment != null && attachment.isImage()).collect(Collectors.toList());
				if (attachments.size() > 0) {
					Attachment randomImage = RandomUtils.getRandomItem(attachments);

					File resourcesFolder = new File("src/main/resources/");
					if (!resourcesFolder.exists()) {
						resourcesFolder.mkdirs();
					}
					
					String filePath = resourcesFolder.getAbsolutePath() + randomImage.getFileName();
					File image = new File(filePath);
					if (!image.exists()) {
						image.createNewFile();
						image = randomImage.downloadToFile(filePath).join();
					}
					Messages.sendImage(image, image.getName(), channel);
					image.deleteOnExit();
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			} catch (ExecutionException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			break;
		case "seal":
			Messages.sendImage(BotConstants.sealImageUrl, "Cute seal.jpg", channel);
			break;
		case "help":
			Messages.sendMessage(BotConstants.getCommands(), channel);
			break;
		default:
			Messages.sendMessage("Sorry, i don't speak wrong. 🤡  Try \"ru help\".", channel);
		}

	}

	@Override
	public void onReady(ReadyEvent event) {
		System.out.println("Ready!");
	}

	private Player getScoreSaberPlayerFromMessage(List<String> msgParts) throws FileNotFoundException {
		if (msgParts.size() < 3) {
			throw new FileNotFoundException("ScoreSaber URL is missing! Syntax: \"ru register <URL>\"");
		}
		String profileUrl = msgParts.get(2);
		String playerId = profileUrl.replace(ApiConstants.USER_PRE_URL, "");
		Player player = ss.getPlayerById(playerId);
		if (player == null) {
			throw new FileNotFoundException("Player could not be found!");
		}
		return player;
	}
}
