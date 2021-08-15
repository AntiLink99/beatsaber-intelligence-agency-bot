package bot.dto;

public class PlaylistSong {
	public String hash;
	public String songName;

	public PlaylistSong(String hash, String songName) {
		this.hash = hash.toLowerCase();
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
}
