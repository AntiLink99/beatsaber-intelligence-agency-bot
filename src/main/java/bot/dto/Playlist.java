package bot.dto;

import java.util.List;

public class Playlist {

	private String playlistTitle;
	private String playlistAuthor;
	private String image;
	private List<Song> songs;

	public String getPlaylistTitle() {
		return playlistTitle;
	}

	public void setPlaylistTitle(String playlistTitle) {
		this.playlistTitle = playlistTitle;
	}

	public String getPlaylistAuthor() {
		return playlistAuthor;
	}

	public void setPlaylistAuthor(String playlistAuthor) {
		this.playlistAuthor = playlistAuthor;
	}

	public String getImage() {
		return image;
	}

	public void setImage(String image) {
		this.image = image;
	}

	public List<Song> getSongs() {
		return songs;
	}

	public void setSongs(List<Song> songs) {
		this.songs = songs;
	}
}
