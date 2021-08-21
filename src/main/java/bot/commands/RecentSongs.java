package bot.commands;

import bot.api.ApiConstants;
import bot.api.ScoreSaber;
import bot.dto.SongScore;
import bot.dto.beatsaviour.RankedMaps;
import bot.dto.player.Player;
import bot.utils.Format;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.awt.*;
import java.util.List;

public class RecentSongs {

    final RankedMaps ranked;

    public RecentSongs(RankedMaps ranked) {
        this.ranked = ranked;
    }

    public void sendRecentSongs(Player player, MessageReceivedEvent event) {
        ScoreSaber ss = new ScoreSaber();
        List<SongScore> scores = ss.getRecentScoresByPlayerId(Long.parseLong(player.getPlayerId()));
        EmbedBuilder builder = new EmbedBuilder();
        builder.setTitle("-- Recent Songs --");
        builder.setAuthor(player.getPlayerName(), ApiConstants.USER_PRE_URL + player.getPlayerId(), ApiConstants.SS_PRE_URL + player.getAvatar());
        for (SongScore score : scores) {
            boolean isRanked = score.getMaxScore() != 0;

            String scoreTitle = "[" + (scores.indexOf(score) + 1) + "] " + score.getSongName() + " (" + score.getDifficultyName() + ")";
            String scoreDescription = "#" + score.getRank() + "\n";
            if (isRanked) {
                scoreDescription += score.getAccuracyString() + "\n" + score.getPpString() + "\n";
            } else {
                scoreDescription += "\n\n";
            }
            scoreDescription = Format.codeAutohotkey(scoreDescription + score.getRelativeTimeString());
            builder.addField(scoreTitle, scoreDescription, true);
        }
        builder.setColor(Color.CYAN);
        builder.setFooter("Enter \"ru recentsong <ID>\" to show a specific song.");
        event.getChannel().sendMessage(builder.build()).queue();
    }
}
