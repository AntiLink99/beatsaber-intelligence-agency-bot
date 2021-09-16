package bot.dto.rankedmaps;

import com.google.gson.annotations.SerializedName;

public class Uploader{

	@SerializedName("name")
	private String name;

	@SerializedName("uniqueSet")
	private boolean uniqueSet;

	@SerializedName("id")
	private int id;

	@SerializedName("avatar")
	private String avatar;

	@SerializedName("type")
	private String type;

	@SerializedName("hash")
	private String hash;

	public void setName(String name){
		this.name = name;
	}

	public String getName(){
		return name;
	}

	public void setUniqueSet(boolean uniqueSet){
		this.uniqueSet = uniqueSet;
	}

	public boolean isUniqueSet(){
		return uniqueSet;
	}

	public void setId(int id){
		this.id = id;
	}

	public int getId(){
		return id;
	}

	public void setAvatar(String avatar){
		this.avatar = avatar;
	}

	public String getAvatar(){
		return avatar;
	}

	public void setType(String type){
		this.type = type;
	}

	public String getType(){
		return type;
	}

	public void setHash(String hash){
		this.hash = hash;
	}

	public String getHash(){
		return hash;
	}
}