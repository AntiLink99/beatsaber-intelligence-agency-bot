package bot.listeners;

import bot.main.BotConstants;
import bot.utils.MapUtils;
import bot.utils.Messages;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.interaction.ButtonClickEvent;
import net.dv8tion.jda.api.hooks.EventListener;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.Button;
import net.dv8tion.jda.api.requests.restaction.interactions.UpdateInteractionAction;
import org.apache.commons.collections4.map.LinkedMap;

import java.time.Duration;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class EmbedButtonListener extends ListenerAdapter implements EventListener {
    private final Message reactionMessage;
    private final String embedTitle;
    private final LinkedMap<String, String> keyValuePairs;
    private int currentPage = 1;
    private LocalTime lastReactionTime;
    private long lastReactionUserId = -1;
    private final boolean isInline = false;
    private final int entriesPerPage = BotConstants.entriesPerReactionPage;

    public EmbedButtonListener(Message reactionMessage, String embedTitle, Map<String, String> keyValuePairs) {
        this.reactionMessage = reactionMessage;
        this.embedTitle = embedTitle;
        this.keyValuePairs = (LinkedMap<String, String>) keyValuePairs;
        this.lastReactionTime = LocalTime.now();

        new java.util.Timer().schedule(new java.util.TimerTask() {
            @Override
            public void run() {
                reactionMessage.getJDA().removeEventListener(EmbedButtonListener.this);
                Messages.editMessageStringMap(reactionMessage.getIdLong(), Collections.emptyMap(), "⚔️ Your session has expired. ⚔️", "", isInline, reactionMessage.getTextChannel());
            }
        }, 30 * 1000);
    }

    @Override
    public void onButtonClick(ButtonClickEvent event) {
        if (event.getMessageIdLong() != reactionMessage.getIdLong())
            return;
        if (event.getMember() != null && event.getMember().getUser().isBot())
            return;

        if (userReactedTooQuickly(lastReactionTime) && event.getUser().getIdLong() == lastReactionUserId) {
            lastReactionTime = LocalTime.now();
            Messages.sendTempMessage(event.getMember().getAsMention() + ", calm down okay? 🤡", 10, event.getChannel());
            return;
        }
        lastReactionTime = LocalTime.now();
        lastReactionUserId = event.getUser().getIdLong();

        switch (event.getComponentId()) {
            case "previousPage":
                if (isNotFirstPage()) {
                    currentPage--;
                    UpdateInteractionAction updateAction = event.deferEdit();
                    showCurrentPage(updateAction);
                }
                break;
            case "nextPage":
                if (isNotLastPage()) {
                    currentPage++;
                    UpdateInteractionAction updateAction = event.deferEdit();
                    showCurrentPage(updateAction);
                }
                break;
        }
    }

    private boolean userReactedTooQuickly(LocalTime lastReaction) {
        Duration duration = Duration.between(lastReaction, LocalTime.now());
        long seconds = duration.getSeconds();
        int nano = duration.getNano();
		return seconds == 0 && nano < BotConstants.improvementReactionMinTimeDiff;
	}

    private void showCurrentPage(UpdateInteractionAction updateAction) {
        int startIndex = (currentPage - 1) * entriesPerPage;
        int endIndex = (currentPage - 1) * entriesPerPage + entriesPerPage;

        if (keyValuePairs.size() - 1 < endIndex) {
            endIndex = keyValuePairs.size();
        }

        String footer = "Page " + currentPage;
        LinkedMap<String, String> subMap = MapUtils.getSubMap(keyValuePairs, startIndex, endIndex);

        EmbedBuilder builder = new EmbedBuilder();
        for (String key : subMap.keySet()) {
            builder.addField(key, subMap.get(key), isInline);
        }
        builder.setColor(Messages.embedColor);
        builder.setFooter(footer);
        builder.setTitle(embedTitle);

        List<Button> newButtons = new ArrayList<>();
        if (isNotFirstPage()) {
            newButtons.add(Button.secondary("previousPage", "Previous Page"));
        }
        if (isNotLastPage()) {
            newButtons.add(Button.secondary("nextPage", "Next Page"));
        }

        updateAction.setEmbeds(builder.build())
                .setActionRows(ActionRow.of(newButtons))
                .queue();
    }

    private boolean isNotFirstPage() {
        return currentPage != 1;
    }

    private boolean isNotLastPage() {
        return keyValuePairs.size() > currentPage * entriesPerPage;
    }
}
