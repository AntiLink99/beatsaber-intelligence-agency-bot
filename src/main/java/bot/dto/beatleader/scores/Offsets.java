package bot.dto.beatleader.scores;

import com.google.gson.annotations.SerializedName;

public class Offsets{

	@SerializedName("pauses")
	private int pauses;

	@SerializedName("notes")
	private int notes;

	@SerializedName("walls")
	private int walls;

	@SerializedName("frames")
	private int frames;

	@SerializedName("heights")
	private int heights;

	@SerializedName("id")
	private int id;

	public int getPauses(){
		return pauses;
	}

	public int getNotes(){
		return notes;
	}

	public int getWalls(){
		return walls;
	}

	public int getFrames(){
		return frames;
	}

	public int getHeights(){
		return heights;
	}

	public int getId(){
		return id;
	}
}