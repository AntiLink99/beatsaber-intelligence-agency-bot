package bot.dto.rankedmaps;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class BeatSaverRankedMaps{

	@SerializedName("docs")
	private List<BeatSaverRankedMap> docs;

	public void setRankedMaps(List<BeatSaverRankedMap> docs){
		this.docs = docs;
	}

	public List<BeatSaverRankedMap> getRankedMaps(){
		return docs;
	}
}