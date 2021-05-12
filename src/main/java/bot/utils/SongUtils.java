package bot.utils;

import bot.dto.Song;
import bot.dto.beatsaviour.BeatSaviourRankedMap;
import bot.dto.beatsaviour.RankedMapCharacteristicsDifficulties;
import bot.dto.beatsaviour.RankedMapRootDifficulties;
import bot.main.BotConstants;

public class SongUtils {

	public static int getNoteCountForBeatSaverMapDiff(Song map, int difficulty) {
		if (map == null || map.getCharacteristics() == null || map.getCharacteristics().getDifficulties() == null) {
			return -1;
		}
		RankedMapCharacteristicsDifficulties diffs = map.getCharacteristics().getDifficulties();
		switch (difficulty) {
		case 1:
			return diffs.getEasy().getNotes();
		case 3:
			return diffs.getNormal().getNotes();
		case 5:
			return diffs.getHard().getNotes();
		case 7:
			return diffs.getExpert().getNotes();
		case 9:
			return diffs.getExpertPlus().getNotes();
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
}
