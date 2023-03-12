package bot.dto.beatleader.scores;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Song{

	@SerializedName("author")
	private String author;

	@SerializedName("downloadUrl")
	private String downloadUrl;

	@SerializedName("description")
	private Object description;

	@SerializedName("mapperId")
	private int mapperId;

	@SerializedName("mapper")
	private String mapper;

	@SerializedName("uploadTime")
	private int uploadTime;

	@SerializedName("tags")
	private Object tags;

	@SerializedName("duration")
	private int duration;

	@SerializedName("difficulties")
	private List<DifficultiesItem> difficulties;

	@SerializedName("subName")
	private String subName;

	@SerializedName("coverImage")
	private String coverImage;

	@SerializedName("name")
	private String name;

	@SerializedName("createdTime")
	private String createdTime;

	@SerializedName("id")
	private String id;

	@SerializedName("hash")
	private String hash;

	@SerializedName("bpm")
	private float bpm;

	public String getAuthor(){
		return author;
	}

	public String getDownloadUrl(){
		return downloadUrl;
	}

	public Object getDescription(){
		return description;
	}

	public int getMapperId(){
		return mapperId;
	}

	public String getMapper(){
		return mapper;
	}

	public int getUploadTime(){
		return uploadTime;
	}

	public Object getTags(){
		return tags;
	}

	public int getDuration(){
		return duration;
	}

	public List<DifficultiesItem> getDifficulties(){
		return difficulties;
	}

	public String getSubName(){
		return subName;
	}

	public String getCoverImage(){
		return coverImage;
	}

	public String getName(){
		return name;
	}

	public String getCreatedTime(){
		return createdTime;
	}

	public String getId(){
		return id;
	}

	public String getHash(){
		return hash;
	}

	public float getBpm(){
		return bpm;
	}
}