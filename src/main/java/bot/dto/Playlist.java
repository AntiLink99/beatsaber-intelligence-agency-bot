package bot.dto;

import java.util.LinkedList;

public class Playlist {

	private String playlistTitle;
	private String playlistAuthor;
	private String image;
	private LinkedList<PlaylistSong> songs;

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

	public LinkedList<PlaylistSong> getSongs() {
		return songs;
	}

	public void setSongs(LinkedList<PlaylistSong> songs) {
		this.songs = songs;
	}
}
