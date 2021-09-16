package bot.dto.rankedmaps;

import com.google.gson.annotations.SerializedName;

public class Metadata{

	@SerializedName("duration")
	private int duration;

	@SerializedName("songName")
	private String songName;

	@SerializedName("levelAuthorName")
	private String levelAuthorName;

	@SerializedName("songAuthorName")
	private String songAuthorName;

	@SerializedName("songSubName")
	private String songSubName;

	@SerializedName("bpm")
	private int bpm;

	public void setDuration(int duration){
		this.duration = duration;
	}

	public int getDuration(){
		return duration;
	}

	public void setSongName(String songName){
		this.songName = songName;
	}

	public String getSongName(){
		return songName;
	}

	public void setLevelAuthorName(String levelAuthorName){
		this.levelAuthorName = levelAuthorName;
	}

	public String getLevelAuthorName(){
		return levelAuthorName;
	}

	public void setSongAuthorName(String songAuthorName){
		this.songAuthorName = songAuthorName;
	}

	public String getSongAuthorName(){
		return songAuthorName;
	}

	public void setSongSubName(String songSubName){
		this.songSubName = songSubName;
	}

	public String getSongSubName(){
		return songSubName;
	}

	public void setBpm(int bpm){
		this.bpm = bpm;
	}

	public int getBpm(){
		return bpm;
	}
}