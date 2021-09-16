package bot.dto.rankedmaps;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class VersionsItem implements Comparable<VersionsItem> {

	@SerializedName("coverURL")
	private String coverURL;

	@SerializedName("createdAt")
	private String createdAt;

	@SerializedName("previewURL")
	private String previewURL;

	@SerializedName("downloadURL")
	private String downloadURL;

	@SerializedName("state")
	private String state;

	@SerializedName("diffs")
	private List<DiffsItem> diffs;

	@SerializedName("sageScore")
	private int sageScore;

	@SerializedName("hash")
	private String hash;

	@SerializedName("key")
	private String key;

	public void setCoverURL(String coverURL){
		this.coverURL = coverURL;
	}

	public String getCoverURL(){
		return coverURL;
	}

	public void setCreatedAt(String createdAt){
		this.createdAt = createdAt;
	}

	public String getCreatedAt(){
		return createdAt;
	}

	public void setPreviewURL(String previewURL){
		this.previewURL = previewURL;
	}

	public String getPreviewURL(){
		return previewURL;
	}

	public void setDownloadURL(String downloadURL){
		this.downloadURL = downloadURL;
	}

	public String getDownloadURL(){
		return downloadURL;
	}

	public void setState(String state){
		this.state = state;
	}

	public String getState(){
		return state;
	}

	public void setDiffs(List<DiffsItem> diffs){
		this.diffs = diffs;
	}

	public List<DiffsItem> getDiffs(){
		return diffs;
	}

	public void setSageScore(int sageScore){
		this.sageScore = sageScore;
	}

	public int getSageScore(){
		return sageScore;
	}

	public void setHash(String hash){
		this.hash = hash;
	}

	public String getHash(){
		return hash;
	}

	public void setKey(String key){
		this.key = key;
	}

	public String getKey(){
		return key;
	}

	@Override
	public int compareTo(VersionsItem o) {
		return getCreatedAt().compareTo(o.getCreatedAt());
	}
}