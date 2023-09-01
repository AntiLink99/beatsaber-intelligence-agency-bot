package bot.listeners;

import bot.commands.UnifiedSongsCommands;
import bot.db.DatabaseManager;
import bot.dto.LeaderboardService;
import bot.dto.MessageEventDTO;
import bot.dto.MessageEventType;
import bot.dto.player.DataBasePlayer;
import bot.dto.rankedmaps.RankedMaps;
import bot.dto.supporters.SupporterInfo;
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
    private final SupporterInfo supportInfo;
    private final int index;

    DatabaseManager db;
    RankedMaps ssRanked;

    Timer timer;

    public SongsCommandsListener(SongsCommandsType type, DataBasePlayer player, SupporterInfo supportInfo, int index, MessageEventDTO userEvent, DatabaseManager db, RankedMaps ssRanked) {
        this.type = type;
        this.userEvent = userEvent;
        this.player = player;
        this.supportInfo = supportInfo;
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
                UnifiedSongsCommands ssCommand = new UnifiedSongsCommands(db, ssRanked);
                executeCommand(ssCommand, selectedService, type, player, supportInfo, index, userEvent);
                break;
            case BEATLEADER:
                UnifiedSongsCommands blCommand = new UnifiedSongsCommands(db);
                executeCommand(blCommand, selectedService, type, player, supportInfo, index, userEvent);
                break;
            case ACCSABER:
                UnifiedSongsCommands accCommand = new UnifiedSongsCommands(db);
                executeCommand(accCommand, selectedService, type, player, supportInfo, index, userEvent);
                break;
        }
        if (userEvent.getType() == MessageEventType.TEXT) {
            btnEvent.getMessage().delete().queue();
            replyMessage.deleteOriginal().queue();
        }
        timer.cancel();
        userEvent.getJDA().removeEventListener(this);
    }

    public void executeCommand(UnifiedSongsCommands unifiedSongService, LeaderboardService service, SongsCommandsType type, DataBasePlayer player, SupporterInfo supportInfo, int index, MessageEventDTO userEvent) {
        if (type == SongsCommandsType.RECENT) {
            unifiedSongService.sendRecentSongs(service, player, supportInfo, index, userEvent);
        } else {
            unifiedSongService.sendTopSongs(service, player, supportInfo, index, userEvent);
        }
    }

    public void sendAskForServiceEmbed(MessageEventDTO event) {
        messageReferenceId = Messages.sendAskServiceMessage(event);
    }
}
