package bot.commands;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.stream.Collectors;

import bot.dto.MessageEventDTO;
import bot.utils.Messages;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.User;

public class SendStats {

	public static void sendStats(MessageEventDTO event) {
		User author = event.getAuthor().getUser();		
		MessageChannel dmChannel = author.openPrivateChannel().complete();
		
		JDA jda = event.getJDA();	
		List<Guild> guilds = jda.getGuilds();	
		List<Member> members = guilds.stream().flatMap(guild -> guild.getMembers().stream()).collect(Collectors.toList());
		List<Member> membersDistinct = members.stream().distinct().collect(Collectors.toList());
		LinkedHashMap<String, String> stats = new LinkedHashMap<>();
		stats.put("Guild count".toUpperCase(), String.valueOf(guilds.size()));
		stats.put("Member count".toUpperCase(), String.valueOf(members.size()));
		stats.put("Member count (distinct)".toUpperCase(), String.valueOf(membersDistinct.size()));
		for (Guild guild : guilds) {		
			stats.put(guild.getName(), guild.getMemberCount() + " members");
		}
		Messages.sendMessageStringMap(stats, "Stats", dmChannel);	
	}
}
