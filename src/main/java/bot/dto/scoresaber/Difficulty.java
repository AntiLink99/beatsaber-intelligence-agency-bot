package bot.dto.scoresaber;

import com.google.gson.annotations.SerializedName;

public class Difficulty{

	@SerializedName("difficulty")
	private int difficulty;

	@SerializedName("leaderboardId")
	private int leaderboardId;

	@SerializedName("gameMode")
	private String gameMode;

	@SerializedName("difficultyRaw")
	private String difficultyRaw;

	public int getDifficulty(){
		return difficulty;
	}

	public int getLeaderboardId(){
		return leaderboardId;
	}

	public String getGameMode(){
		return gameMode;
	}

	public String getDifficultyRaw(){
		return difficultyRaw;
	}

	public String getDifficultyName() {
		switch (difficulty) {
			case 1:
				return "Easy";
			case 3:
				return "Normal";
			case 5:
				return "Hard";
			case 7:
				return "Expert";
			case 9:
				return "Expert+";
			default:
				return "???";
		}
	}
}