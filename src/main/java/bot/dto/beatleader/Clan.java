package bot.dto.beatleader;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Clan{

	@SerializedName("pp")
	private double pp;

	@SerializedName("pendingInvites")
	private List<String> pendingInvites;

	@SerializedName("playersCount")
	private int playersCount;

	@SerializedName("color")
	private String color;

	@SerializedName("averageRank")
	private double averageRank;

	@SerializedName("players")
	private List<String> players;

	@SerializedName("name")
	private String name;

	@SerializedName("icon")
	private String icon;

	@SerializedName("id")
	private int id;

	@SerializedName("tag")
	private String tag;

	@SerializedName("averageAccuracy")
	private double averageAccuracy;

	@SerializedName("leaderID")
	private String leaderID;

	public double getPp(){
		return pp;
	}

	public List<String> getPendingInvites(){
		return pendingInvites;
	}

	public int getPlayersCount(){
		return playersCount;
	}

	public String getColor(){
		return color;
	}

	public double getAverageRank(){
		return averageRank;
	}

	public List<String> getPlayers(){
		return players;
	}

	public String getName(){
		return name;
	}

	public String getIcon(){
		return icon;
	}

	public int getId(){
		return id;
	}

	public String getTag(){
		return tag;
	}

	public double getAverageAccuracy(){
		return averageAccuracy;
	}

	public String getLeaderID(){
		return leaderID;
	}
}