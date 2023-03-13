package bot.dto.accsaber;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class AccSaberScores{

	@SerializedName("pages")
	private int pages;

	@SerializedName("scores")
	private List<PlayerScoreACC> scores;

	@SerializedName("page")
	private int page;

	public int getPages(){
		return pages;
	}

	public List<PlayerScoreACC> getScores(){
		return scores;
	}

	public int getPage(){
		return page;
	}
}