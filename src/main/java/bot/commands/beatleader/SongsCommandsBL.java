package bot.commands.beatleader;

import bot.api.BeatLeader;
import bot.db.DatabaseManager;
import bot.dto.MessageEventDTO;
import bot.dto.PlayerScore;
import bot.dto.player.Player;
import bot.dto.rankedmaps.RankedMaps;
import bot.graphics.SongsImage;
import bot.main.BotConstants;
import bot.utils.JavaFXUtils;
import bot.utils.Messages;

import java.io.File;
import java.util.List;
import java.util.concurrent.TimeUnit;

//TODO Redundanzen zusammenfassen
// TODO RecentSong RecentSongs TopSongs
public class SongsCommandsBL {
    //TODO: TopSongs & RecentSongs redundant code

    final DatabaseManager db;
    final RankedMaps ranked;

    public SongsCommandsBL(DatabaseManager db, RankedMaps ranked) {
        this.db = db;
        this.ranked = ranked;
    }

    public void sendRecentSongs(Player player, int index, MessageEventDTO event) {
        if (player == null) {
            Messages.sendMessage("Player could not be found.", event.getChannel());
            return;
        }
        if (index > 50 || index < 1) {
            Messages.sendMessage("The value provided has to be an integer between 1 and 50.", event.getChannel());
            return;
        }

        BeatLeader bl = new BeatLeader();

        String playerId = player.getId();
        String messageId = String.valueOf(event.getId());

        List<PlayerScore> scores = bl.getRecentScoresByPlayerIdAndPage(Long.parseLong(player.getId()), index);
        if (scores == null || scores.isEmpty()) {
            Messages.sendMessage("Scores could not be fetched. Please try again later.", event.getChannel());
            return;
        }
        String filePath = BotConstants.RESOURCES_PATH+"recentSongs_" + playerId + "_" + messageId + ".png";
        File recentSongsImage = new File(filePath);
        // Remove old image file if exists
        if (recentSongsImage.exists()) {
            recentSongsImage.delete();
        }

        SongsImage.setFilePath(filePath);
        SongsImage.setScores(scores);
        JavaFXUtils.launch(SongsImage.class);

        int recentSongsWaitingCounter = 0;
        SongsImage.setFinished(false); // Timing Problem Fix
        while (!SongsImage.isFinished()) {

            if (recentSongsWaitingCounter > 30) {
                break;
            }
            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            recentSongsWaitingCounter++;
        }
        if (recentSongsImage.exists()) {
            Messages.sendImage(recentSongsImage, "recentSongs_" + playerId + "_" + messageId + ".png", event.getChannel());
            recentSongsImage.delete();
        }

    }

    public void sendTopSongs(Player player, int index, MessageEventDTO event) {
        if (player == null) {
            Messages.sendMessage("Player could not be found.", event.getChannel());
            return;
        }
        if (index > 50 || index < 1) {
            Messages.sendMessage("The value provided has to be an integer between 1 and 50.", event.getChannel());
            return;
        }

        BeatLeader bl = new BeatLeader();

        String playerId = player.getId();
        String messageId = String.valueOf(event.getId());

        List<PlayerScore> scores = bl.getTopScoresByPlayerIdAndPage(Long.parseLong(player.getId()), index);
        if (scores == null || scores.isEmpty()) {
            Messages.sendMessage("Scores could not be fetched. Please try again later.", event.getChannel());
            return;
        }
        String filePath = BotConstants.RESOURCES_PATH+"topSongs_" + playerId + "_" + messageId + ".png";
        File recentSongsImage = new File(filePath);
        // Remove old image file if exists
        if (recentSongsImage.exists()) {
            recentSongsImage.delete();
        }

        SongsImage.setFilePath(filePath);
        SongsImage.setScores(scores);
        JavaFXUtils.launch(SongsImage.class);

        int recentSongsWaitingCounter = 0;
        SongsImage.setFinished(false); // Timing Problem Fix
        while (!SongsImage.isFinished()) {

            if (recentSongsWaitingCounter > 30) {
                break;
            }
            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            recentSongsWaitingCounter++;
        }
        if (recentSongsImage.exists()) {
            Messages.sendImage(recentSongsImage, "topSongs_" + playerId + "_" + messageId + ".png", event.getChannel());
            recentSongsImage.delete();
        }

    }
}
