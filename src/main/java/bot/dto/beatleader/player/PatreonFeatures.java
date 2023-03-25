package bot.dto.beatleader.player;

import com.google.gson.annotations.SerializedName;

public class PatreonFeatures{

	@SerializedName("leftSaberColor")
	private String leftSaberColor;

	@SerializedName("bio")
	private String bio;

	@SerializedName("id")
	private int id;

	@SerializedName("message")
	private String message;

	@SerializedName("rightSaberColor")
	private String rightSaberColor;

	public String getLeftSaberColor(){
		return leftSaberColor;
	}

	public String getBio(){
		return bio;
	}

	public int getId(){
		return id;
	}

	public String getMessage(){
		return message;
	}

	public String getRightSaberColor(){
		return rightSaberColor;
	}
}