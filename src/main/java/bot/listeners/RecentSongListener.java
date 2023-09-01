package bot.listeners;

import bot.commands.scoresaber.RecentSong;
import bot.db.DatabaseManager;
import bot.dto.LeaderboardService;
import bot.dto.MessageEventDTO;
import bot.dto.MessageEventType;
import bot.dto.player.DataBasePlayer;
import bot.dto.rankedmaps.RankedMaps;
import bot.utils.Messages;
import net.dv8tion.jda.api.events.interaction.ButtonClickEvent;
import net.dv8tion.jda.api.hooks.EventListener;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.InteractionHook;

import java.util.Timer;

public class RecentSongListener extends ListenerAdapter implements EventListener {
    private final MessageEventDTO userEvent;
    private long messageReferenceId;
    private final DataBasePlayer player;
    private final int index;

    DatabaseManager db;
    RankedMaps ssRanked;

    Timer timer;

    public RecentSongListener(DataBasePlayer player, int index, MessageEventDTO userEvent, DatabaseManager db, RankedMaps ssRanked) {
        this.userEvent = userEvent;
        this.player = player;
        this.index = index;
        this.db = db;
        this.ssRanked = ssRanked;

        sendAskForServiceEmbed(userEvent);
        timer = new java.util.Timer();
        timer.schedule(new java.util.TimerTask() {
            @Override
            public void run() {
                System.out.println("Deleting Songs Listener... Left:" + userEvent.getJDA().getEventManager().getRegisteredListeners().size());
                userEvent.getJDA().removeEventListener(this);
            }
        }, 150 * 1000);
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
                RecentSong ssCommand = new RecentSong(db);
                ssCommand.sendRecentSong(LeaderboardService.SCORESABER, player, ssRanked, index, userEvent);
                break;
            case BEATLEADER:
                RecentSong blCommand = new RecentSong(db);
                blCommand.sendRecentSong(LeaderboardService.BEATLEADER, player, ssRanked, index, userEvent);
                break;
            case ACCSABER:
                RecentSong accCommand = new RecentSong(db);
                accCommand.sendRecentSong(LeaderboardService.ACCSABER, player, ssRanked, index, userEvent);
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
