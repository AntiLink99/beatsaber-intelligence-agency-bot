package bot.dto.beatleader.scores;

import com.google.gson.annotations.SerializedName;

public class Leaderboard{

	@SerializedName("song")
	private Song song;

	@SerializedName("difficulty")
	private Difficulty difficulty;

	@SerializedName("qualification")
	private Object qualification;

	@SerializedName("plays")
	private int plays;

	@SerializedName("scores")
	private Object scores;

	@SerializedName("leaderboardGroup")
	private Object leaderboardGroup;

	@SerializedName("reweight")
	private Object reweight;

	@SerializedName("changes")
	private Object changes;

	@SerializedName("id")
	private String id;

	public Song getSong(){
		return song;
	}

	public Difficulty getDifficulty(){
		return difficulty;
	}

	public Object getQualification(){
		return qualification;
	}

	public int getPlays(){
		return plays;
	}

	public Object getScores(){
		return scores;
	}

	public Object getLeaderboardGroup(){
		return leaderboardGroup;
	}

	public Object getReweight(){
		return reweight;
	}

	public Object getChanges(){
		return changes;
	}

	public String getId(){
		return id;
	}
}