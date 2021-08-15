package bot.listeners;

import java.time.Duration;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.collections4.map.LinkedMap;

import bot.main.BotConstants;
import bot.utils.DiscordLogger;
import bot.utils.Emotes;
import bot.utils.MapUtils;
import bot.utils.Messages;
import dto.discord.MessageAuthor;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class EmbedReactionListener extends ListenerAdapter {
	private Message reactionMessage;
	private String embedTitle;
	private String embedFooter;
	private MessageAuthor author;
	private LinkedMap<String, String> keyValuePairs;
	private int currentPage = 1;
	private LocalTime lastReactionTime;
	private long lastReactionUserId = -1;
	private boolean isInline = false;
	private int entriesPerPage = BotConstants.entriesPerReactionPage;

	public EmbedReactionListener(Message reactionMessage, String embedTitle, Map<String, String> keyValuePairs) {
		this.reactionMessage = reactionMessage;
		this.embedTitle = embedTitle;
		this.keyValuePairs = (LinkedMap<String, String>) keyValuePairs;
		this.lastReactionTime = LocalTime.now();
		reactionMessage.addReaction(Emotes.ARROW_LEFT).queue();
		reactionMessage.addReaction(Emotes.ARROW_RIGHT).queue();

		new java.util.Timer().schedule(new java.util.TimerTask() {
			@Override
			public void run() {
				reactionMessage.getJDA().removeEventListener(EmbedReactionListener.this);
				Messages.editMessageStringMap(reactionMessage.getIdLong(), new HashMap<>(), "⚔️ Your session has expired. ⚔️", "", null, isInline, reactionMessage.getTextChannel());
			}
		}, 300 * 1000);
	}

	public EmbedReactionListener(Message reactionMessage, String embedTitle, MessageAuthor author, String embedFooter, boolean isInline, int entriesPerPage, Map<String, String> keyValuePairs) {
		this.reactionMessage = reactionMessage;
		this.embedTitle = embedTitle;
		this.keyValuePairs = (LinkedMap<String, String>) keyValuePairs;
		this.embedFooter = embedFooter;
		this.author = author;
		this.lastReactionTime = LocalTime.now();
		this.isInline = isInline;
		this.entriesPerPage = entriesPerPage;
		try {
			reactionMessage.addReaction(Emotes.ARROW_LEFT).queue();
			reactionMessage.addReaction(Emotes.ARROW_RIGHT).queue();
		} catch (Exception e) {
			DiscordLogger.sendLogInChannel("Could not add reactions on guild \"" + reactionMessage.getGuild().getName() + "\"", DiscordLogger.INFO);
		}
		new java.util.Timer().schedule(new java.util.TimerTask() {
			@Override
			public void run() {
				reactionMessage.getJDA().removeEventListener(EmbedReactionListener.this);
				Messages.editMessageStringMap(reactionMessage.getIdLong(), new HashMap<>(), "⚔️ Your session has expired. ⚔️", "", author, isInline, reactionMessage.getTextChannel());
			}
		}, 300 * 1000);
	}

	@Override
	public void onMessageReactionAdd(MessageReactionAddEvent event) {
		if (event.getMessageIdLong() != reactionMessage.getIdLong())
			return;
		if (event.getMember().getUser().isBot())
			return;

		if (userReactedTooQuickly(lastReactionTime) && event.getUser().getIdLong() == lastReactionUserId) {
			try {
				event.getReaction().removeReaction(event.getUser()).queue();
			} catch (Exception e) {
				//ok
			}
			lastReactionTime = LocalTime.now();
			Messages.sendTempMessage(event.getMember().getAsMention() + ", calm down okay? 🤡", 10, event.getChannel());
			return;
		}
		lastReactionTime = LocalTime.now();
		lastReactionUserId = event.getUser().getIdLong();

		switch (event.getReactionEmote().getEmoji()) {
		case Emotes.ARROW_LEFT:
			if (!isFirstPage()) {
				currentPage--;
				showCurrentPage();
			}
			break;
		case Emotes.ARROW_RIGHT:
			if (!isLastPage()) {
				currentPage++;
				showCurrentPage();
			}
			break;
		default:
			return;
		}
		try {
			event.getReaction().removeReaction(event.getUser()).queue();
		} catch (Exception e) {
			DiscordLogger.sendLogInChannel("Could not remove reaction on guild \"" + event.getGuild().getName() + "\"", DiscordLogger.INFO);
		}
	}

	private boolean userReactedTooQuickly(LocalTime lastReaction) {
		Duration duration = Duration.between(lastReaction, LocalTime.now());
		long seconds = duration.getSeconds();
		int nano = duration.getNano();
		if (seconds == 0 && nano < BotConstants.improvementReactionMinTimeDiff) {
			return true;
		}
		return false;
	}

	private void showCurrentPage() {
		int startIndex = (currentPage - 1) * entriesPerPage;
		int endIndex = (currentPage - 1) * entriesPerPage + entriesPerPage;

		if (keyValuePairs.size() - 1 < endIndex) {
			endIndex = keyValuePairs.size();
		}

		String footer = embedFooter == null ? "Page " + currentPage : embedFooter;
		LinkedMap<String, String> subMap = MapUtils.getSubMap(keyValuePairs, startIndex, endIndex);
		Messages.editMessageStringMap(reactionMessage.getIdLong(), subMap, embedTitle, footer, author, isInline, reactionMessage.getTextChannel());
	}

	private boolean isFirstPage() {
		return currentPage == 1;
	}

	private boolean isLastPage() {
		return keyValuePairs.size() <= currentPage * entriesPerPage;
	}

	public boolean isInline() {
		return isInline;
	}

	public void setInline(boolean isInline) {
		this.isInline = isInline;
	}

	public int getEntriesPerPage() {
		return entriesPerPage;
	}

	public void setEntriesPerPage(int entriesPerPage) {
		this.entriesPerPage = entriesPerPage;
	}
}
