package bot.api;

import bot.dto.MessageEventDTO;
import bot.dto.Playlist;
import bot.dto.PlaylistSong;
import bot.dto.Song;
import bot.listeners.PlaylistDifficultyListener;
import bot.main.BotConstants;
import bot.utils.DiscordLogger;
import bot.utils.Messages;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.TextChannel;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

public class BeatSaver {

    final HttpMethods http;
    final Gson gson;

    public BeatSaver() {
        http = new HttpMethods();
        gson = new Gson();
    }

    public void sendPlaylistInChannelByKeys(List<String> mapKeys, String playlistTitle, String playlistImageBase64, MessageChannel channel) {
        Playlist playlist;
        try {
            playlist = buildPlaylistByKeys(mapKeys, playlistImageBase64, playlistTitle);
        } catch (IllegalArgumentException e) {
            DiscordLogger.sendLogInChannel(e.getMessage(), DiscordLogger.HTTP_ERRORS);
            Messages.sendTempMessage(e.getMessage(), 10, channel);
            return;
        }
        createAndSendPlaylistFile(playlist, channel);
    }

    public void sendPlaylistInChannelBySongs(List<Song> songs, String playlistTitle, String playlistImageBase64, MessageChannel channel) {
        Playlist playlist;
        try {
            playlist = buildPlaylist(convertSongsToPlaylistSongs(songs), playlistImageBase64, playlistTitle);
        } catch (IllegalArgumentException e) {
            DiscordLogger.sendLogInChannel(e.getMessage(), DiscordLogger.HTTP_ERRORS);
            Messages.sendTempMessage(e.getMessage(), 10, channel);
            return;
        }
        createAndSendPlaylistFile(playlist, channel);
    }

    public void sendRecruitingPlaylistInChannel(List<String> mapKeys, String playlistTitle, String playlistImageBase64, MessageEventDTO event) {
        TextChannel channel = event.getChannel();

        LinkedList<Song> songs = mapKeys.stream().map(this::fetchSongByKey).collect(Collectors.toCollection(LinkedList::new));
        Playlist playlist;
        try {
            playlist = buildPlaylist(convertSongsToPlaylistSongs(songs), playlistImageBase64, playlistTitle);
        } catch (IllegalArgumentException e) {
            DiscordLogger.sendLogInChannel(e.getMessage(), DiscordLogger.HTTP_ERRORS);
            Messages.sendTempMessage(e.getMessage(), 10, channel);
            return;
        }

        askForPlaylistDifficultiesAndSendEmbed(playlist, songs, event);
    }

    private void askForPlaylistDifficultiesAndSendEmbed(Playlist playlist, List<Song> songs, MessageEventDTO event) {
        event.getJDA().addEventListener(new PlaylistDifficultyListener(playlist, songs, event, this));
    }

    private Playlist buildPlaylist(List<PlaylistSong> songs, String image, String playlistTitle) {
        if (songs == null || songs.contains(null)) {
            throw new IllegalArgumentException("At least one the given keys is invalid.");
        } else if (songs.size() == 0) {
            throw new IllegalArgumentException("Please enter at least one key after the title.");
        }

        Playlist playlist = new Playlist();
        playlist.setPlaylistAuthor(BotConstants.playlistAuthor);
        playlist.setImage(image);
        playlist.setPlaylistTitle(playlistTitle);
        playlist.setSongs(songs);
        return playlist;
    }

    private Playlist buildPlaylistByKeys(List<String> mapKeys, String image, String playlistTitle) {
        LinkedList<Song> songs = mapKeys.stream().map(this::fetchSongByKey).collect(Collectors.toCollection(LinkedList::new));
        return buildPlaylist(convertSongsToPlaylistSongs(songs), image, playlistTitle);
    }

    public void createAndSendPlaylistFile(Playlist playlist, MessageChannel channel) {
        String playlistJson = gson.toJson(playlist);
        File playlistFile = new File(BotConstants.RESOURCES_PATH + playlist.getPlaylistTitle().toLowerCase() + ".json");
        try {
            FileUtils.writeStringToFile(playlistFile, playlistJson, "UTF-8");
            if (playlistFile.exists()) {
                Messages.sendFile(playlistFile, playlistFile.getName(), channel);
            }
        } catch (IOException e) {
            DiscordLogger.sendLogInChannel(e.getMessage(), DiscordLogger.HTTP_ERRORS);
            e.printStackTrace();
        }
        playlistFile.deleteOnExit();
    }

    private Song fetchSongByKey(String key) {
        String infoUrl = ApiConstants.BS_MAP_BY_KEY + key;
        JsonObject response = http.fetchJsonObject(infoUrl);
        return getSongFromJson(response);
    }

    public Song fetchSongByHash(String hash) {
        String infoUrl = ApiConstants.BS_MAP_BY_HASH_URL + hash;
        JsonObject response = http.fetchJsonObject(infoUrl);
        return getSongFromJson(response);
    }

    private Song getSongFromJson(JsonObject json) {
        if (json == null) {
            return null;
        }
        try {
            return gson.fromJson(json, Song.class);
        } catch (NullPointerException e) {
            DiscordLogger.sendLogInChannel(e.getMessage(), DiscordLogger.HTTP_ERRORS);
            return null;
        }
    }

    private List<PlaylistSong> convertSongsToPlaylistSongs(List<Song> songs) {
        if (songs == null) {
            return null;
        }
        return songs.stream().map(song -> new PlaylistSong(song.getId(), song.getName())).collect(Collectors.toList());
    }
}
