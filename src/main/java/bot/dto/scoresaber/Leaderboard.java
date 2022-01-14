package bot.dto.scoresaber;

import com.google.gson.annotations.SerializedName;

public class Leaderboard{

	@SerializedName("songName")
	private String songName;

	@SerializedName("levelAuthorName")
	private String levelAuthorName;

	@SerializedName("rankedDate")
	private String rankedDate;

	@SerializedName("qualified")
	private boolean qualified;

	@SerializedName("plays")
	private int plays;

	@SerializedName("dailyPlays")
	private int dailyPlays;

	@SerializedName("maxPP")
	private int maxPP;

	@SerializedName("stars")
	private double stars;

	@SerializedName("maxScore")
	private int maxScore;

	@SerializedName("difficulty")
	private Difficulty difficulty;

	@SerializedName("createdDate")
	private String createdDate;

	@SerializedName("loved")
	private boolean loved;

	@SerializedName("qualifiedDate")
	private String qualifiedDate;

	@SerializedName("songAuthorName")
	private String songAuthorName;

	@SerializedName("coverImage")
	private String coverImage;

	@SerializedName("positiveModifiers")
	private boolean positiveModifiers;

	@SerializedName("ranked")
	private boolean ranked;

	@SerializedName("id")
	private int id;

	@SerializedName("lovedDate")
	private String lovedDate;

	@SerializedName("songSubName")
	private String songSubName;

	@SerializedName("songHash")
	private String songHash;

	public String getSongName(){
		return songName;
	}

	public String getLevelAuthorName(){
		return levelAuthorName;
	}

	public String getRankedDate(){
		return rankedDate;
	}

	public boolean isQualified(){
		return qualified;
	}

	public int getPlays(){
		return plays;
	}

	public int getDailyPlays(){
		return dailyPlays;
	}

	public int getMaxPP(){
		return maxPP;
	}

	public double getStars(){
		return stars;
	}

	public int getMaxScore(){
		return maxScore;
	}

	public Difficulty getDifficulty(){
		return difficulty;
	}

	public int getDifficultyValue() {
		return difficulty.getDifficulty();
	}

	public String getCreatedDate(){
		return createdDate;
	}

	public boolean isLoved(){
		return loved;
	}

	public String getQualifiedDate(){
		return qualifiedDate;
	}

	public String getSongAuthorName(){
		return songAuthorName;
	}

	public String getCoverImage(){
		return coverImage;
	}

	public boolean isPositiveModifiers(){
		return positiveModifiers;
	}

	public boolean isRanked(){
		return ranked;
	}

	public int getId(){
		return id;
	}

	public Object getLovedDate(){
		return lovedDate;
	}

	public String getSongSubName(){
		return songSubName;
	}

	public String getSongHash(){
		return songHash;
	}
}