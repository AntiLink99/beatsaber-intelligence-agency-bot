package bot.dto;

public interface PlayerScore {
     String getSongName();
     String getSongHash();
     String getAuthorName();
     int getRank();
     String getPPString();
     String getWeightedPPString();
     String getDifficultyName();
     int getDifficultyValue();
     String getAccuracyString();
     float getStars();
     String getRelativeTimeString();
     boolean isRanked();
     float getWeight();
     String getCoverURL();
     LeaderboardService getService();
     LeaderboardServicePlayer getLeaderboardPlayer();
}
