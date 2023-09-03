package bot.utils;

import bot.dto.PlayerScore;
import bot.dto.Song;
import bot.dto.Song.Version;
import bot.dto.rankedmaps.BeatSaverRankedMap;
import bot.dto.rankedmaps.DiffsItem;
import bot.main.BotConstants;

public class SongUtils {

    public static int getNoteCountForBeatSaverMapDiff(Song map, PlayerScore score) {
        String playedVersionHash = score.getSongHash().toLowerCase();
        int difficulty = score.getDifficultyValue();

        if (map == null || map.getVersions() == null || map.getVersions().get(0) == null) {
            return -1;
        }
        Version playedVersion = map.getVersionByHash(playedVersionHash);
        if (playedVersion == null) {
            playedVersion = map.getLatestVersion();
        }
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

    public static float getStarRatingForMapDiff(BeatSaverRankedMap rankedMap, int difficulty) {
        if (rankedMap == null || rankedMap.getLatestVersion() == null) {
            return 0;
        }
        switch (difficulty) {
            case 1:
                return getStarsByDiffName(rankedMap, "easy");
            case 3:
                return getStarsByDiffName(rankedMap, "normal");
            case 5:
                return getStarsByDiffName(rankedMap, "hard");
            case 7:
                return getStarsByDiffName(rankedMap, "expert");
            case 9:
                return getStarsByDiffName(rankedMap, "expertplus");
            default:
                return 0;
        }
    }

    private static float getStarsByDiffName(BeatSaverRankedMap rankedMap, String diffName) {
        DiffsItem diff = rankedMap.getDiffByNameForLatestVersion(diffName);
        if (diff != null) {
            return (float) diff.getStars();
        }
        return -1;
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

    public static boolean matchScoreSaberAndBeatSaviorDiffname(String ssDiff, String saviourDiff) {
        String ssDiffName = ssDiff.toLowerCase();
        String saviourDiffName = saviourDiff.toLowerCase();
        return ssDiffName.equals(saviourDiffName) || (ssDiffName.contains("expert+") && saviourDiffName.equals("expertplus"));
    }
}
