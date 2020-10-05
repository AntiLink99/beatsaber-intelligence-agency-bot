package bot.commands;

import java.util.List;
import java.util.stream.Collectors;

import bot.db.DatabaseManager;
import bot.listeners.RegisterAllListener;
import bot.utils.Format;
import bot.utils.Messages;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class RegisterAll {

	public static void registerAllMembers(DatabaseManager db, MessageReceivedEvent event) {
		TextChannel channel = event.getTextChannel();
		List<Member> guildMembers = channel.getGuild().getMembers();
		List<Long> storedUserIds = db.getAllStoredPlayers().stream().map(p -> p.getDiscordUserId()).collect(Collectors.toList());

		List<Member> missingMembers = guildMembers.stream().filter(m -> !storedUserIds.contains(m.getIdLong()) && !m.getUser().isBot()).collect(Collectors.toList());
		List<String> missingMemberNames = missingMembers.stream().map(m -> m.getEffectiveName()).sorted(String.CASE_INSENSITIVE_ORDER).collect(Collectors.toList());

		if (missingMemberNames != null && missingMemberNames.size() > 0) {
			String message = Format.bold(missingMemberNames.size() + " members are not registered yet:\n\n");
			message = message.concat(String.join("\n", missingMemberNames));
			Messages.sendMessage(message, channel);
			event.getJDA().addEventListener(new RegisterAllListener(channel, event.getAuthor(), missingMembers, db));
		} else {
			Messages.sendMessage("All members are already registered.", channel);
		}
	}
}
