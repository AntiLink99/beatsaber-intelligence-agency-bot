package bot.utils;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.TextChannel;

import java.util.List;

public class DiscordLogger {

    public static final String INFO = "info";
    public static final String ERRORS = "errors";
    public static final String HTTP_ERRORS = "http-errors";
    public static final String WATCHER_REFRESH = "foaa-refresh";
    public static final String USERS = "users";
    public static final String GUILDS = "guilds";
    public static final String DB = "database";

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
