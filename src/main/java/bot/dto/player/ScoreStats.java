package bot.dto.player;

import com.google.gson.annotations.SerializedName;
import org.bson.Document;

public class ScoreStats{

	@SerializedName("replaysWatched")
	private int replaysWatched;

	@SerializedName("rankedPlayCount")
	private int rankedPlayCount;

	@SerializedName("totalRankedScore")
	private long totalRankedScore;

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

	public long getTotalRankedScore(){
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

	// Setters (make sure to add these)
	public void setReplaysWatched(int replaysWatched) {
		this.replaysWatched = replaysWatched;
	}

	public void setRankedPlayCount(int rankedPlayCount) {
		this.rankedPlayCount = rankedPlayCount;
	}

	public void setTotalRankedScore(long totalRankedScore) {
		this.totalRankedScore = totalRankedScore;
	}

	public void setAverageRankedAccuracy(double averageRankedAccuracy) {
		this.averageRankedAccuracy = averageRankedAccuracy;
	}

	public void setTotalPlayCount(int totalPlayCount) {
		this.totalPlayCount = totalPlayCount;
	}

	public void setTotalScore(long totalScore) {
		this.totalScore = totalScore;
	}

	public Document toDocument() {
		return new Document()
				.append("replaysWatched", replaysWatched)
				.append("rankedPlayCount", rankedPlayCount)
				.append("totalRankedScore", totalRankedScore)
				.append("averageRankedAccuracy", averageRankedAccuracy)
				.append("totalPlayCount", totalPlayCount)
				.append("totalScore", totalScore);
	}

	public static ScoreStats fromDocument(Document scoreStatsDocument) {
		if (scoreStatsDocument == null) return null;

		ScoreStats stats = new ScoreStats();
		stats.setReplaysWatched(scoreStatsDocument.getInteger("replaysWatched"));
		stats.setRankedPlayCount(scoreStatsDocument.getInteger("rankedPlayCount"));
		stats.setTotalRankedScore(scoreStatsDocument.getLong("totalRankedScore"));
		stats.setAverageRankedAccuracy(scoreStatsDocument.getDouble("averageRankedAccuracy"));
		stats.setTotalPlayCount(scoreStatsDocument.getInteger("totalPlayCount"));
		stats.setTotalScore(scoreStatsDocument.getLong("totalScore"));
		return stats;
	}
}