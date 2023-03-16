package bot.listeners;

import bot.commands.accsaber.SongsCommandsACC;
import bot.commands.beatleader.SongsCommandsBL;
import bot.commands.scoresaber.SongsCommands;
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

public class SongsCommandsListener extends ListenerAdapter implements EventListener {
    private final SongsCommandsType type;
    private final MessageEventDTO userEvent;
    private long messageReferenceId;
    private final DataBasePlayer player;
    private final int index;

    DatabaseManager db;
    RankedMaps ssRanked;

    Timer timer;

    public SongsCommandsListener(SongsCommandsType type, DataBasePlayer player, int index, MessageEventDTO userEvent, DatabaseManager db, RankedMaps ssRanked) {
        this.type = type;
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
                if (type == SongsCommandsType.RECENT)
                    new SongsCommands(db, ssRanked).sendRecentSongs(player, index, userEvent);
                else
                    new SongsCommands(db, ssRanked).sendTopSongs(player, index, userEvent);
                break;
            case BEATLEADER:
                if (type == SongsCommandsType.RECENT)
                    new SongsCommandsBL(db).sendRecentSongs(player, index, userEvent);
                else
                    new SongsCommandsBL(db).sendTopSongs(player, index, userEvent);
                break;
            case ACCSABER:
                if (type == SongsCommandsType.RECENT)
                    new SongsCommandsACC(db).sendRecentSongs(player, index, userEvent);
                else
                    new SongsCommandsACC(db).sendTopSongs(player, index, userEvent);
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
