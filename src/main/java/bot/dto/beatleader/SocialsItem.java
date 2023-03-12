package bot.dto.beatleader;

import com.google.gson.annotations.SerializedName;

public class SocialsItem{

	@SerializedName("service")
	private String service;

	@SerializedName("link")
	private String link;

	@SerializedName("id")
	private int id;

	@SerializedName("user")
	private String user;

	@SerializedName("userId")
	private String userId;

	public String getService(){
		return service;
	}

	public String getLink(){
		return link;
	}

	public int getId(){
		return id;
	}

	public String getUser(){
		return user;
	}

	public String getUserId(){
		return userId;
	}
}