package bot.dto.beatleader.scores;

import bot.api.ApiConstants;
import bot.dto.LeaderboardServicePlayer;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Player implements LeaderboardServicePlayer {

	@SerializedName("pp")
	private double pp;

	@SerializedName("country")
	private String country;

	@SerializedName("role")
	private String role;

	@SerializedName("avatar")
	private String avatar;

	@SerializedName("platform")
	private String platform;

	@SerializedName("profileSettings")
	private ProfileSettings profileSettings;

	@SerializedName("patreonFeatures")
	private PatreonFeatures patreonFeatures;

	@SerializedName("name")
	private String name;

	@SerializedName("clans")
	private List<ClansItem> clans;

	@SerializedName("rank")
	private int rank;

	@SerializedName("id")
	private String id;

	@SerializedName("socials")
	private List<SocialsItem> socials;

	@SerializedName("countryRank")
	private int countryRank;

	public double getPp(){
		return pp;
	}

	public String getCountry(){
		return country;
	}

	public String getRole(){
		return role;
	}

	public String getAvatar(){
		return avatar;
	}

	@Override
	public String getProfileURL() {
		return ApiConstants.BL_USER_PRE_URL + id;
	}

	public String getPlatform(){
		return platform;
	}

	public ProfileSettings getProfileSettings(){
		return profileSettings;
	}

	public PatreonFeatures getPatreonFeatures(){
		return patreonFeatures;
	}

	public String getName(){
		return name;
	}

	public List<ClansItem> getClans(){
		return clans;
	}

	public int getRank(){
		return rank;
	}

	public String getId(){
		return id;
	}

	public List<SocialsItem> getSocials(){
		return socials;
	}

	public int getCountryRank(){
		return countryRank;
	}
}