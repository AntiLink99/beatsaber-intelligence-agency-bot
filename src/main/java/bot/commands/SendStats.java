package bot.commands;

import bot.dto.MessageEventDTO;
import bot.main.BotConstants;
import bot.utils.Messages;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.User;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class SendStats {

    public void sendStats(MessageEventDTO event) {
        User author = event.getAuthor().getUser();
        MessageChannel dmChannel = author.openPrivateChannel().complete();

        JDA jda = event.getJDA();
        List<Guild> guilds = jda.getGuilds().stream()
                .sorted(Comparator.comparingInt(Guild::getMemberCount))
                .collect(Collectors.toList());
        List<Member> members = guilds.stream()
                .flatMap(guild -> guild.getMembers().stream())
                .collect(Collectors.toList());

        StringBuilder statsResult = new StringBuilder();
        statsResult.append("Guild count:".toUpperCase())
                .append("   ")
                .append(guilds.size())
                .append("\n");
        statsResult.append("Member count:".toUpperCase())
                .append("   ")
                .append(members.size())
                .append("\n\n");
        for (Guild guild : guilds) {
            statsResult.append(guild.getName())
                    .append(":   ")
                    .append(guild.getMemberCount())
                    .append(" members")
                    .append("\n");
        }
        File statsFile = new File(BotConstants.RESOURCES_PATH+"stats_"+event.getId()+".txt");
        try (PrintWriter out = new PrintWriter(statsFile.getAbsolutePath())) {
            out.println(statsResult);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return;
        }

        Messages.sendFile(statsFile, statsFile.getName(), event.getChannel());
        statsFile.deleteOnExit();
    }
}
