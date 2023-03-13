package bot.dto.accsaber;

import bot.dto.LeaderboardService;
import bot.dto.LeaderboardServicePlayer;
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

public class PlayerScoreACC implements PlayerScore {

	@SerializedName("songName")
	private String songName;

	@SerializedName("levelAuthorName")
	private String levelAuthorName;

	@SerializedName("complexity")
	private float complexity;

	@SerializedName("mods")
	private String mods;

	@SerializedName("categoryDisplayName")
	private String categoryDisplayName;

	@SerializedName("accuracy")
	private double accuracy;

	@SerializedName("leaderboardId")
	private String leaderboardId;

	@SerializedName("weightedAp")
	private double weightedAp;

	@SerializedName("ap")
	private double ap;

	@SerializedName("difficulty")
	private String difficulty;

	@SerializedName("score")
	private int score;

	@SerializedName("beatsaverKey")
	private String beatsaverKey;

	@SerializedName("songAuthorName")
	private String songAuthorName;

	@SerializedName("rank")
	private int rank;

	@SerializedName("timeSet")
	private String timeSet;

	@SerializedName("songHash")
	private String songHash;

	private final transient DecimalFormat format;

	public PlayerScoreACC() {
		format = new DecimalFormat("##0.00");
		format.setDecimalFormatSymbols(DecimalFormatSymbols.getInstance(Locale.ENGLISH));
	}

	public String getSongName(){
		return songName;
	}

	public String getLevelAuthorName(){
		return levelAuthorName;
	}

	public float getComplexity(){
		return complexity;
	}

	public String getMods(){
		return mods;
	}

	public String getCategoryDisplayName(){
		return categoryDisplayName;
	}

	public double getAccuracy(){
		return accuracy;
	}

	public String getLeaderboardId(){
		return leaderboardId;
	}

	public double getWeightedAp(){
		return weightedAp;
	}

	public double getAp(){
		return ap;
	}

	public String getDifficulty(){
		return difficulty;
	}

	public int getScore(){
		return score;
	}

	public String getBeatsaverKey(){
		return beatsaverKey;
	}

	public String getSongAuthorName(){
		return songAuthorName;
	}

	public int getRank(){
		return rank;
	}

	@Override
	public String getPPString() {
		return format.format(ap) + "AP";
	}

	@Override
	public String getWeightedPPString() {
		return "(" + format.format(weightedAp) + "AP)";
	}

	@Override
	public String getDifficultyName() {
		switch (difficulty.toLowerCase()) {
			case "expertplus": return "Expert+";
			case "expert": return "Expert";
			case "hard": return "Hard";
			case "normal": return "Normal";
			case "easy": return "Easy";
			default: return difficulty;
		}
	}

	@Override
	public int getDifficultyValue() {
		switch (difficulty.toLowerCase()) {
			case "expertplus": return 9;
			case "expert": return 7;
			case "hard": return 5;
			case "normal": return 3;
			case "easy": return 1;
			default: return 0;
		}
	}

	@Override
	public String getAccuracyString() {
		double acc = getAccuracy();
		if (acc != -1 && !Double.isInfinite(acc)) {
			return format.format(acc * 100d) + "%";
		}
		return "";
	}

	@Override
	public float getStars() {
		return complexity;
	}

	public LocalDateTime getTimeSetLocalDateTime() {
		return LocalDateTime.parse(timeSet.replace("Z", ""), DateTimeFormatter.ISO_LOCAL_DATE_TIME);
	}

	@Override
	public String getRelativeTimeString() {
		return new PrettyTime(Locale.ENGLISH).format(Date.from(getTimeSetLocalDateTime().atZone(ZoneOffset.UTC).toInstant()));
	}

	@Override
	public boolean isRanked() {
		return true;
	}

	@Override
	public float getWeight() {
		return (float) (weightedAp/ap);
	}

	@Override
	public String getCoverURL() {
		if (songHash == null) {
			return "https://eu.cdn.beatsaver.com/0dff062066e7fc61236cfd7c810f31cca5b1ffca.jpg";
		}
		return "https://eu.cdn.beatsaver.com/" + songHash.toLowerCase() + ".jpg";
	}

	@Override
	public LeaderboardService getService() {
		return LeaderboardService.ACCSABER;
	}

	@Override
	public LeaderboardServicePlayer getLeaderboardPlayer() {
		return null;
	}

	public String getTimeSet(){
		return timeSet;
	}

	public String getSongHash(){
		return songHash;
	}

	@Override
	public String getAuthorName() {
		return songAuthorName;
	}


}