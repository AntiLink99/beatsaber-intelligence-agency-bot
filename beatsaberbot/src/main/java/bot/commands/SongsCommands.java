package bot.commands;

import java.io.File;
import java.util.List;
import java.util.concurrent.TimeUnit;

import bot.api.BeatSaver;
import bot.api.ScoreSaber;
import bot.db.DatabaseManager;
import bot.dto.MessageEventDTO;
import bot.dto.Song;
import bot.dto.SongScore;
import bot.dto.beatsaviour.BeatSaviourRankedMap;
import bot.dto.beatsaviour.RankedMaps;
import bot.dto.player.Player;
import bot.graphics.SongsImage;
import bot.utils.DiscordLogger;
import bot.utils.JavaFXUtils;
import bot.utils.Messages;
import bot.utils.RankedMapUtils;
import bot.utils.SongUtils;

public class SongsCommands {

	public static void sendRecentSongs(Player player, RankedMaps ranked, int index, DatabaseManager db, ScoreSaber ss, BeatSaver bs, MessageEventDTO event) {
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

		DiscordLogger.sendLogInChannel("Sending RecentSongs for author "+event.getAuthor().getUser().getName()+" and player "+player.getPlayerName(), DiscordLogger.RECENTSONGS_TOPSONGS);
		List<SongScore> scores = ss.getRecentScoresByPlayerIdAndPage(Long.valueOf(player.getPlayerId()), index);
		if (scores == null || scores.isEmpty()) {
			Messages.sendMessage("Scores could not be fetched. Please try again later.", event.getChannel());
			return;
		}
		for (SongScore score : scores) {
			Song song = bs.fetchSongByHash(score.getSongHash());
			if (song != null) {
				score.setCoverURL(song.getVersionByHash(score.getSongHash()).getCoverURL());

				if (score.getMaxScore() == 0 && SongUtils.getNoteCountForBeatSaverMapDiff(song, score) >= 13) { // Acc can't be calculated if map has < 13 notes
					int noteCount = SongUtils.getNoteCountForBeatSaverMapDiff(song, score);
					int maxScore = noteCount * 920 - 7245;
					float accuracyValue = Float.valueOf(score.getScore()) / Float.valueOf(maxScore);
					score.setAccuracy(accuracyValue);
				}
			}

			BeatSaviourRankedMap map = RankedMapUtils.findRankedMapBySongHash(ranked, score.getSongHash());
			if (map != null) {
				float starRating = SongUtils.getStarRatingForMapDiff(map, score.getDifficulty());
				score.setSongStars(starRating);
			}
		}
		String filePath = "src/main/resources/recentSongs_" + playerId + "_" + messageId + ".png";
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
		} else {
			DiscordLogger.sendLogInChannel("Image wasnt generated. "+recentSongsImage.getAbsolutePath(), DiscordLogger.RECENTSONGS_TOPSONGS);
		}

	}

	public static void sendTopSongs(Player player, RankedMaps ranked, int index, DatabaseManager db, ScoreSaber ss, BeatSaver bs, MessageEventDTO event) {
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

		DiscordLogger.sendLogInChannel("Sending TopSongs for author "+event.getAuthor().getUser().getName()+" and player "+player.getPlayerName(), DiscordLogger.RECENTSONGS_TOPSONGS);
		List<SongScore> scores = ss.getTopScoresByPlayerIdAndPage(Long.valueOf(player.getPlayerId()), index);
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
					float accuracyValue = Float.valueOf(score.getScore()) / Float.valueOf(maxScore);
					score.setAccuracy(accuracyValue);
				}
			}
			
			BeatSaviourRankedMap map = RankedMapUtils.findRankedMapBySongHash(ranked, score.getSongHash());
			if (map != null) {
				float starRating = SongUtils.getStarRatingForMapDiff(map, score.getDifficulty());
				score.setSongStars(starRating);
			}
		}
		String filePath = "src/main/resources/topSongs_" + playerId + "_" + messageId + ".png";
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
		} else {
			DiscordLogger.sendLogInChannel("Image wasnt generated. "+topSongsImage.getAbsolutePath(), DiscordLogger.RECENTSONGS_TOPSONGS);
		}
		
	}
}
