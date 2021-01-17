package bot.dto;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Locale;

import org.ocpsoft.prettytime.PrettyTime;

public class SongScore {
	private int rank;
	private long scoreId;
	private long score;
	private float pp;
	private float weight;
	private String timeSet;
	private long leaderboardId;
	private int maxScore;
	private int difficulty;
	private String songHash;
	private String songName;
	private String songSubName;
	private String songAuthorName;
	private String levelAuthorName;
	private String coverURL;

	private transient DecimalFormat format;

	public SongScore() {
		format = new DecimalFormat("###.##");
		format.setDecimalFormatSymbols(DecimalFormatSymbols.getInstance(Locale.ENGLISH));
	}

	public int getRank() {
		return rank;
	}

	public void setRank(int rank) {
		this.rank = rank;
	}

	public long getScoreId() {
		return scoreId;
	}

	public void setScoreId(long scoreId) {
		this.scoreId = scoreId;
	}

	public long getScore() {
		return score;
	}

	public void setScore(long score) {
		this.score = score;
	}

	public float getPp() {
		return pp;
	}

	public void setPp(float pp) {
		this.pp = pp;
	}

	public float getWeight() {
		return weight;
	}

	public String getPpString() {
		return format.format(getPp()) + "PP";
	}

	public void setWeight(float weight) {
		this.weight = weight;
	}

	public String getWeightPpString() {
		return "(" + format.format(getPp() * getWeight()) + "PP)";
	}

	public String getSongName() {
		return songName;
	}

	public void setSongName(String songName) {
		this.songName = songName;
	}

	public String getSongSubName() {
		return songSubName;
	}

	public void setSongSubName(String songSubName) {
		this.songSubName = songSubName;
	}

	public String getSongAuthorName() {
		return songAuthorName;
	}

	public void setSongAuthorName(String songAuthorName) {
		this.songAuthorName = songAuthorName;
	}

	public String getLevelAuthorName() {
		return levelAuthorName;
	}

	public void setLevelAuthorName(String levelAuthorName) {
		this.levelAuthorName = levelAuthorName;
	}

	public String getCoverURL() {
		return coverURL;
	}

	public void setCoverURL(String coverURL) {
		this.coverURL = coverURL;
	}

	public String getSongHash() {
		return songHash;
	}

	public void setSongHash(String songHash) {
		this.songHash = songHash;
	}

	public int getMaxScore() {
		return maxScore;
	}

	public void setMaxScore(int maxScore) {
		this.maxScore = maxScore;
	}

	public double getAccuracy() {
		return Double.valueOf(score) / Double.valueOf(maxScore);
	}

	public String getAccuracyString() {
		return format.format(getAccuracy() * 100d) + "%";
	}

	public int getDifficulty() {
		return difficulty;
	}

	public void setDifficulty(int difficulty) {
		this.difficulty = difficulty;
	}

	public String getDifficultyName() {
		switch (difficulty) {
		case 1:
			return "Easy";
		case 3:
			return "Normal";
		case 5:
			return "Hard";
		case 7:
			return "Expert";
		case 9:
			return "Expert+";
		default:
			return "???";
		}
	}

	public long getLeaderboardId() {
		return leaderboardId;
	}

	public void setLeaderboardId(long leaderboardId) {
		this.leaderboardId = leaderboardId;
	}

	public String getTimeSet() {
		return timeSet;
	}

	public void setTimeSet(String timeSet) {
		this.timeSet = timeSet;
	}
	
	public LocalDateTime getTimeSetLocalDateTime() {
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
		return LocalDateTime.parse(timeSet, formatter);
	}
	
	public String getRelativeTimeString() {
		return new PrettyTime(Locale.ENGLISH).format(Date.from(getTimeSetLocalDateTime().atZone(ZoneId.systemDefault()).toInstant()));
	}
}
