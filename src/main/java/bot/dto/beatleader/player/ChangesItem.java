package bot.dto.beatleader.player;

import com.google.gson.annotations.SerializedName;

public class ChangesItem{

	@SerializedName("newName")
	private String newName;

	@SerializedName("oldCountry")
	private String oldCountry;

	@SerializedName("oldName")
	private String oldName;

	@SerializedName("changer")
	private Object changer;

	@SerializedName("id")
	private int id;

	@SerializedName("newCountry")
	private Object newCountry;

	@SerializedName("timestamp")
	private int timestamp;

	@SerializedName("playerId")
	private String playerId;

	public String getNewName(){
		return newName;
	}

	public String getOldCountry(){
		return oldCountry;
	}

	public String getOldName(){
		return oldName;
	}

	public Object getChanger(){
		return changer;
	}

	public int getId(){
		return id;
	}

	public Object getNewCountry(){
		return newCountry;
	}

	public int getTimestamp(){
		return timestamp;
	}

	public String getPlayerId(){
		return playerId;
	}
}