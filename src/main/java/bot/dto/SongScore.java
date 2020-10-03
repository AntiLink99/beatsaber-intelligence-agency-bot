package bot.dto;

public class SongScore {
	private int rank;
	private long scoreId;
	private long score;
	private float pp;
	private float weight;
	private String songName;
	private String songSubName;
	private String songAuthorName;
	private String levelAuthorName;
	long maxScore;

	public int getRank() {
		return rank;
	}

	public void setRank(int rank) {
		this.rank = rank;
	}

	public long getScoreId() {
		return scoreId;
	}

	public void setScoreId(long scoreId) {
		this.scoreId = scoreId;
	}

	public long getScore() {
		return score;
	}

	public void setScore(long score) {
		this.score = score;
	}

	public float getPp() {
		return pp;
	}

	public void setPp(float pp) {
		this.pp = pp;
	}

	public float getWeight() {
		return weight;
	}

	public void setWeight(float weight) {
		this.weight = weight;
	}

	public String getSongName() {
		return songName;
	}

	public void setSongName(String songName) {
		this.songName = songName;
	}

	public String getSongSubName() {
		return songSubName;
	}

	public void setSongSubName(String songSubName) {
		this.songSubName = songSubName;
	}

	public String getSongAuthorName() {
		return songAuthorName;
	}

	public void setSongAuthorName(String songAuthorName) {
		this.songAuthorName = songAuthorName;
	}

	public String getLevelAuthorName() {
		return levelAuthorName;
	}

	public void setLevelAuthorName(String levelAuthorName) {
		this.levelAuthorName = levelAuthorName;
	}
}
