package bot.dto.beatleader.scores;

import bot.dto.LeaderboardService;
import bot.dto.LeaderboardServicePlayer;
import bot.dto.PlayerScore;
import com.google.gson.annotations.SerializedName;
import org.ocpsoft.prettytime.PrettyTime;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Date;
import java.util.Locale;

public class PlayerScoreBL implements PlayerScore {

	@SerializedName("pp")
	private double pp;

	@SerializedName("country")
	private String country;

	@SerializedName("fullCombo")
	private boolean fullCombo;

	@SerializedName("rankVoting")
	private Object rankVoting;

	@SerializedName("metadata")
	private Object metadata;

	@SerializedName("myScore")
	private Object myScore;

	@SerializedName("accuracy")
	private double accuracy;

	@SerializedName("baseScore")
	private int baseScore;

	@SerializedName("modifiers")
	private String modifiers;

	@SerializedName("maxCombo")
	private int maxCombo;

	@SerializedName("platform")
	private String platform;

	@SerializedName("pauses")
	private int pauses;

	@SerializedName("fcAccuracy")
	private double fcAccuracy;

	@SerializedName("modifiedScore")
	private int modifiedScore;

	@SerializedName("timepost")
	private int timepost;

	@SerializedName("rank")
	private int rank;

	@SerializedName("maxStreak")
	private int maxStreak;

	@SerializedName("timeset")
	private String timeset;

	@SerializedName("id")
	private int id;

	@SerializedName("badCuts")
	private int badCuts;

	@SerializedName("countryRank")
	private int countryRank;

	@SerializedName("playerId")
	private String playerId;

	@SerializedName("player")
	private Player player;

	@SerializedName("controller")
	private int controller;

	@SerializedName("accRight")
	private double accRight;

	@SerializedName("weight")
	private float weight;

	@SerializedName("accLeft")
	private double accLeft;

	@SerializedName("replay")
	private String replay;

	@SerializedName("leaderboardId")
	private String leaderboardId;

	@SerializedName("bonusPp")
	private double bonusPp;

	@SerializedName("wallsHit")
	private int wallsHit;

	@SerializedName("replaysWatched")
	private int replaysWatched;

	@SerializedName("playCount")
	private int playCount;

	@SerializedName("leaderboard")
	private Leaderboard leaderboard;

	@SerializedName("fcPp")
	private float fcPp;

	@SerializedName("offsets")
	private Offsets offsets;

	@SerializedName("scoreImprovement")
	private ScoreImprovement scoreImprovement;

	@SerializedName("missedNotes")
	private int missedNotes;

	@SerializedName("hmd")
	private int hmd;

	@SerializedName("bombCuts")
	private int bombCuts;

	private final transient DecimalFormat format;

	public PlayerScoreBL() {
		format = new DecimalFormat("##0.00");
		format.setDecimalFormatSymbols(DecimalFormatSymbols.getInstance(Locale.ENGLISH));
	}

	public double getPp(){
		return pp;
	}

	public String getCountry(){
		return country;
	}

	public boolean isFullCombo(){
		return fullCombo;
	}

	public Object getRankVoting(){
		return rankVoting;
	}

	public Object getMetadata(){
		return metadata;
	}

	public Object getMyScore(){
		return myScore;
	}

	public double getAccuracy(){
		return accuracy;
	}

	public int getBaseScore(){
		return baseScore;
	}

	public String getModifiers(){
		return modifiers;
	}

	public int getMaxCombo(){
		return maxCombo;
	}

	public String getPlatform(){
		return platform;
	}

	public int getPauses(){
		return pauses;
	}

	public double getFcAccuracy(){
		return fcAccuracy;
	}

	public int getModifiedScore(){
		return modifiedScore;
	}

	public int getTimepost(){
		return timepost;
	}

	@Override
	public String getSongName() {
		return leaderboard.getSong().getName();
	}

	@Override
	public String getSongHash() {
		return leaderboard.getSong().getHash();
	}

	@Override
	public String getAuthorName() {
		return leaderboard.getSong().getAuthor();
	}

	public int getRank(){
		return rank;
	}

	@Override
	public String getPPString() {
		return format.format(pp) + "PP";
	}

	@Override
	public String getWeightedPPString() {
		return "(" + (format.format(pp * weight)) + "PP)";
	}

	@Override
	public String getDifficultyName() {
		return leaderboard.getDifficulty().getDifficultyName();
	}

	@Override
	public int getDifficultyValue() {
		return leaderboard.getDifficulty().getValue();
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
		return leaderboard.getDifficulty().getStars();
	}

	public LocalDateTime getTimeSetLocalDateTime() {
		return Instant.ofEpochMilli(Long.parseLong(timeset) * 1000).atZone(ZoneOffset.UTC).toLocalDateTime();
	}

	@Override
	public String getRelativeTimeString() {
		return new PrettyTime(Locale.ENGLISH).format(Date.from(getTimeSetLocalDateTime().atZone(ZoneOffset.UTC).toInstant()));
	}

	@Override
	public boolean isRanked() {
		return leaderboard.getDifficulty().getStars() > 0 && pp > 0;
	}

	public int getMaxStreak(){
		return maxStreak;
	}

	public String getTimeset(){
		return timeset;
	}

	public int getId(){
		return id;
	}

	public int getBadCuts(){
		return badCuts;
	}

	public int getCountryRank(){
		return countryRank;
	}

	public String getPlayerId(){
		return playerId;
	}

	public Player getPlayer(){
		return player;
	}

	public int getController(){
		return controller;
	}

	public double getAccRight(){
		return accRight;
	}

	public float getWeight(){
		return weight;
	}

	@Override
	public String getCoverURL() {
		return leaderboard.getSong().getCoverImage();
	}

	@Override
	public LeaderboardService getService() {
		return LeaderboardService.BEATLEADER;
	}

	@Override
	public LeaderboardServicePlayer getLeaderboardPlayer() {
		return player;
	}

	public double getAccLeft(){
		return accLeft;
	}

	public String getReplay(){
		return replay;
	}

	public String getLeaderboardId(){
		return leaderboardId;
	}

	public double getBonusPp(){
		return bonusPp;
	}

	public int getWallsHit(){
		return wallsHit;
	}

	public int getReplaysWatched(){
		return replaysWatched;
	}

	public int getPlayCount(){
		return playCount;
	}

	public Leaderboard getLeaderboard(){
		return leaderboard;
	}

	public float getFcPp(){
		return fcPp;
	}

	public Offsets getOffsets(){
		return offsets;
	}

	public ScoreImprovement getScoreImprovement(){
		return scoreImprovement;
	}

	public int getMissedNotes(){
		return missedNotes;
	}

	public int getHmd(){
		return hmd;
	}

	public int getBombCuts(){
		return bombCuts;
	}


}