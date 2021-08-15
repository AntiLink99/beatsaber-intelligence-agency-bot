package bot.utils;

import java.util.List;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.TextChannel;

public class DiscordLogger {

	public static String INFO = "info";
	public static String ERRORS = "errors";
	public static String HTTP_ERRORS = "http-errors";
	public static String FOAA_REFRESH = "foaa-refresh";
	public static String RECENTSONGS_TOPSONGS = "recentsongs-topsongs";
	
	private static Guild guild;
	
	public static void setLogGuild(Guild guild) {
		DiscordLogger.guild = guild;
	}
	public static void sendLogInChannel(String message, String channelName) {
		List<TextChannel> channels = guild.getTextChannelsByName(channelName, true);
		if (!channels.isEmpty()) {
			TextChannel channel = channels.get(0);
			Messages.sendPlainMessage(message, channel);
		}
	}
}
