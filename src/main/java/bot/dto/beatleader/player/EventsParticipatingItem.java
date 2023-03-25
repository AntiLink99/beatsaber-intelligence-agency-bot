package bot.dto.beatleader.player;

import com.google.gson.annotations.SerializedName;

public class EventsParticipatingItem{

	@SerializedName("pp")
	private double pp;

	@SerializedName("eventId")
	private int eventId;

	@SerializedName("country")
	private String country;

	@SerializedName("name")
	private String name;

	@SerializedName("rank")
	private int rank;

	@SerializedName("id")
	private int id;

	@SerializedName("countryRank")
	private int countryRank;

	@SerializedName("playerId")
	private String playerId;

	public double getPp(){
		return pp;
	}

	public int getEventId(){
		return eventId;
	}

	public String getCountry(){
		return country;
	}

	public String getName(){
		return name;
	}

	public int getRank(){
		return rank;
	}

	public int getId(){
		return id;
	}

	public int getCountryRank(){
		return countryRank;
	}

	public String getPlayerId(){
		return playerId;
	}
}