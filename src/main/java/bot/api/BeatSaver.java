package bot.api;

import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.io.FileUtils;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import bot.dto.Playlist;
import bot.dto.PlaylistSong;
import bot.dto.PlaylistSongDifficulties;
import bot.listeners.PlaylistDifficultyListener;
import bot.main.BotConstants;
import bot.utils.Messages;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class BeatSaver {

	HttpMethods http;
	Gson gson;

	public BeatSaver() {
		http = new HttpMethods();
		gson = new Gson();
	}

//	public File downloadMap(String mapKey) {
//		String downloadUrl = ApiConstants.BS_DOWNLOAD_URL + mapKey;
//
//		try {
//			File resourcesFolder = new File("src/main/resources/");
//			if (!resourcesFolder.exists()) {
//				resourcesFolder.mkdirs();
//			}
//
//			String filePath = resourcesFolder.getAbsolutePath() + mapKey + ".zip";
//			File mapZip = http.downloadFileFromUrl(downloadUrl, filePath);
//
//			return mapZip;
//		} catch (Exception e) {
//			e.printStackTrace();
//			return null;
//		}
//	}

	public void sendPlaylistInChannel(List<String> mapKeys, String playlistTitle, MessageChannel channel) {
		Playlist playlist;
		try {
			playlist = buildPlaylist(mapKeys, playlistTitle);
		} catch (IllegalArgumentException e) {
			Messages.sendTempMessage(e.getMessage(), 10, channel);
			return;
		}
		createAndSendPlaylistFile(playlist, channel);
	}

	public void sendRecruitingPlaylistInChannel(List<String> mapKeys, String playlistTitle, MessageReceivedEvent event) {
		TextChannel channel = event.getTextChannel();
		Playlist playlist;
		try {
			playlist = buildPlaylist(mapKeys, playlistTitle);
		} catch (IllegalArgumentException e) {
			Messages.sendTempMessage(e.getMessage(), 10, channel);
			return;
		}

		askForPlaylistDifficultiesAndSendEmbed(playlist, event);
	}

	private void askForPlaylistDifficultiesAndSendEmbed(Playlist playlist, MessageReceivedEvent event) {
		event.getJDA().addEventListener(new PlaylistDifficultyListener(playlist, event, this));
	}

	private Playlist buildPlaylist(List<String> mapKeys, String playlistTitle) {
		LinkedList<PlaylistSong> songs = mapKeys.stream().map(key -> fetchPlaylistSongByKey(key)).collect(Collectors.toCollection(LinkedList::new));
		if (songs == null || songs.contains(null)) {
			throw new IllegalArgumentException("At least one the given keys is invalid.");
		} else if (songs.size() == 0) {
			throw new IllegalArgumentException("Please enter at least one key after the title.");
		}

		Playlist playlist = new Playlist();
		playlist.setPlaylistAuthor(BotConstants.playlistAuthor);
		playlist.setImage(BotConstants.playlistImage);
		playlist.setPlaylistTitle(playlistTitle);
		playlist.setSongs(songs);
		return playlist;
	}

	public void createAndSendPlaylistFile(Playlist playlist, MessageChannel channel) {
		String playlistJson = gson.toJson(playlist);
		File playlistFile = new File("src/main/resources/" + playlist.getPlaylistTitle().toLowerCase() + ".bplist");
		try {
			FileUtils.writeStringToFile(playlistFile, playlistJson, "UTF-8");
			if (playlistFile.exists()) {
				Messages.sendFile(playlistFile, playlistFile.getName(), channel);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		playlistFile.delete();
	}

	private PlaylistSong fetchPlaylistSongByKey(String key) {
		String infoUrl = ApiConstants.BS_MAP_DETAILS_URL + key;
		JsonObject response = http.fetchJson(infoUrl);
		try {
			String hash = response.get("hash").getAsString();
			String songName = response.get("metadata").getAsJsonObject().get("songName").getAsString();
			String songKey = response.get("key").getAsString();
			JsonObject diffJson = response.get("metadata").getAsJsonObject().get("difficulties").getAsJsonObject();

			PlaylistSongDifficulties difficulties = new PlaylistSongDifficulties();
			difficulties.setEasy(diffJson.get("easy").getAsBoolean());
			difficulties.setNormal(diffJson.get("normal").getAsBoolean());
			difficulties.setHard(diffJson.get("hard").getAsBoolean());
			difficulties.setExpert(diffJson.get("expert").getAsBoolean());
			difficulties.setExpertPlus(diffJson.get("expertPlus").getAsBoolean());

			return new PlaylistSong(hash, songName, songKey, difficulties);
		} catch (NullPointerException e) {
			return null;
		}
	}

//	public void sendMapInChannel(String mapKey, MessageChannel channel) {
//		File mapZip = downloadMap(mapKey);
//		Messages.sendFile(mapZip, mapZip.getName(), channel); //Too large
//		mapZip.delete();
//	}
}
