package bot.dto.rankedmaps;

import com.google.gson.annotations.SerializedName;

public class Stats{

	@SerializedName("score")
	private double score;

	@SerializedName("plays")
	private int plays;

	@SerializedName("upvotes")
	private int upvotes;

	@SerializedName("downloads")
	private int downloads;

	@SerializedName("downvotes")
	private int downvotes;

	public void setScore(double score){
		this.score = score;
	}

	public double getScore(){
		return score;
	}

	public void setPlays(int plays){
		this.plays = plays;
	}

	public int getPlays(){
		return plays;
	}

	public void setUpvotes(int upvotes){
		this.upvotes = upvotes;
	}

	public int getUpvotes(){
		return upvotes;
	}

	public void setDownloads(int downloads){
		this.downloads = downloads;
	}

	public int getDownloads(){
		return downloads;
	}

	public void setDownvotes(int downvotes){
		this.downvotes = downvotes;
	}

	public int getDownvotes(){
		return downvotes;
	}
}