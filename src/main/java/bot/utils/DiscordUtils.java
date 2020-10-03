package bot.utils;

import java.util.List;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;

public class DiscordUtils {

	public static String getMemberNameFromId(List<Member> members, long id) {
		Member member = members.stream().filter(m -> m.getIdLong() == id).findFirst().orElse(null);
		if (member == null) {
			return "No Discord Member found!";
		}
		return member.getEffectiveName();
	}
	
	public static boolean isAdmin(User user) {
		return user.getIdLong() == 409042541841743889L;
	}
}
