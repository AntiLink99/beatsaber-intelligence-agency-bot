package bot.dto.leaderboards;

import com.google.gson.annotations.SerializedName;

public class ScoreStats{

	@SerializedName("replaysWatched")
	private int replaysWatched;

	@SerializedName("rankedPlayCount")
	private int rankedPlayCount;

	@SerializedName("totalRankedScore")
	private int totalRankedScore;

	@SerializedName("averageRankedAccuracy")
	private double averageRankedAccuracy;

	@SerializedName("totalPlayCount")
	private int totalPlayCount;

	@SerializedName("totalScore")
	private long totalScore;

	public int getReplaysWatched(){
		return replaysWatched;
	}

	public int getRankedPlayCount(){
		return rankedPlayCount;
	}

	public int getTotalRankedScore(){
		return totalRankedScore;
	}

	public double getAverageRankedAccuracy(){
		return averageRankedAccuracy;
	}

	public int getTotalPlayCount(){
		return totalPlayCount;
	}

	public long getTotalScore(){
		return totalScore;
	}
}