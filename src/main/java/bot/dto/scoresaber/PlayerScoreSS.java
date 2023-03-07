package bot.dto.scoresaber;

import bot.dto.LeaderboardService;
import bot.dto.PlayerScore;
import com.google.gson.annotations.SerializedName;
import org.ocpsoft.prettytime.PrettyTime;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Locale;

public class PlayerScoreSS implements PlayerScore {

	@SerializedName("score")
	private Score score;

	@SerializedName("leaderboard")
	private Leaderboard leaderboard;

	private final transient DecimalFormat format;

	public PlayerScoreSS() {
		format = new DecimalFormat("##0.00");
		format.setDecimalFormatSymbols(DecimalFormatSymbols.getInstance(Locale.ENGLISH));
	}

	public void setScore(Score score) {
		this.score = score;
	}

	public void setLeaderboard(Leaderboard leaderboard) {
		this.leaderboard = leaderboard;
	}

	public String getCoverURL() {
		return coverURL;
	}

	public void setCoverURL(String coverURL) {
		this.coverURL = coverURL;
	}

	public float getStars() {
		return songStars;
	}

	public void setSongStars(float songStars) {
		this.songStars = songStars;
	}

	private String coverURL; //manually set

	private float songStars; //manually set

	public Score getScore(){
		return score;
	}

	public float getWeight() {
		return score.getWeight();
	}

	public Leaderboard getLeaderboard(){
		return leaderboard;
	}

	public double getAccuracy() {
		if (score.getAccuracy() != -1) {
			return score.getAccuracy();
		}
		if (score.getModifiedScore() != 0) {
			return (double) score.getModifiedScore() / (double) leaderboard.getMaxScore();
		}
		return -1;
	}

	public String getAccuracyString() {
		double acc = getAccuracy();
		if (acc != -1 && !Double.isInfinite(acc)) {
			return format.format(acc * 100d) + "%";
		}
		return "";
	}

	@Override
	public String getSongName() {
		return leaderboard.getSongName();
	}

	@Override
	public String getSongHash() {
		return leaderboard.getSongHash();
	}

	@Override
	public String getAuthorName() {
		return leaderboard.getLevelAuthorName();
	}

	@Override
	public int getRank() {
		return score.getRank();
	}

	public String getPPString() {
		return format.format(score.getPp()) + "PP";
	}

	public String getWeightedPPString() {
		return "(" + format.format(score.getPp() * score.getWeight()) + "PP)";
	}

	@Override
	public String getDifficultyName() {
		return leaderboard.getDifficulty().getDifficultyName();
	}

	@Override
	public int getDifficultyValue() {
		return leaderboard.getDifficultyValue();
	}

	public LocalDateTime getTimeSetLocalDateTime() {
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
		return LocalDateTime.parse(score.getTimeSet(), formatter);
	}

	public String getRelativeTimeString() {
		return new PrettyTime(Locale.ENGLISH).format(Date.from(getTimeSetLocalDateTime().atZone(ZoneOffset.UTC).toInstant()));
	}

	@Override
	public boolean isRanked() {
		return score.getPp() > 0;
	}

	@Override
	public LeaderboardService getService() {
		return LeaderboardService.SCORESABER;
	}

}