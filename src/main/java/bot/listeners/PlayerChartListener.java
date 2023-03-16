package bot.listeners;

import bot.api.AccSaber;
import bot.api.BeatLeader;
import bot.commands.chart.PlayerChart;
import bot.dto.LeaderboardService;
import bot.dto.MessageEventDTO;
import bot.dto.MessageEventType;
import bot.dto.beatleader.history.PlayerHistoryItem;
import bot.dto.player.DataBasePlayer;
import bot.utils.ListValueUtils;
import bot.utils.Messages;
import net.dv8tion.jda.api.events.interaction.ButtonClickEvent;
import net.dv8tion.jda.api.hooks.EventListener;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.InteractionHook;

import java.awt.*;
import java.util.List;
import java.util.*;
import java.util.stream.Collectors;

public class PlayerChartListener extends ListenerAdapter implements EventListener {
    private final MessageEventDTO userEvent;
    private long messageReferenceId;
    private final DataBasePlayer player;

    Timer timer;

    public PlayerChartListener(DataBasePlayer player, MessageEventDTO userEvent) {
        this.userEvent = userEvent;
        this.player = player;

        sendAskForServiceEmbed(userEvent);
        timer = new Timer();
        timer.schedule(new java.util.TimerTask() {
            @Override
            public void run() {
                System.out.println("Deleting Chart Listener... Left:" + userEvent.getJDA().getEventManager().getRegisteredListeners().size());
                userEvent.getJDA().removeEventListener(this);
            }
        }, 30 * 1000);
    }

    @Override
    public void onButtonClick(ButtonClickEvent btnEvent) {
        if (userEvent.getType() == MessageEventType.TEXT && btnEvent.getMessageIdLong() != messageReferenceId)
            return;
        if (btnEvent.getMember() != null && btnEvent.getMember().getUser().isBot())
            return;
        if (!userEvent.getAuthor().getId().equals(btnEvent.getMember().getId()))
            return;
        if (btnEvent.isAcknowledged())
            return;

        LeaderboardService selectedService = LeaderboardService.valueOf(btnEvent.getButton().getId());
        InteractionHook replyMessage = btnEvent
                .reply("Loading image... Service: " + selectedService.name())
                .setEphemeral(true)
                .complete();

        switch (selectedService) {
            case SCORESABER:
                player.setHistoryValues(ListValueUtils.addElementReturnList(player.getHistoryValues(), player.getRank()));
                new PlayerChart().sendChartImage(player, new Color(255, 222, 24), selectedService, userEvent);
                break;
            case BEATLEADER:
                BeatLeader bl = new BeatLeader();
                List<PlayerHistoryItem> playerHistory = bl.getPlayerHistoryById(player.getPlayerIdLong());
                List<Integer> blHistoryValues = playerHistory.stream()
                        .map(PlayerHistoryItem::getRank)
                        .collect(Collectors.toList());
                Collections.reverse(blHistoryValues);
                player.setHistoryValues(blHistoryValues);
                new PlayerChart().sendChartImage(player, new Color(41, 202, 251), selectedService, userEvent);
                break;
            case ACCSABER:
                AccSaber as = new AccSaber();
                Map<String, Integer> history = as.getPlayerHistoryValues(player.getId());
                List<Integer> historyValues = new ArrayList<>(history.values()).subList(history.size() - 49, history.size());
                player.setHistoryValues(historyValues);
                new PlayerChart().sendChartImage(player, new Color(4, 235, 32), selectedService, userEvent);
                break;
        }
        if (userEvent.getType() == MessageEventType.TEXT) {
            btnEvent.getMessage().delete().queue();
            replyMessage.deleteOriginal().queue();
        }
        timer.cancel();
        userEvent.getJDA().removeEventListener(this);
    }

    public void sendAskForServiceEmbed(MessageEventDTO event) {
        messageReferenceId = Messages.sendAskServiceMessage(event);
    }
}
