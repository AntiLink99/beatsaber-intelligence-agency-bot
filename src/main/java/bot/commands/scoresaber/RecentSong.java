package bot.commands.scoresaber;

import bot.api.ApiConstants;
import bot.api.BeatSaver;
import bot.api.BeatSavior;
import bot.api.ScoreSaber;
import bot.commands.chart.AccuracyChart;
import bot.db.DatabaseManager;
import bot.dto.MessageEventDTO;
import bot.dto.RecentSongData;
import bot.dto.Song;
import bot.dto.beatsavior.BeatSaviorPlayerScore;
import bot.dto.beatsavior.BeatSaviorPlayerScores;
import bot.dto.player.DataBasePlayer;
import bot.dto.rankedmaps.BeatSaverRankedMap;
import bot.dto.rankedmaps.RankedMaps;
import bot.dto.scoresaber.Leaderboard;
import bot.dto.scoresaber.PlayerScoreSS;
import bot.dto.scoresaber.Score;
import bot.graphics.AccuracyGrid;
import bot.main.BotConstants;
import bot.utils.*;

import java.io.File;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class RecentSong {

    final DatabaseManager db;

    public RecentSong(DatabaseManager db) {
        this.db = db;
    }

    private static String getDurationStringFromMap(Song bsMap) {
        if (bsMap == null || bsMap.getMetadata() == null) {
            return "[?:??]";
        }
        double durationSeconds = bsMap.getMetadata().getDuration();
        int minutes = (int) (durationSeconds / 60);
        int seconds = (int) durationSeconds % 60;
        return "[" + minutes + ":" + (seconds < 10 ? "0" + seconds : seconds) + "]";
    }

    private static int getPageNrFromSongIndex(int index) {
        int withoutDigits = (index / 8);
        float decimal = index / 8f - withoutDigits;
        return decimal > 0 ? withoutDigits + 1 : withoutDigits;
    }

    private static String getScoreMessage(int rankOnPlayerLeaderboard) {
        if (rankOnPlayerLeaderboard == 1) {
            return "🔥 Top Play 🔥";
        }
        String suffix = Format.getSuffix(rankOnPlayerLeaderboard);
        return rankOnPlayerLeaderboard + suffix + " Best Play";
    }

    public void sendRecentSong(DataBasePlayer player, RankedMaps ranked, int index, MessageEventDTO event) {
        ScoreSaber ss = new ScoreSaber();
        BeatSaver bs = new BeatSaver();
        BeatSavior bsavior = new BeatSavior();

        if (index > 40 || index < 1) {
            Messages.sendMessage("The value provided has to be an integer between 1 and 40.", event.getChannel());
            return;
        }
        String playerId = player.getId();
        if (playerId != null) {

            // ScoreSaber
            int pageNr = getPageNrFromSongIndex(index);
            index = (index - 1) % 8;
            List<PlayerScoreSS> ssScores = ss.getRecentScoresByPlayerIdAndPage(Long.parseLong(player.getId()), pageNr);
            if (ssScores == null || ssScores.isEmpty()) {
                Messages.sendMessage("ScoreSaber didn't respond. Please try again later.", event.getChannel());
                return;
            }

            PlayerScoreSS recentData = ssScores.get(index); //TODO Nullpointer
            Leaderboard recentLeaderboard = recentData.getLeaderboard();
            Score recentScore = recentData.getScore();


            String accGridFilePath = BotConstants.RESOURCES_PATH+"accGrid_" + playerId + "_" + event.getId() + ".png";
            File gridImage = new File(accGridFilePath);

            // Saviour
            //TODO
            BeatSaviorPlayerScores saviorPlayerScores = bsavior.fetchPlayerMaps(Long.valueOf(playerId));
            System.out.println(saviorPlayerScores);
            List<BeatSaviorPlayerScore> saviourScores = saviorPlayerScores != null ? saviorPlayerScores.getPlayerMaps() : null;

            BeatSaviorPlayerScore saviourScore = null;
            boolean hasBeatSavior = saviourScores != null && !saviourScores.isEmpty();
            if (hasBeatSavior) {
                saviourScore = saviourScores.stream()
                        .filter(score -> score.getSongID().equals(recentLeaderboard.getSongHash()) &&
                        SongUtils.matchScoreSaberAndBeatSaviorDiffname(recentLeaderboard.getDifficulty().getDifficultyName(), score.getSongDifficulty()) &&
                        score.getTrackers().getWinTracker().isWon()).sorted()
                        .findFirst().orElse(null);
                if (saviourScore != null) {
                    List<Float> gridAcc = saviourScore.getTrackers().getAccuracyTracker().getGridAcc();
                    List<Integer> notesCounts = saviourScore.getTrackers().getAccuracyTracker().getGridCut();
                    AccuracyGrid.setAccuracyValues(gridAcc);
                    AccuracyGrid.setNotesCounts(notesCounts);
                    AccuracyGrid.setPlayerId(playerId);
                    AccuracyGrid.setCustomImageUrl(player.getCustomAccGridImage());
                    AccuracyGrid.setMessageId(String.valueOf(event.getId()));
                    AccuracyGrid.setFilePath(accGridFilePath);

                    // Remove old acc grid file if exists
                    if (gridImage.exists()) {
                        gridImage.delete();
                    }

                    JavaFXUtils.launch(AccuracyGrid.class);
                } else {
                    hasBeatSavior = false;
                }
            }
            // Ranked
            boolean isRanked = recentScore.getPp() > 0;
            float starRating = 0;
            String coverUrl = null;
            int rankOnPlayerLeaderboard = -1;
            if (isRanked) {
                BeatSaverRankedMap rankedMap = RankedMapUtils.findRankedMapBySongHash(ranked, recentLeaderboard.getSongHash());
                rankOnPlayerLeaderboard = Format.roundDouble((Math.log10(recentScore.getWeight()) + Math.log10(0.965)) / Math.log10(0.965));
                if (rankedMap != null) {
                    starRating = SongUtils.getStarRatingForMapDiff(rankedMap, recentLeaderboard.getDifficultyValue());
                }
            }

            // Log
            int lineWidth = 6;
            String topInfo = Format.fixedLength("\nRank: ", lineWidth) + "#" + recentScore.getRank();
            if (isRanked) {
                String playRank = Format.fixedLength("\n" + getScoreMessage(rankOnPlayerLeaderboard), lineWidth);
                topInfo += playRank;
            }
            if (hasBeatSavior && saviourScore.getTrackers().getHitTracker().getMiss() == 0) {
                topInfo += Format.fixedLength("\n✨ Full Combo! ✨", lineWidth);
            }
            topInfo += Format.fixedLength("\n" + recentScore.getRelativeTimeString(), lineWidth);

            String songInfo = Format.codeAutohotkey(topInfo);

            //BeatSaver
            Song bsMap = bs.fetchSongByHash(recentLeaderboard.getSongHash());
            int noteCount = -1;
            if (bsMap != null) {
                noteCount = SongUtils.getNoteCountForBeatSaverMapDiff(bsMap, recentData);
                Song.Version version = bsMap.getVersionByHash(recentLeaderboard.getSongHash());
                if (version != null) {
                    coverUrl = version.getCoverURL();
                } else {
                    coverUrl = BotConstants.notOnBeatSaverImageUrl;
                }
            }

            String accuracy = null;
            if (noteCount >= 13) { // Acc can't be calculated if map has < 13 notes
                if (isRanked) {
                    accuracy = Format.fixedLength("Accuracy: " + recentData.getAccuracyString(), lineWidth);
                } else {
                    int maxScore = noteCount * 920 - 7245;
                    float accuracyValue = (float) recentScore.getModifiedScore() / (float) maxScore * 100f;
                    accuracy = Format.fixedLength("Accuracy: " + Format.decimal(accuracyValue) + "%", lineWidth);
                }
                if (!hasBeatSavior) {
                    songInfo += Format.codeProlog("\n" + accuracy);
                }
            }
            if (hasBeatSavior) {
                String hitAccuracy = Format.fixedLength("Hit Accuracy: ", lineWidth) + Format.decimal(saviourScore.getTrackers().getAccuracyTracker().getAverageAcc());
                String leftHandAccuracy = Format.fixedLength("Left Hand Hit Accuracy: ", lineWidth) + Format.decimal(saviourScore.getTrackers().getAccuracyTracker().getAccLeft());
                String rightHandAccuracy = Format.fixedLength("Right Hand Hit Accuracy: ", lineWidth) + Format.decimal(saviourScore.getTrackers().getAccuracyTracker().getAccRight());
                songInfo += Format.codeProlog("\n" + (accuracy != null ? accuracy + "\n" : "") + hitAccuracy + "\n" + leftHandAccuracy + "\n" + rightHandAccuracy);

                boolean hasSwingData = saviourScore.getTrackers().getAccuracyTracker().getAveragePreswing() != 0;
                if (hasSwingData) {
                    String leftPreswing = Format.fixedLength("Preswing Left: ", lineWidth) + Format.decimal(saviourScore.getTrackers().getAccuracyTracker().getLeftPreswing() * 100) + "%";
                    String leftPostswing = Format.fixedLength("Postswing Left: ", lineWidth) + Format.decimal(saviourScore.getTrackers().getAccuracyTracker().getLeftPostswing() * 100) + "%";
                    String rightPreswing = Format.fixedLength("Preswing Right: ", lineWidth) + Format.decimal(saviourScore.getTrackers().getAccuracyTracker().getRightPreswing() * 100) + "%";
                    String rightPostswing = Format.fixedLength("Postswing Right: ", lineWidth) + Format.decimal(saviourScore.getTrackers().getAccuracyTracker().getRightPostswing() * 100) + "%";
                    String averagePreswing = Format.fixedLength("Preswing: ", lineWidth) + Format.decimal(saviourScore.getTrackers().getAccuracyTracker().getAveragePreswing() * 100) + "%";
                    String averagePostswing = Format.fixedLength("Postswing: ", lineWidth) + Format.decimal(saviourScore.getTrackers().getAccuracyTracker().getAveragePostswing() * 100) + "%";

                    songInfo += Format.codeProlog("\n\n" + averagePreswing + "\n" + averagePostswing + "\n\n" + leftPreswing + "\n" + rightPreswing + "\n\n" + leftPostswing + "\n" + rightPostswing);
                }
                String misses = Format.fixedLength("Misses: ", lineWidth) + saviourScore.getTrackers().getHitTracker().getMiss();
                String maxCombo = Format.fixedLength("Max Combo: ", lineWidth) + saviourScore.getTrackers().getHitTracker().getMaxCombo();
                String bombHits = Format.fixedLength("Bomb Hits: ", lineWidth) + saviourScore.getTrackers().getHitTracker().getBombHit();
                String pauseCount = Format.fixedLength("Pause Count: ", lineWidth) + saviourScore.getTrackers().getWinTracker().getNbOfPause();
                songInfo += Format.codeProlog("\n\n" + misses + "\n" + maxCombo + "\n" + bombHits + "\n" + pauseCount);
            }

            if (isRanked) {
                String rawPP = Format.fixedLength("Raw PP: ", lineWidth) + Format.decimal(recentScore.getPp());
                String weightPP = Format.fixedLength("Weighted PP: ", lineWidth) + Format.decimal(recentScore.getWeight() * recentScore.getPp());
                String stars = Format.fixedLength("Stars: ", lineWidth) + (starRating > 0 ? Format.decimal(starRating) : "?") + "⭐";
                songInfo += Format.codeProlog("\n" + rawPP + "\n" + weightPP + "\n" + stars);
            }
            String diffImageUrl = SongUtils.getDiffImageUrl(recentLeaderboard.getDifficultyValue());
            String songName = recentLeaderboard.getSongName() + " - " + getDurationStringFromMap(bsMap);
            String songUrl = ApiConstants.SS_LEADERBOARD_PRE_URL + recentLeaderboard.getId();
            String footerText = "Mapped by " + recentLeaderboard.getLevelAuthorName();

            RecentSongData recentSongData = new RecentSongData();
            recentSongData.setSongInfo(songInfo);
            recentSongData.setSongName(songName);
            recentSongData.setSongUrl(songUrl);
            recentSongData.setCoverUrl(coverUrl);
            recentSongData.setDiffImageUrl(diffImageUrl);
            recentSongData.setFooterText(footerText);
            recentSongData.setRanked(isRanked);

            recentSongData.setMapKey(bsMap != null ? bsMap.getId() : null);
            recentSongData.setPlayerId(playerId);
            recentSongData.setDiffName(recentLeaderboard.getDifficulty().getDifficultyName());

            Messages.sendRecentSongMessage(recentSongData, event.getChannel());
            if (hasBeatSavior) {
                AccuracyChart.sendChartImage(saviourScore, player.getName(), recentLeaderboard.getDifficulty().getDifficultyName(), event);
                int gridWaitingCounter = 0;
                while (!AccuracyGrid.isFinished()) {
                    if (gridWaitingCounter > 8) {
                        break;
                    }
                    try {
                        TimeUnit.SECONDS.sleep(1);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    gridWaitingCounter++;
                }
                if (gridImage.exists()) {
                    Messages.sendImage(gridImage, "accGrid_" + playerId + "_" + event.getId() + ".png", event);
                    gridImage.delete();
                } else {
                    DiscordLogger.sendLogInChannel("Image wasnt generated. accGrid_" + playerId + "_" + event.getId() + ".png", DiscordLogger.ERRORS);
                }
            } else {
                Messages.sendPlainMessage(Format.italic("No BeatSavior data was found for this score.\nMaybe it was set too far in the past or you don't have the mod installed."), event.getChannel());
            }
        }
    }
}
