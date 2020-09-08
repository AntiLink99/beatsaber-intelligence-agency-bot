package bot.listeners;

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

	public EmbedReactionListener(Message reactionMessage, String embedTitle, Map<String, String> keyValuePairs) {
		this.reactionMessage = reactionMessage;
		this.embedTitle = embedTitle;
		this.keyValuePairs = (LinkedMap<String, String>) keyValuePairs;
		reactionMessage.addReaction(Emotes.ARROW_LEFT).queue();
		reactionMessage.addReaction(Emotes.ARROW_RIGHT).queue();

		new java.util.Timer().schedule(new java.util.TimerTask() {
			@Override
			public void run() {
				reactionMessage.getJDA().removeEventListener(EmbedReactionListener.this);
				Messages.editMessageStringMap(reactionMessage.getIdLong(), new HashMap<String, String>(), "⚔️ Your session has expired. ⚔️", "", reactionMessage.getTextChannel());
			}
		}, 300 * 1000);
	}

	@Override
	public void onMessageReactionAdd(MessageReactionAddEvent event) {
		if (event.getMessageIdLong() != reactionMessage.getIdLong())
			return;
		if (event.getMember().getUser().isBot())
			return;

		switch (event.getReactionEmote().getEmoji()) {
		case Emotes.ARROW_LEFT:
			if (isFirstPage()) {
				removeAllReactionsExcept(Emotes.ARROW_RIGHT);
			} else {
				currentPage--;
				showCurrentPage();
				addReactionIfNotExists(Emotes.ARROW_RIGHT);
			}
			break;
		case Emotes.ARROW_RIGHT:
			if (isLastPage()) {
				removeAllReactionsExcept(Emotes.ARROW_LEFT);
			} else {
				currentPage++;
				showCurrentPage();
				addReactionIfNotExists(Emotes.ARROW_LEFT);
			}
			break;
		default:
			return;
		}
		event.getReaction().removeReaction(event.getUser()).queue();
	}

	private void addReactionIfNotExists(String emote) {
		if (reactionMessage.getReactionByUnicode(emote) == null) {
			reactionMessage.addReaction(emote).queue();
		}
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

	private void removeAllReactionsExcept(String emoji) {
		// TODO
	}

	private boolean isFirstPage() {
		return currentPage == 1;
	}

	private boolean isLastPage() {
		return keyValuePairs.size() <= currentPage * BotConstants.entriesPerReactionPage;
	}
}
