package bot.dto.beatleader;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Player {

	@SerializedName("pp")
	private double pp;

	@SerializedName("country")
	private String country;

	@SerializedName("role")
	private String role;

	@SerializedName("changes")
	private Object changes;

	@SerializedName("platform")
	private String platform;

	@SerializedName("pinnedScores")
	private Object pinnedScores;

	@SerializedName("lastWeekPp")
	private double lastWeekPp;

	@SerializedName("inactive")
	private boolean inactive;

	@SerializedName("scoreStats")
	private ScoreStats scoreStats;

	@SerializedName("profileSettings")
	private ProfileSettings profileSettings;

	@SerializedName("eventsParticipating")
	private Object eventsParticipating;

	@SerializedName("patreonFeatures")
	private Object patreonFeatures;

	@SerializedName("rank")
	private int rank;

	@SerializedName("banned")
	private boolean banned;

	@SerializedName("id")
	private String id;

	@SerializedName("socials")
	private List<SocialsItem> socials;

	@SerializedName("countryRank")
	private int countryRank;

	@SerializedName("lastWeekCountryRank")
	private int lastWeekCountryRank;

	@SerializedName("mapperId")
	private int mapperId;

	@SerializedName("histories")
	private String histories;

	@SerializedName("history")
	private Object history;

	@SerializedName("avatar")
	private String avatar;

	@SerializedName("banDescription")
	private Object banDescription;

	@SerializedName("lastWeekRank")
	private int lastWeekRank;

	@SerializedName("badges")
	private Object badges;

	@SerializedName("name")
	private String name;

	@SerializedName("clans")
	private List<ClansItem> clans;

	@SerializedName("externalProfileUrl")
	private String externalProfileUrl;

	public double getPp(){
		return pp;
	}

	public String getCountry(){
		return country;
	}

	public String getRole(){
		return role;
	}

	public Object getChanges(){
		return changes;
	}

	public String getPlatform(){
		return platform;
	}

	public Object getPinnedScores(){
		return pinnedScores;
	}

	public double getLastWeekPp(){
		return lastWeekPp;
	}

	public boolean isInactive(){
		return inactive;
	}

	public ScoreStats getScoreStats(){
		return scoreStats;
	}

	public ProfileSettings getProfileSettings(){
		return profileSettings;
	}

	public Object getEventsParticipating(){
		return eventsParticipating;
	}

	public Object getPatreonFeatures(){
		return patreonFeatures;
	}

	public int getRank(){
		return rank;
	}

	public boolean isBanned(){
		return banned;
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

	public int getLastWeekCountryRank(){
		return lastWeekCountryRank;
	}

	public int getMapperId(){
		return mapperId;
	}

	public String getHistories(){
		return histories;
	}

	public Object getHistory(){
		return history;
	}

	public String getAvatar(){
		return avatar;
	}

	public Object getBanDescription(){
		return banDescription;
	}

	public int getLastWeekRank(){
		return lastWeekRank;
	}

	public Object getBadges(){
		return badges;
	}

	public String getName(){
		return name;
	}

	public List<ClansItem> getClans(){
		return clans;
	}

	public String getExternalProfileUrl(){
		return externalProfileUrl;
	}
}