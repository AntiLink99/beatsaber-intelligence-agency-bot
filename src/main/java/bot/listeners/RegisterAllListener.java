package bot.listeners;

import bot.api.ApiConstants;
import bot.api.ScoreSaber;
import bot.db.DatabaseManager;
import bot.dto.player.Player;
import bot.utils.Messages;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.util.List;

public class RegisterAllListener extends ListenerAdapter {

    private final long channelId, authorId;
    RegisterAllState progressionState = RegisterAllState.WAITING;
    Member currentMember;
    final DatabaseManager db;
    final ScoreSaber ss;
    private final List<Member> missingMembers;

    public RegisterAllListener(MessageChannel channel, User author, List<Member> missingMembers, DatabaseManager db) {
        this.channelId = channel.getIdLong();
        this.authorId = author.getIdLong();
        this.missingMembers = missingMembers;
        this.db = db;
        this.ss = new ScoreSaber();

        Messages.sendMessage("Do you want to register missing members? [y/n]", channel);
    }

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        if (event.getAuthor().isBot())
            return; // don't respond to other bots
        if (event.getChannel().getIdLong() != channelId)
            return; // ignore other channels
        if (progressionState == RegisterAllState.FINISHED)
            return;
        MessageChannel channel = event.getChannel();
        String content = event.getMessage().getContentRaw();

        if (content.equalsIgnoreCase("stop")) {
            channel.sendMessage("Ok, registering stopped.").queue();
            event.getJDA().removeEventListener(this);
            progressionState = RegisterAllState.FINISHED;
            return;
        } else if (event.getAuthor().getIdLong() == authorId) {

            if (progressionState == RegisterAllState.WAITING) {
                switch (content) {
                    case "y":
                        currentMember = missingMembers.get(0);
                        Messages.sendMessage("Please enter the ScoreSaber URL for this member: " + currentMember.getEffectiveName(), channel);
                        progressionState = RegisterAllState.AWAIT_SS_URL;
                        break;
                    case "n":
                    default:
                        event.getJDA().removeEventListener(this);
                        channel.sendMessage("Ok, no registering.").queue();
                        progressionState = RegisterAllState.FINISHED;
                        return;
                }
            } else if (progressionState == RegisterAllState.AWAIT_SS_URL) {
                if (content.equalsIgnoreCase("skip")) {
                    nextMember(channel, event);
                    return;
                }
                String playerId = content.replace(ApiConstants.USER_PRE_URL, "");
                Player player = ss.getPlayerById(playerId);
                if (player == null) {
                    Messages.sendMessage("Could not find a player with this URL.", channel);
                    return;
                }

                player.setDiscordUserId(currentMember.getIdLong());
                boolean successSave = db.savePlayer(player);
                if (!successSave) {
                    Messages.sendMessage("The player \"" + player.getPlayerName() + "\" is already registered! Use \"ru unregister <URL / Username>\" to remove the player from the database.", channel);
                    return;
                }
                event.getMessage().addReaction("✔").queue();
                nextMember(channel, event);
            }
        }
    }

    private void nextMember(MessageChannel channel, MessageReceivedEvent event) {
        int currentIndex = missingMembers.indexOf(currentMember);
        if (currentIndex + 1 < missingMembers.size()) {
            currentMember = missingMembers.get(currentIndex + 1);
            Messages.sendMessage("Please enter the ScoreSaber URL for this member: " + currentMember.getEffectiveName(), channel);
        } else {
            Messages.sendMessage("All members are now registered.", channel);
            event.getJDA().removeEventListener(this);
            progressionState = RegisterAllState.FINISHED;
        }
    }
}
