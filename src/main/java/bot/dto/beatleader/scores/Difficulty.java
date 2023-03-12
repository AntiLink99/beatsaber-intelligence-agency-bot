package bot.dto.beatleader.scores;

import com.google.gson.annotations.SerializedName;

public class Difficulty{

	@SerializedName("modeName")
	private String modeName;

	@SerializedName("notes")
	private int notes;

	@SerializedName("bombs")
	private int bombs;

	@SerializedName("stars")
	private float stars;

	@SerializedName("type")
	private int type;

	@SerializedName("maxScore")
	private int maxScore;

	@SerializedName("qualifiedTime")
	private int qualifiedTime;

	@SerializedName("difficultyName")
	private String difficultyName;

	@SerializedName("mode")
	private int mode;

	@SerializedName("nps")
	private double nps;

	@SerializedName("duration")
	private int duration;

	@SerializedName("nominatedTime")
	private int nominatedTime;

	@SerializedName("walls")
	private int walls;

	@SerializedName("modifierValues")
	private ModifierValues modifierValues;

	@SerializedName("njs")
	private float njs;

	@SerializedName("rankedTime")
	private int rankedTime;

	@SerializedName("id")
	private int id;

	@SerializedName("value")
	private int value;

	@SerializedName("status")
	private int status;

	public String getModeName(){
		return modeName;
	}

	public int getNotes(){
		return notes;
	}

	public int getBombs(){
		return bombs;
	}

	public float getStars(){
		return stars;
	}

	public int getType(){
		return type;
	}

	public int getMaxScore(){
		return maxScore;
	}

	public int getQualifiedTime(){
		return qualifiedTime;
	}

	public String getDifficultyName(){
		return difficultyName;
	}

	public int getMode(){
		return mode;
	}

	public double getNps(){
		return nps;
	}

	public int getDuration(){
		return duration;
	}

	public int getNominatedTime(){
		return nominatedTime;
	}

	public int getWalls(){
		return walls;
	}

	public ModifierValues getModifierValues(){
		return modifierValues;
	}

	public float getNjs(){
		return njs;
	}

	public int getRankedTime(){
		return rankedTime;
	}

	public int getId(){
		return id;
	}

	public int getValue(){
		return value;
	}

	public int getStatus(){
		return status;
	}
}