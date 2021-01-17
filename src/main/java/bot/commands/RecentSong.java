package bot.commands;

import java.io.File;
import java.util.List;
import java.util.concurrent.TimeUnit;

import bot.api.ApiConstants;
import bot.api.BeatSaver;
import bot.api.BeatSaviour;
import bot.api.ScoreSaber;
import bot.chart.AccuracyChart;
import bot.db.DatabaseManager;
import bot.dto.Song;
import bot.dto.SongScore;
import bot.dto.beatsaviour.BeatSaviourPlayerScore;
import bot.dto.beatsaviour.BeatSaviourRankedMap;
import bot.dto.beatsaviour.RankedMapRootDifficulties;
import bot.dto.beatsaviour.RankedMaps;
import bot.dto.player.Player;
import bot.graphics.AccuracyGrid;
import bot.main.BotConstants;
import bot.utils.Format;
import bot.utils.Messages;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.stage.Stage;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class RecentSong {

	private static boolean javaFxLaunched = false;

	public static void sendRecentSong(Player player, RankedMaps ranked, int index, DatabaseManager db, ScoreSaber ss, MessageReceivedEvent event) {
		BeatSaviour bsaviour = new BeatSaviour();

		if (index > 8 || index < 1) {
			Messages.sendMessage("The value provided has to be an integer between 1 and 8", event.getChannel());
			return;
		}
		String playerId = player.getPlayerId();
		if (playerId != null) {
			File gridImage = new File("src/main/resources/accGrid_" + playerId + ".png");

			// ScoreSaber
			List<SongScore> ssScores = ss.getRecentScoresByPlayerId(Long.valueOf(player.getPlayerId()));
			if (ssScores == null || ssScores.isEmpty()) {
				Messages.sendMessage("ScoreSaber didn't respond. Please try again later.", event.getTextChannel());
				return;
			}
			SongScore recentScore = ssScores.get(index - 1);

			// Saviour
			List<BeatSaviourPlayerScore> saviourScores = bsaviour.fetchPlayerMaps(Long.valueOf(playerId)).getPlayerMaps();

			BeatSaviourPlayerScore saviourScore = null;
			boolean hasBeatSaviour = saviourScores != null && !saviourScores.isEmpty();
			if (hasBeatSaviour) {
				saviourScore = saviourScores.stream().filter(score -> score.getSongID().equals(recentScore.getSongHash()) && score.getTrackers().getWinTracker().isWon()).sorted().findFirst().orElse(null);
				if (saviourScore != null) {
					List<Float> gridAcc = saviourScore.getTrackers().getAccuracyTracker().getGridAcc();
					List<Integer> notesCounts = saviourScore.getTrackers().getAccuracyTracker().getGridCut();
					AccuracyGrid.setAccuracyValues(gridAcc);
					AccuracyGrid.setNotesCounts(notesCounts);
					AccuracyGrid.setPlayerId(playerId);

					// Remove old acc grid file if exists
					if (gridImage.exists()) {
						gridImage.delete();
					}

					myLaunch(AccuracyGrid.class);
				} else {
					hasBeatSaviour = false;
				}
			}
			// Ranked
			boolean isRanked = recentScore.getPp() > 0;
			float starRating = 0;
			String coverUrl = null;
			int rankOnPlayerLeaderboard = -1;
			if (isRanked) {
				BeatSaviourRankedMap rankedMap = findRankedMapBySongHash(ranked, recentScore.getSongHash());
				rankOnPlayerLeaderboard = Format.roundDouble((Math.log10(recentScore.getWeight()) + Math.log10(0.965)) / Math.log10(0.965));
				if (rankedMap != null) {
					starRating = getStarRatingForMapDiff(rankedMap, recentScore.getDifficulty());
					coverUrl = ApiConstants.BS_PRE_URL + rankedMap.getCoverURL();
				}
			}

			// Log
			int lineWidth = 6;
			String topInfo = Format.fixedLength("\nRank: ", lineWidth) + "#" + recentScore.getRank();
			if (isRanked) {
				String playRank = Format.fixedLength("\n" + getScoreMessage(rankOnPlayerLeaderboard), lineWidth);
				topInfo += playRank;
			}
			if (hasBeatSaviour && saviourScore != null && saviourScore.getTrackers().getHitTracker().getMiss() == 0) {
				topInfo += Format.fixedLength("\n✨ Full Combo! ✨", lineWidth);
			}
			topInfo += Format.fixedLength("\n" + recentScore.getRelativeTimeString(), lineWidth);

			String songInfo = Format.codeAutohotkey(topInfo);

			String accuracy = null;
			if (isRanked) {
				accuracy = Format.fixedLength("Accuracy: " + recentScore.getAccuracyString(), lineWidth);
				if (!hasBeatSaviour) {
					songInfo += Format.codeProlog("\n" + accuracy);
				}
			}
			if (hasBeatSaviour) {
				String hitAccuracy = Format.fixedLength("Hit Accuracy: ", lineWidth) + Format.decimal(saviourScore.getTrackers().getAccuracyTracker().getAverageAcc());
				String leftHandAccuracy = Format.fixedLength("Left Hand Hit Accuracy: ", lineWidth) + Format.decimal(saviourScore.getTrackers().getAccuracyTracker().getAccLeft());
				String rightHandAccuracy = Format.fixedLength("Right Hand Hit Accuracy: ", lineWidth) + Format.decimal(saviourScore.getTrackers().getAccuracyTracker().getAccRight());
				songInfo += Format.codeProlog("\n" + (isRanked ? accuracy + "\n" : "") + hitAccuracy + "\n" + leftHandAccuracy + "\n" + rightHandAccuracy);

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
			if (coverUrl == null) {
				Song song = new BeatSaver().fetchSongByHash(recentScore.getSongHash());
				if (song != null) {
					coverUrl = song.getCoverURL();
				} else {
					coverUrl = BotConstants.notOnBeatSaverImageUrl;
				}
			}
			String diffImageUrl = getDiffImageUrl(recentScore.getDifficulty());
			String songName = recentScore.getSongName();
			String songUrl = ApiConstants.SS_LEADERBOARD_PRE_URL + recentScore.getLeaderboardId();
			String footerText = "Mapped by " + recentScore.getLevelAuthorName();

			Messages.sendMessageWithImagesAndTexts(songInfo, songName, songUrl, coverUrl, diffImageUrl, footerText, event.getTextChannel());
			if (hasBeatSaviour) {
				AccuracyChart.sendChartImage(saviourScore, player.getPlayerName(), recentScore.getDifficultyName(), event);
				int gridWaitingCounter = 0;
				long fileSizeInKB = gridImage.length() / 1024;
				while ((!gridImage.exists() && gridWaitingCounter < 8) || fileSizeInKB < 1) {
					System.out.println("Waiting for image... " + gridWaitingCounter);
					try {
						TimeUnit.SECONDS.sleep(1);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					fileSizeInKB = gridImage.length() / 1024;
					gridWaitingCounter++;
				}
				if (gridImage.exists()) {
					Messages.sendImage(gridImage, "accGrid_" + playerId + "_" + recentScore.getSongHash() + ".png", event.getTextChannel());
					gridImage.delete();
				} else {
					System.out.println("Image wasnt generated.");
				}

			} else {
				Messages.sendPlainMessage(Format.italic("It seems like you don't have the BeatSaviourData mod installed yet.\nGet it to receive more information about your plays.\nhttps://github.com/Mystogan98/BeatSaviorData/releases"), event.getTextChannel());
			}
		}
	}

	public static void myLaunch(Class<? extends Application> applicationClass) {
		if (!javaFxLaunched) { // First time
			Platform.setImplicitExit(false);
			new Thread(() -> Application.launch(applicationClass)).start();
			javaFxLaunched = true;
		} else { // Next times
			Platform.runLater(() -> {
				try {
					Application application = applicationClass.getDeclaredConstructor().newInstance();
					Stage primaryStage = new Stage();
					application.start(primaryStage);
				} catch (Exception e) {
					e.printStackTrace();
				}
			});
		}
	}

	private static float getStarRatingForMapDiff(BeatSaviourRankedMap rankedMap, int difficulty) {
		if (rankedMap == null || rankedMap.getDiffs() == null || rankedMap.getDiffs().getDifficultiesAsList().isEmpty()) {
			return -1;
		}
		RankedMapRootDifficulties diffs = rankedMap.getDiffs();
		switch (difficulty) {
		case 1:
			return diffs.getEasy().getStars();
		case 3:
			return diffs.getNormal().getStars();
		case 5:
			return diffs.getHard().getStars();
		case 7:
			return diffs.getExpert().getStars();
		case 9:
			return diffs.getExpertPlus().getStars();
		default:
			return -1;
		}
	}

	private static String getDiffImageUrl(int difficulty) {
		switch (difficulty) {
		case 1:
			return BotConstants.imageUrlDiffEasy;
		case 3:
			return BotConstants.imageUrlDiffNormal;
		case 5:
			return BotConstants.imageUrlDiffHard;
		case 7:
			return BotConstants.imageUrlDiffExpert;
		case 9:
			return BotConstants.imageUrlDiffExpertPlus;
		default:
			return "";
		}
	}

	private static BeatSaviourRankedMap findRankedMapBySongHash(RankedMaps ranked, String songHash) {
		for (BeatSaviourRankedMap map : ranked.getRankedMaps()) {
			try {
				if (map.getHash().toUpperCase().equals(songHash.toUpperCase())) {
					return map;
				}
			} catch (NullPointerException e) {
				System.out.println("Null Pointer at ranked maps");
				System.out.println("SongHash: " + songHash);
				System.out.println("Map: " + map);
				System.out.println("RankedMaps: " + ranked.getRankedMaps().size());
			}
		}
		return null;
	}

	private static String getSuffix(final int n) {
		String[] sufixes = new String[] { "th", "st", "nd", "rd", "th", "th", "th", "th", "th", "th" };
		switch (n % 100) {
		case 11:
		case 12:
		case 13:
			return "th";
		default:
			return sufixes[n % 10];
		}
	}

	private static String getScoreMessage(int rankOnPlayerLeaderboard) {
		if (rankOnPlayerLeaderboard == 1) {
			return "🔥 Top play 🔥";
		}
		String suffix = getSuffix(rankOnPlayerLeaderboard);
		return rankOnPlayerLeaderboard + suffix + " Best Play";
	}
}
