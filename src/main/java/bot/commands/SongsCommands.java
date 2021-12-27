package bot.commands;

import bot.api.BeatSaver;
import bot.api.ScoreSaber;
import bot.db.DatabaseManager;
import bot.dto.MessageEventDTO;
import bot.dto.Song;
import bot.dto.SongScore;
import bot.dto.player.Player;
import bot.dto.rankedmaps.BeatSaverRankedMap;
import bot.dto.rankedmaps.RankedMaps;
import bot.graphics.SongsImage;
import bot.main.BotConstants;
import bot.utils.JavaFXUtils;
import bot.utils.Messages;
import bot.utils.RankedMapUtils;
import bot.utils.SongUtils;

import java.io.File;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class SongsCommands {
    //TODO: TopSongs & RecentSongs gleicher machen
    final DatabaseManager db;
    final RankedMaps ranked;

    public SongsCommands(DatabaseManager db, RankedMaps ranked) {
        this.db = db;
        this.ranked = ranked;
    }

    public void sendRecentSongs(Player player, int index, MessageEventDTO event) {
        ScoreSaber ss = new ScoreSaber();
        BeatSaver bs = new BeatSaver();
        if (player == null) {
            Messages.sendMessage("Player could not be found.", event.getChannel());
            return;
        }

        if (index > 50 || index < 1) {
            Messages.sendMessage("The value provided has to be an integer between 1 and 50.", event.getChannel());
            return;
        }

        String playerId = player.getPlayerId();
        String messageId = String.valueOf(event.getId());

        List<SongScore> scores = ss.getRecentScoresByPlayerIdAndPage(Long.parseLong(player.getPlayerId()), index);
        if (scores == null || scores.isEmpty()) {
            Messages.sendMessage("Scores could not be fetched. Please try again later.", event.getChannel());
            return;
        }
        for (SongScore score : scores) {
            Song song = bs.fetchSongByHash(score.getSongHash());
            if (song != null) {
                Song.Version version = song.getVersionByHash(score.getSongHash().toLowerCase());
                if (version == null) {
                    version = song.getLatestVersion();
                }
                score.setCoverURL(version.getCoverURL());

                if (score.getMaxScore() == 0 && SongUtils.getNoteCountForBeatSaverMapDiff(song, score) >= 13) { // Acc can't be calculated if map has < 13 notes
                    int noteCount = SongUtils.getNoteCountForBeatSaverMapDiff(song, score);
                    int maxScore = noteCount * 920 - 7245;
                    float accuracyValue = (float) score.getScore() / (float) maxScore;
                    score.setAccuracy(accuracyValue);
                }
            }

            BeatSaverRankedMap map = RankedMapUtils.findRankedMapBySongHash(ranked, score.getSongHash());
            if (map != null) {
                float starRating = SongUtils.getStarRatingForMapDiff(map, score.getDifficulty());
                score.setSongStars(starRating);
            }
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
        ScoreSaber ss = new ScoreSaber();
        BeatSaver bs = new BeatSaver();
        if (player == null) {
            Messages.sendMessage("Player could not be found.", event.getChannel());
            return;
        }

        if (index > 50 || index < 1) {
            Messages.sendMessage("The value provided has to be an integer between 1 and 50.", event.getChannel());
            return;
        }

        String playerId = player.getPlayerId();
        String messageId = String.valueOf(event.getId());

        List<SongScore> scores = ss.getTopScoresByPlayerIdAndPage(Long.parseLong(player.getPlayerId()), index);
        if (scores == null || scores.isEmpty()) {
            Messages.sendMessage("Scores could not be fetched. Please try again later.", event.getChannel());
            return;
        }
        for (SongScore score : scores) {
            Song song = bs.fetchSongByHash(score.getSongHash());
            if (song != null) {
                score.setCoverURL(song.getVersionByHash(score.getSongHash()).getCoverURL());

                if (score.getMaxScore() == 0) {
                    int noteCount = SongUtils.getNoteCountForBeatSaverMapDiff(song, score);
                    int maxScore = noteCount * 920 - 7245;
                    float accuracyValue = (float) score.getScore() / (float) maxScore;
                    score.setAccuracy(accuracyValue);
                }
            }

            BeatSaverRankedMap map = RankedMapUtils.findRankedMapBySongHash(ranked, score.getSongHash());
            if (map != null) {
                float starRating = SongUtils.getStarRatingForMapDiff(map, score.getDifficulty());
                score.setSongStars(starRating);
            }
        }
        String filePath = BotConstants.RESOURCES_PATH+"topSongs_" + playerId + "_" + messageId + ".png";
        File topSongsImage = new File(filePath);
        // Remove old image file if exists
        if (topSongsImage.exists()) {
            topSongsImage.delete();
        }

        SongsImage.setFilePath(filePath);
        SongsImage.setScores(scores);
        JavaFXUtils.launch(SongsImage.class);

        int recentSongsWaitingCounter = 0;
        SongsImage.setFinished(false); //Timing Problem Fix
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
        if (topSongsImage.exists()) {
            Messages.sendImage(topSongsImage, "topSongsImage_" + playerId + "_" + messageId + ".png", event.getChannel());
            topSongsImage.delete();
        }

    }
}
