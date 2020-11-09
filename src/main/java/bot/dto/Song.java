package bot.dto;

public class Song {
	private String hash;
	private String songName;
	private transient String songKey;
	private transient SongDifficulties difficulties;
	private transient String coverURL;

	public String getHash() {
		return hash;
	}

	public void setHash(String hash) {
		this.hash = hash;
	}

	public String getSongName() {
		return songName;
	}

	public void setSongName(String songName) {
		this.songName = songName;
	}

	public SongDifficulties getDifficulties() {
		return difficulties;
	}

	public void setDifficulties(SongDifficulties difficulties) {
		this.difficulties = difficulties;
	}

	public String getSongKey() {
		return songKey;
	}

	public void setSongKey(String songKey) {
		this.songKey = songKey;
	}

	public String getCoverURL() {
		return coverURL;
	}

	public void setCoverURL(String coverURL) {
		this.coverURL = coverURL;
	}
}
