package bot.commands;

import bot.db.DatabaseManager;
import bot.dto.MessageEventDTO;
import bot.dto.player.DataBasePlayer;
import bot.listeners.RegisterAllListener;
import bot.utils.Format;
import bot.utils.Messages;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;

import java.util.List;
import java.util.stream.Collectors;

public class RegisterAll {

    final DatabaseManager db;

    public RegisterAll(DatabaseManager db) {
        this.db = db;
    }

    public void registerAllMembers(MessageEventDTO event) {
        TextChannel channel = event.getChannel();
        List<Member> guildMembers = channel.getGuild().getMembers();
        List<Long> storedUserIds = db.getAllStoredPlayers().stream().map(DataBasePlayer::getDiscordUserId).collect(Collectors.toList());

        List<Member> missingMembers = guildMembers.stream().filter(m -> !storedUserIds.contains(m.getIdLong()) && !m.getUser().isBot()).collect(Collectors.toList());
        List<String> missingMemberNames = missingMembers.stream().map(Member::getEffectiveName).sorted(String.CASE_INSENSITIVE_ORDER).collect(Collectors.toList());

        if (missingMemberNames.size() > 0) {
            String message = Format.bold(missingMemberNames.size() + " members are not registered yet:\n\n");
            message = message.concat(String.join("\n", missingMemberNames));
            Messages.sendMessage(message, channel);
            event.getJDA().addEventListener(new RegisterAllListener(channel, event.getAuthor().getUser(), missingMembers, db));
        } else {
            Messages.sendMessage("All members are already registered.", channel);
        }
    }
}
