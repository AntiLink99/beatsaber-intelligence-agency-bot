package bot.dto.beatleader.scores;

import com.google.gson.annotations.SerializedName;

public class ScoreImprovement{

	@SerializedName("pp")
	private float pp;

	@SerializedName("accRight")
	private float accRight;

	@SerializedName("totalRank")
	private int totalRank;

	@SerializedName("accuracy")
	private double accuracy;

	@SerializedName("totalPp")
	private float totalPp;

	@SerializedName("accLeft")
	private float accLeft;

	@SerializedName("bonusPp")
	private int bonusPp;

	@SerializedName("wallsHit")
	private int wallsHit;

	@SerializedName("score")
	private int score;

	@SerializedName("pauses")
	private int pauses;

	@SerializedName("averageRankedAccuracy")
	private float averageRankedAccuracy;

	@SerializedName("rank")
	private int rank;

	@SerializedName("timeset")
	private String timeset;

	@SerializedName("id")
	private int id;

	@SerializedName("missedNotes")
	private int missedNotes;

	@SerializedName("badCuts")
	private int badCuts;

	@SerializedName("bombCuts")
	private int bombCuts;

	public float getPp(){
		return pp;
	}

	public float getAccRight(){
		return accRight;
	}

	public int getTotalRank(){
		return totalRank;
	}

	public double getAccuracy(){
		return accuracy;
	}

	public float getTotalPp(){
		return totalPp;
	}

	public float getAccLeft(){
		return accLeft;
	}

	public int getBonusPp(){
		return bonusPp;
	}

	public int getWallsHit(){
		return wallsHit;
	}

	public int getScore(){
		return score;
	}

	public int getPauses(){
		return pauses;
	}

	public float getAverageRankedAccuracy(){
		return averageRankedAccuracy;
	}

	public int getRank(){
		return rank;
	}

	public String getTimeset(){
		return timeset;
	}

	public int getId(){
		return id;
	}

	public int getMissedNotes(){
		return missedNotes;
	}

	public int getBadCuts(){
		return badCuts;
	}

	public int getBombCuts(){
		return bombCuts;
	}
}