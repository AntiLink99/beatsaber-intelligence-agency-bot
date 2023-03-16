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
    //TODO: TopSongs & RecentSongs redundant code

    final DatabaseManager db;
    final RankedMaps ranked;

    public SongsCommands(DatabaseManager db, RankedMaps ranked) {
        this.db = db;
        this.ranked = ranked;
    }

    public void sendRecentSongs(DataBasePlayer player, int index, MessageEventDTO event) {
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

        String playerId = player.getId();
        String messageId = String.valueOf(event.getId());

        List<PlayerScoreSS> scores = ss.getRecentScoresByPlayerIdAndPage(Long.parseLong(player.getId()), index);
        if (scores == null || scores.isEmpty()) {
            Messages.sendMessage("Scores could not be fetched. Please try again later.", event.getChannel());
            return;
        }
        for (PlayerScoreSS score : scores) {
            Song song = bs.fetchSongByHash(score.getLeaderboard().getSongHash());
            if (song != null) {
                Song.Version version = song.getVersionByHash(score.getLeaderboard().getSongHash().toLowerCase());
                if (version == null) {
                    version = song.getLatestVersion();
                }
                score.setCoverURL(version.getCoverURL());

                if (score.getLeaderboard().getMaxScore() == 0 && SongUtils.getNoteCountForBeatSaverMapDiff(song, score) >= 13) { // Acc can't be calculated if map has < 13 notes
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
        String filePath = BotConstants.RESOURCES_PATH+"recentSongs_" + playerId + "_" + messageId + ".png";
        File recentSongsImage = new File(filePath);
        // Remove old image file if exists
        if (recentSongsImage.exists()) {
            recentSongsImage.delete();
        }

        SongsImage.setFilePath(filePath);
        SongsImage.setPlayer(player);
        SongsImage.setScores(new ArrayList<>(scores));
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
            Messages.sendImage(recentSongsImage, "recentSongs_" + playerId + "_" + messageId + ".png", event);
            recentSongsImage.delete();
        }

    }

    public void sendTopSongs(DataBasePlayer player, int index, MessageEventDTO event) {
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

        String playerId = player.getId();
        String messageId = String.valueOf(event.getId());

        List<PlayerScoreSS> scores = ss.getTopScoresByPlayerIdAndPage(Long.parseLong(player.getId()), index);
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
        String filePath = BotConstants.RESOURCES_PATH+"topSongs_" + playerId + "_" + messageId + ".png";
        File topSongsImage = new File(filePath);
        // Remove old image file if exists
        if (topSongsImage.exists()) {
            topSongsImage.delete();
        }

        SongsImage.setFilePath(filePath);
        SongsImage.setPlayer(player);
        SongsImage.setScores(new ArrayList<>(scores));
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
            Messages.sendImage(topSongsImage, "topSongsImage_" + playerId + "_" + messageId + ".png", event);
            topSongsImage.delete();
        }

    }
}
