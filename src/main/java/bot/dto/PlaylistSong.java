package bot.dto;

public class PlaylistSong {
	private String hash;
	private String songName;
	private transient String songKey;
	private transient PlaylistSongDifficulties difficulties;

	public PlaylistSong(String hash, String songName, String songKey, PlaylistSongDifficulties difficulties) {
		this.hash = hash;
		this.songName = songName;
		this.difficulties = difficulties;
		this.songKey = songKey;
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

	public PlaylistSongDifficulties getDifficulties() {
		return difficulties;
	}

	public void setDifficulties(PlaylistSongDifficulties difficulties) {
		this.difficulties = difficulties;
	}

	public String getSongKey() {
		return songKey;
	}

	public void setSongKey(String songKey) {
		this.songKey = songKey;
	}
}
