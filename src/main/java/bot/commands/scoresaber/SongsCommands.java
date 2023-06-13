package bot.commands.scoresaber;

import bot.api.BeatSaver;
import bot.api.ScoreSaber;
import bot.db.DatabaseManager;
import bot.dto.MessageEventDTO;
import bot.dto.Song;
import bot.dto.player.DataBasePlayer;
import bot.dto.rankedmaps.BeatSaverRankedMap;
import bot.dto.rankedmaps.RankedMaps;
import bot.dto.scoresaber.PlayerScoreSS;
import bot.graphics.SongsImage;
import bot.main.BotConstants;
import bot.utils.JavaFXUtils;
import bot.utils.Messages;
import bot.utils.RankedMapUtils;
import bot.utils.SongUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class SongsCommands {
    final DatabaseManager db;
    final RankedMaps ranked;

    public SongsCommands(DatabaseManager db, RankedMaps ranked) {
        this.db = db;
        this.ranked = ranked;
    }

    private void sendSongs(DataBasePlayer player, int index, MessageEventDTO event, boolean isTopSongs) {
        ScoreSaber ss = new ScoreSaber();
        BeatSaver bs = new BeatSaver();
        String songType = isTopSongs ? "topSongs" : "recentSongs";

        if (player == null) {
            Messages.sendMessage("Player could not be found.", event.getChannel());
            return;
        }

        if (index > 50 || index < 1) {
            Messages.sendMessage("The value provided has to be an integer between 1 and 50.", event.getChannel());
            return;
        }

        String playerId = player.getId();
        String messageId = String.valueOf(event.getId());

        List<PlayerScoreSS> scores = isTopSongs ?
                ss.getTopScoresByPlayerIdAndPage(Long.parseLong(player.getId()), index) :
                ss.getRecentScoresByPlayerIdAndPage(Long.parseLong(player.getId()), index);

        if (scores == null || scores.isEmpty()) {
            Messages.sendMessage("Scores could not be fetched. Please try again later.", event.getChannel());
            return;
        }

        for (PlayerScoreSS score : scores) {
            Song song = bs.fetchSongByHash(score.getLeaderboard().getSongHash());
            if (song != null) {
                score.setCoverURL(song.getVersionByHash(score.getLeaderboard().getSongHash()).getCoverURL());

                if (score.getLeaderboard().getMaxScore() == 0) {
                    int noteCount = SongUtils.getNoteCountForBeatSaverMapDiff(song, score);
                    int maxScore = noteCount * 920 - 7245;
                    double accuracyValue = score.getScore().getModifiedScore() / (double) maxScore;
                    score.getScore().setAccuracy(accuracyValue);
                }
            }

            BeatSaverRankedMap map = RankedMapUtils.findRankedMapBySongHash(ranked, score.getLeaderboard().getSongHash());
            if (map != null) {
                float starRating = SongUtils.getStarRatingForMapDiff(map, score.getLeaderboard().getDifficultyValue());
                score.setSongStars(starRating);
            }
        }
        String filePath = BotConstants.RESOURCES_PATH + songType + "_" + playerId + "_" + messageId + ".png";

        generateImage(scores, filePath, player);

        File songImage = new File(filePath);
        if (songImage.exists()) {
            Messages.sendImage(songImage, songType + "_" + playerId + "_" + messageId + ".png", event);
            songImage.delete();
        }
    }

    private void generateImage(List<PlayerScoreSS> scores, String filePath, DataBasePlayer player) {
        // Remove old image file if exists
        File songImage = new File(filePath);
        if (songImage.exists()) {
            songImage.delete();
        }

        SongsImage.setFilePath(filePath);
        SongsImage.setPlayer(player);
        SongsImage.setScores(new ArrayList<>(scores));
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

    public void sendRecentSongs(DataBasePlayer player, int index, MessageEventDTO event) {
        sendSongs(player, index, event, false);
    }

    public void sendTopSongs(DataBasePlayer player, int index, MessageEventDTO event) {
        sendSongs(player, index, event, true);
    }
}