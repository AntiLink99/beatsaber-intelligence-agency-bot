package bot.commands.beatleader;

import bot.api.BeatLeader;
import bot.db.DatabaseManager;
import bot.dto.LeaderboardServicePlayer;
import bot.dto.MessageEventDTO;
import bot.dto.PlayerScore;
import bot.dto.beatleader.scores.Player;
import bot.dto.player.DataBasePlayer;
import bot.dto.supporters.SupporterInfo;
import bot.graphics.SongsImage;
import bot.main.BotConstants;
import bot.utils.JavaFXUtils;
import bot.utils.Messages;

import java.io.File;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class SongsCommandsBL {
    final DatabaseManager db;

    public SongsCommandsBL(DatabaseManager db) {
        this.db = db;
    }

    private void sendSongs(DataBasePlayer player, SupporterInfo supportInfo, int index, MessageEventDTO event, boolean isTopSongs) {
        String songType = isTopSongs ? "topSongs" : "recentSongs";

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

        List<PlayerScore> scores = isTopSongs ?
                bl.getTopScoresByPlayerIdAndPage(Long.parseLong(playerId), index) :
                bl.getRecentScoresByPlayerIdAndPage(Long.parseLong(playerId), index);

        if (scores == null || scores.isEmpty()) {
            Messages.sendMessage("Scores could not be fetched. Please try again later.", event.getChannel());
            return;
        }
        String filePath = BotConstants.RESOURCES_PATH+ songType + "_" + playerId + "_" + messageId + ".png";

        generateImage(scores, supportInfo, filePath);

        File songImage = new File(filePath);
        if (songImage.exists()) {
            Messages.sendImage(songImage, songType + "_" + playerId + "_" + messageId + ".png", event);
            songImage.delete();
        }
    }

    private void generateImage(List<PlayerScore> scores, SupporterInfo supportInfo, String filePath) {
        // Remove old image file if exists
        File songImage = new File(filePath);
        if (songImage.exists()) {
            songImage.delete();
        }

        SongsImage.setFilePath(filePath);
        SongsImage.setScores(scores);
        LeaderboardServicePlayer imagePlayer = scores.get(0) != null ? scores.get(0).getLeaderboardPlayer() : new Player();
        SongsImage.setPlayer(imagePlayer);
        SongsImage.setSupporterInfo(supportInfo);
        JavaFXUtils.launch(SongsImage.class);

        int songsWaitingCounter = 0;
        SongsImage.setFinished(false); // Timing Problem Fix
        while (!SongsImage.isFinished()) {

            if (songsWaitingCounter > 30) {
                break;
            }
            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            songsWaitingCounter++;
        }
    }

    public void sendRecentSongs(DataBasePlayer player, SupporterInfo supportInfo, int index, MessageEventDTO event) {
        sendSongs(player, supportInfo, index, event, false);
    }

    public void sendTopSongs(DataBasePlayer player, SupporterInfo supportInfo, int index, MessageEventDTO event) {
        sendSongs(player, supportInfo, index, event, true);
    }
}