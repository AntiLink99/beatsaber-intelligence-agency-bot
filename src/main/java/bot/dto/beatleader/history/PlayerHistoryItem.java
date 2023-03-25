package bot.dto.beatleader.history;

import com.google.gson.annotations.SerializedName;

public class PlayerHistoryItem{

	@SerializedName("pp")
	private double pp;

	@SerializedName("aPlays")
	private int aPlays;

	@SerializedName("peakRank")
	private int peakRank;

	@SerializedName("rankedPlayCount")
	private int rankedPlayCount;

	@SerializedName("sspPlays")
	private int sspPlays;

	@SerializedName("totalRankedScore")
	private int totalRankedScore;

	@SerializedName("spPlays")
	private int spPlays;

	@SerializedName("topRankedAccuracy")
	private double topRankedAccuracy;

	@SerializedName("topBonusPP")
	private double topBonusPP;

	@SerializedName("averageUnrankedAccuracy")
	private double averageUnrankedAccuracy;

	@SerializedName("lastRankedScoreTime")
	private int lastRankedScoreTime;

	@SerializedName("averageRank")
	private double averageRank;

	@SerializedName("averageUnrankedRank")
	private double averageUnrankedRank;

	@SerializedName("averageRankedAccuracy")
	private double averageRankedAccuracy;

	@SerializedName("rank")
	private int rank;

	@SerializedName("maxStreak")
	private int maxStreak;

	@SerializedName("topAccuracy")
	private double topAccuracy;

	@SerializedName("id")
	private int id;

	@SerializedName("sPlays")
	private int sPlays;

	@SerializedName("averageAccuracy")
	private double averageAccuracy;

	@SerializedName("averageRightTiming")
	private double averageRightTiming;

	@SerializedName("dailyImprovements")
	private int dailyImprovements;

	@SerializedName("countryRank")
	private int countryRank;

	@SerializedName("timestamp")
	private int timestamp;

	@SerializedName("playerId")
	private String playerId;

	@SerializedName("averageLeftTiming")
	private double averageLeftTiming;

	@SerializedName("topUnrankedAccuracy")
	private double topUnrankedAccuracy;

	@SerializedName("topPp")
	private double topPp;

	@SerializedName("lastUnrankedScoreTime")
	private int lastUnrankedScoreTime;

	@SerializedName("topHMD")
	private int topHMD;

	@SerializedName("topPlatform")
	private String topPlatform;

	@SerializedName("averageWeightedRankedAccuracy")
	private double averageWeightedRankedAccuracy;

	@SerializedName("totalScore")
	private int totalScore;

	@SerializedName("averageRankedRank")
	private double averageRankedRank;

	@SerializedName("replaysWatched")
	private int replaysWatched;

	@SerializedName("watchedReplays")
	private int watchedReplays;

	@SerializedName("averageWeightedRankedRank")
	private double averageWeightedRankedRank;

	@SerializedName("unrankedPlayCount")
	private int unrankedPlayCount;

	@SerializedName("lastScoreTime")
	private int lastScoreTime;

	@SerializedName("totalPlayCount")
	private int totalPlayCount;

	@SerializedName("totalUnrankedScore")
	private int totalUnrankedScore;

	@SerializedName("medianAccuracy")
	private double medianAccuracy;

	@SerializedName("ssPlays")
	private int ssPlays;

	@SerializedName("medianRankedAccuracy")
	private double medianRankedAccuracy;

	public double getPp(){
		return pp;
	}

	public int getAPlays(){
		return aPlays;
	}

	public int getPeakRank(){
		return peakRank;
	}

	public int getRankedPlayCount(){
		return rankedPlayCount;
	}

	public int getSspPlays(){
		return sspPlays;
	}

	public int getTotalRankedScore(){
		return totalRankedScore;
	}

	public int getSpPlays(){
		return spPlays;
	}

	public double getTopRankedAccuracy(){
		return topRankedAccuracy;
	}

	public double getTopBonusPP(){
		return topBonusPP;
	}

	public double getAverageUnrankedAccuracy(){
		return averageUnrankedAccuracy;
	}

	public int getLastRankedScoreTime(){
		return lastRankedScoreTime;
	}

	public double getAverageRank(){
		return averageRank;
	}

	public double getAverageUnrankedRank(){
		return averageUnrankedRank;
	}

	public double getAverageRankedAccuracy(){
		return averageRankedAccuracy;
	}

	public int getRank(){
		return rank;
	}

	public int getMaxStreak(){
		return maxStreak;
	}

	public double getTopAccuracy(){
		return topAccuracy;
	}

	public int getId(){
		return id;
	}

	public int getSPlays(){
		return sPlays;
	}

	public double getAverageAccuracy(){
		return averageAccuracy;
	}

	public double getAverageRightTiming(){
		return averageRightTiming;
	}

	public int getDailyImprovements(){
		return dailyImprovements;
	}

	public int getCountryRank(){
		return countryRank;
	}

	public int getTimestamp(){
		return timestamp;
	}

	public String getPlayerId(){
		return playerId;
	}

	public double getAverageLeftTiming(){
		return averageLeftTiming;
	}

	public double getTopUnrankedAccuracy(){
		return topUnrankedAccuracy;
	}

	public double getTopPp(){
		return topPp;
	}

	public int getLastUnrankedScoreTime(){
		return lastUnrankedScoreTime;
	}

	public int getTopHMD(){
		return topHMD;
	}

	public String getTopPlatform(){
		return topPlatform;
	}

	public double getAverageWeightedRankedAccuracy(){
		return averageWeightedRankedAccuracy;
	}

	public int getTotalScore(){
		return totalScore;
	}

	public double getAverageRankedRank(){
		return averageRankedRank;
	}

	public int getReplaysWatched(){
		return replaysWatched;
	}

	public int getWatchedReplays(){
		return watchedReplays;
	}

	public double getAverageWeightedRankedRank(){
		return averageWeightedRankedRank;
	}

	public int getUnrankedPlayCount(){
		return unrankedPlayCount;
	}

	public int getLastScoreTime(){
		return lastScoreTime;
	}

	public int getTotalPlayCount(){
		return totalPlayCount;
	}

	public int getTotalUnrankedScore(){
		return totalUnrankedScore;
	}

	public double getMedianAccuracy(){
		return medianAccuracy;
	}

	public int getSsPlays(){
		return ssPlays;
	}

	public double getMedianRankedAccuracy(){
		return medianRankedAccuracy;
	}
}