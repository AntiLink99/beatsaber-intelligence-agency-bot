package bot.dto.beatleader;

import com.google.gson.annotations.SerializedName;

public class ClansItem{

	@SerializedName("color")
	private String color;

	@SerializedName("id")
	private int id;

	@SerializedName("tag")
	private String tag;

	public String getColor(){
		return color;
	}

	public int getId(){
		return id;
	}

	public String getTag(){
		return tag;
	}
}