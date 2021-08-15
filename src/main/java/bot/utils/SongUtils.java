package bot.utils;

import bot.dto.Song;
import bot.dto.Song.Version;
import bot.dto.SongScore;
import bot.dto.beatsaviour.BeatSaviourRankedMap;
import bot.dto.beatsaviour.RankedMapRootDifficulties;
import bot.main.BotConstants;

public class SongUtils {

	public static int getNoteCountForBeatSaverMapDiff(Song map, SongScore score) {
		 String playedVersionHash = score.getSongHash().toLowerCase();
		 int difficulty = score.getDifficulty();
		 
		if (map == null || map.getVersions() == null || map.getVersions().get(0) == null) {
			return -1;
		}
		Version playedVersion = map.getVersionByHash(playedVersionHash);
		switch (difficulty) {
		case 1:
			return playedVersion.getDiffByName("Easy").getNotes();
		case 3:
			return playedVersion.getDiffByName("Normal").getNotes();
		case 5:
			return playedVersion.getDiffByName("Hard").getNotes();
		case 7:
			return playedVersion.getDiffByName("Expert").getNotes();
		case 9:
			return playedVersion.getDiffByName("ExpertPlus").getNotes();
		default:
			return -1;
		}
	}
	
	public static float getStarRatingForMapDiff(BeatSaviourRankedMap rankedMap, int difficulty) {
		if (rankedMap == null || rankedMap.getDiffs() == null || rankedMap.getDiffs().getDifficultiesAsList().isEmpty()) {
			return -1;
		}
		RankedMapRootDifficulties diffs = rankedMap.getDiffs();
		switch (difficulty) {
		case 1:
			if (diffs.getEasy() != null) {
				return diffs.getEasy().getStars();
			}
			return -1;
		case 3:
			if (diffs.getNormal() != null) {
				return diffs.getNormal().getStars();
			}
			return -1;
		case 5:
			if (diffs.getHard() != null) {
				return diffs.getHard().getStars();
			}
			return -1;
		case 7:
			if (diffs.getExpert() != null) {
				return diffs.getExpert().getStars();
			}
			return -1;
		case 9:
			if (diffs.getExpertPlus() != null) {
				return diffs.getExpertPlus().getStars();
			}
			return -1;
		default:
			return -1;
		}
	}

	public static String getDiffImageUrl(int difficulty) {
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
	
	public static boolean matchScoreSaberAndBeatSaviourDiffname(String ssDiff, String saviourDiff) {
		String ssDiffName = ssDiff.toLowerCase();
		String saviourDiffName = saviourDiff.toLowerCase();
		return ssDiffName.equals(saviourDiffName) || (ssDiffName.contains("expert+") && saviourDiffName.equals("expertplus"));
	}
}
