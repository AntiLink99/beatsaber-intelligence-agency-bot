package bot.dto.scoresaber.player;

import bot.dto.player.ScoreStats;
import com.google.gson.annotations.SerializedName;

public class ScoreSaberPlayer {

	@SerializedName("pp")
	private double pp;

	@SerializedName("country")
	private String country;

	@SerializedName("role")
	private String role;

	@SerializedName("histories")
	private String histories;

	@SerializedName("badges")
	private Object badges;

	@SerializedName("profilePicture")
	private String profilePicture;

	@SerializedName("inactive")
	private boolean inactive;

	@SerializedName("scoreStats")
	private ScoreStats scoreStats;

	@SerializedName("permissions")
	private int permissions;

	@SerializedName("name")
	private String name;

	@SerializedName("rank")
	private int rank;

	@SerializedName("id")
	private String id;

	@SerializedName("banned")
	private boolean banned;

	@SerializedName("countryRank")
	private int countryRank;

	private int customLeaderboardRank;

	public double getPp(){
		return pp;
	}

	public String getCountry(){
		return country;
	}

	public String getRole(){
		return role;
	}

	public String getHistories(){
		return histories;
	}

	public Object getBadges(){
		return badges;
	}

	public String getProfilePicture(){
		return profilePicture;
	}

	public boolean isInactive(){
		return inactive;
	}

	public ScoreStats getScoreStats(){
		return scoreStats;
	}

	public int getPermissions(){
		return permissions;
	}

	public String getName(){
		return name;
	}

	public int getRank(){
		return rank;
	}

	public String getId(){
		return id;
	}

	public long getIdLong() {
		return Long.parseLong(id);
	}

	public boolean isBanned(){
		return banned;
	}

	public int getCountryRank(){
		return countryRank;
	}

	public int getCustomLeaderboardRank() {
		return customLeaderboardRank;
	}

	public void setCustomLeaderboardRank(int customLeaderboardRank) {
		this.customLeaderboardRank = customLeaderboardRank;
	}

}