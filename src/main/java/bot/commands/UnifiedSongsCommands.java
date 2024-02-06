package bot.commands;

import bot.api.AccSaber;
import bot.api.BeatLeader;
import bot.api.ScoreSaber;
import bot.db.DatabaseManager;
import bot.dto.LeaderboardService;
import bot.dto.MessageEventDTO;
import bot.dto.PlayerScore;
import bot.dto.player.DataBasePlayer;
import bot.dto.scoresaber.PlayerScoreSS;
import bot.dto.supporters.SupporterInfo;
import bot.graphics.songsImage.SongsImage;
import bot.graphics.songsImage.SongsImageParams;
import bot.main.BotConstants;
import bot.utils.Messages;
import javafx.application.Platform;
import javafx.stage.Stage;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class UnifiedSongsCommands {
    final DatabaseManager db;
    Object serviceSpecific;

    public UnifiedSongsCommands(DatabaseManager db) {
        this.db = db;
    }

    public UnifiedSongsCommands(DatabaseManager db, Object serviceSpecific) {
        this.db = db;
        this.serviceSpecific = serviceSpecific;
    }

    private List<PlayerScore> fetchScores(LeaderboardService service, long playerId, int index, boolean isTopSongs) {
        switch (service) {
            case BEATLEADER:
                BeatLeader bl = new BeatLeader();
                return isTopSongs ? bl.getTopScoresByPlayerIdAndPage(playerId, index) : bl.getRecentScoresByPlayerIdAndPage(playerId, index);
            case SCORESABER:
                ScoreSaber ss = new ScoreSaber();
                List<PlayerScoreSS> scores = isTopSongs ? ss.getTopScoresByPlayerIdAndPage(playerId, index) : ss.getRecentScoresByPlayerIdAndPage(playerId, index);
                return new ArrayList<>(scores);
            case ACCSABER:
                AccSaber as = new AccSaber();
                return isTopSongs ? as.getTopScoresByPlayerIdAndPage(playerId, index) : as.getRecentScoresByPlayerIdAndPage(playerId, index);
            default:
                return null;
        }
    }

    private void sendSongs(LeaderboardService service, DataBasePlayer player, SupporterInfo supportInfo, int index, MessageEventDTO event, boolean isTopSongs) {
        String songType = isTopSongs ? "topSongs" : "recentSongs";
        if (player == null) {
            Messages.sendMessage("Player could not be found.", event);
            return;
        }
        long playerId = Long.parseLong(player.getId());
        List<PlayerScore> scores = fetchScores(service, playerId, index, isTopSongs);

        if (scores == null || scores.isEmpty()) {
            Messages.sendMessage("Scores could not be fetched. Please try again later.", event);
            return;
        }

        String messageId = String.valueOf(event.getId());
        String filePath = BotConstants.RESOURCES_PATH + songType + "_" + playerId + "_" + messageId + ".png";
        generateImage(new ArrayList<>(scores), player, supportInfo, filePath);

        File songImage = new File(filePath);
        if (songImage.exists()) {
            Messages.sendSongsImage(songImage, songType + "_" + playerId + "_" + messageId + ".png", event);
        }
    }

    private void generateImage(List<PlayerScore> scores, DataBasePlayer player, SupporterInfo supportInfo, String filePath) {
        File songImage = new File(filePath);
        if (songImage.exists()) {
            songImage.delete();
        }

        CountDownLatch latch = new CountDownLatch(1);
        SongsImageParams params = new SongsImageParams(filePath, scores, supportInfo, player);

        Platform.runLater(() -> {
            try {
                SongsImage imageGenerator = new SongsImage(params);
                imageGenerator.start(new Stage());
            } finally {
                latch.countDown();
            }
        });

        try {
            if (!latch.await(30, TimeUnit.SECONDS)) {
                System.out.println("Timeout!");
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void sendRecentSongs(LeaderboardService service, DataBasePlayer player, SupporterInfo supportInfo, int index, MessageEventDTO event) {
        sendSongs(service, player, supportInfo, index, event, false);
    }

    public void sendTopSongs(LeaderboardService service, DataBasePlayer player, SupporterInfo supportInfo, int index, MessageEventDTO event) {
        sendSongs(service, player, supportInfo, index, event, true);
    }
}
