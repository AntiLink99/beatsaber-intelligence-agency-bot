package bot.commands.accsaber;

import bot.api.AccSaber;
import bot.db.DatabaseManager;
import bot.dto.LeaderboardService;
import bot.dto.MessageEventDTO;
import bot.dto.PlayerScore;
import bot.dto.player.DataBasePlayer;
import bot.graphics.SongsImage;
import bot.main.BotConstants;
import bot.utils.JavaFXUtils;
import bot.utils.Messages;

import java.io.File;
import java.util.List;
import java.util.concurrent.TimeUnit;

//TODO Redundanzen zusammenfassen
// TODO RecentSong RecentSongs TopSongs
public class SongsCommandsACC {

    final DatabaseManager db;

    public SongsCommandsACC(DatabaseManager db) {
        this.db = db;
    }

    public void sendRecentSongs(DataBasePlayer player, int index, MessageEventDTO event) {
        if (player == null) {
            Messages.sendMessage("Player could not be found.", event.getChannel());
            return;
        }
        if (index > 6 || index < 1) {
            Messages.sendMessage("The value provided has to be an integer between 1 and 6.", event.getChannel());
            return;
        }

        AccSaber as = new AccSaber();

        player.setService(LeaderboardService.ACCSABER);
        String playerId = player.getId();
        String messageId = String.valueOf(event.getId());

        List<PlayerScore> scores = as.getRecentScoresByPlayerIdAndPage(Long.parseLong(player.getId()), index);
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
        SongsImage.setPlayer(player);
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

    public void sendTopSongs(DataBasePlayer player, int index, MessageEventDTO event) {
        if (player == null) {
            Messages.sendMessage("Player could not be found.", event.getChannel());
            return;
        }
        if (index > 6 || index < 1) {
            Messages.sendMessage("The value provided has to be an integer between 1 and 6.", event.getChannel());
            return;
        }

        AccSaber as = new AccSaber();

        player.setService(LeaderboardService.ACCSABER);
        String playerId = player.getId();
        String messageId = String.valueOf(event.getId());

        List<PlayerScore> scores = as.getTopScoresByPlayerIdAndPage(Long.parseLong(player.getId()), index);
        if (scores == null || scores.isEmpty()) {
            Messages.sendMessage("Scores could not be fetched. Please try again later.", event.getChannel());
            return;
        }
        String filePath = BotConstants.RESOURCES_PATH + "topSongs_" + playerId + "_" + messageId + ".png";
        File topSongsImage = new File(filePath);
        // Remove old image file if exists
        if (topSongsImage.exists()) {
            topSongsImage.delete();
        }

        SongsImage.setFilePath(filePath);
        SongsImage.setScores(scores);
        SongsImage.setPlayer(player);
        JavaFXUtils.launch(SongsImage.class);

        int topSongsWaitingCounter = 0;
        SongsImage.setFinished(false); // Timing Problem Fix
        while (!SongsImage.isFinished()) {

            if (topSongsWaitingCounter > 30) {
                break;
            }
            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            topSongsWaitingCounter++;
        }
        if (topSongsImage.exists()) {
            Messages.sendImage(topSongsImage, "topSongs_" + playerId + "_" + messageId + ".png", event.getChannel());
            topSongsImage.delete();
        }

    }
}
