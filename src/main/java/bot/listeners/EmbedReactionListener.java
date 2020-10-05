package bot.listeners;

import java.time.Duration;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.collections4.map.LinkedMap;

import bot.main.BotConstants;
import bot.utils.Emotes;
import bot.utils.MapUtils;
import bot.utils.Messages;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class EmbedReactionListener extends ListenerAdapter {
	private Message reactionMessage;
	private String embedTitle;
	private LinkedMap<String, String> keyValuePairs;
	private int currentPage = 1;
	private LocalTime lastReactionTime;
	private long lastReactionUserId = -1;

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
				Messages.editMessageStringMap(reactionMessage.getIdLong(), new HashMap<>(), "⚔️ Your session has expired. ⚔️", "", reactionMessage.getTextChannel());
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
			event.getReaction().removeReaction(event.getUser()).queue();
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
		event.getReaction().removeReaction(event.getUser()).queue();
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
		int startIndex = (currentPage - 1) * BotConstants.entriesPerReactionPage;
		int endIndex = (currentPage - 1) * BotConstants.entriesPerReactionPage + BotConstants.entriesPerReactionPage;

		if (keyValuePairs.size() - 1 < endIndex) {
			endIndex = keyValuePairs.size();
		}

		LinkedMap<String, String> subMap = MapUtils.getSubMap(keyValuePairs, startIndex, endIndex);
		Messages.editMessageStringMap(reactionMessage.getIdLong(), subMap, embedTitle, "Page " + currentPage, reactionMessage.getTextChannel());
	}

	private boolean isFirstPage() {
		return currentPage == 1;
	}

	private boolean isLastPage() {
		return keyValuePairs.size() <= currentPage * BotConstants.entriesPerReactionPage;
	}
}
