package bot.dto.beatleader.player;

import com.google.gson.annotations.SerializedName;

public class ProfileSettings{

	@SerializedName("saturation")
	private double saturation;

	@SerializedName("starredFriends")
	private String starredFriends;

	@SerializedName("profileAppearance")
	private String profileAppearance;

	@SerializedName("leftSaberColor")
	private String leftSaberColor;

	@SerializedName("profileCover")
	private String profileCover;

	@SerializedName("bio")
	private Object bio;

	@SerializedName("hue")
	private int hue;

	@SerializedName("id")
	private int id;

	@SerializedName("message")
	private Object message;

	@SerializedName("rightSaberColor")
	private String rightSaberColor;

	@SerializedName("effectName")
	private String effectName;

	public double getSaturation(){
		return saturation;
	}

	public String getStarredFriends(){
		return starredFriends;
	}

	public String getProfileAppearance(){
		return profileAppearance;
	}

	public String getLeftSaberColor(){
		return leftSaberColor;
	}

	public String getProfileCover(){
		return profileCover;
	}

	public Object getBio(){
		return bio;
	}

	public int getHue(){
		return hue;
	}

	public int getId(){
		return id;
	}

	public Object getMessage(){
		return message;
	}

	public String getRightSaberColor(){
		return rightSaberColor;
	}

	public String getEffectName(){
		return effectName;
	}
}