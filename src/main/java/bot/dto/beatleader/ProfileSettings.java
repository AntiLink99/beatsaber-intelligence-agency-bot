package bot.dto.beatleader;

import com.google.gson.annotations.SerializedName;

public class ProfileSettings{

	@SerializedName("saturation")
	private int saturation;

	@SerializedName("profileAppearance")
	private String profileAppearance;

	@SerializedName("leftSaberColor")
	private String leftSaberColor;

	@SerializedName("profileCover")
	private Object profileCover;

	@SerializedName("bio")
	private Object bio;

	@SerializedName("hue")
	private int hue;

	@SerializedName("id")
	private int id;

	@SerializedName("message")
	private Object message;

	@SerializedName("rightSaberColor")
	private Object rightSaberColor;

	@SerializedName("effectName")
	private String effectName;

	public int getSaturation(){
		return saturation;
	}

	public String getProfileAppearance(){
		return profileAppearance;
	}

	public String getLeftSaberColor(){
		return leftSaberColor;
	}

	public Object getProfileCover(){
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

	public Object getRightSaberColor(){
		return rightSaberColor;
	}

	public String getEffectName(){
		return effectName;
	}
}