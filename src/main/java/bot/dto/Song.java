package bot.dto;

import bot.dto.beatsaviour.RankedMapCharacteristics;

public class Song {
	private String hash;
	private String songName;
	private transient String key;
	private transient SongDifficulties difficulties;
	private transient RankedMapCharacteristics characteristics;
	private transient String coverURL;

	public Song(String hash, String songName)  {
		this.hash = hash;
		this.songName = songName;
	}
	
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
		return key;
	}

	public void setSongKey(String songKey) {
		this.key = songKey;
	}

	public String getCoverURL() {
		return coverURL;
	}

	public void setCoverURL(String coverURL) {
		this.coverURL = coverURL;
	}

	public RankedMapCharacteristics getCharacteristics() {
		return characteristics;
	}

	public void setCharacteristics(RankedMapCharacteristics characteristics) {
		this.characteristics = characteristics;
	}
}
