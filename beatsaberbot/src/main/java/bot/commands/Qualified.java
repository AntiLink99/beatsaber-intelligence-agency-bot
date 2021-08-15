package bot.commands;

import java.util.List;
import java.util.stream.Collectors;

import bot.api.BeatSaver;
import bot.api.ScoreSaber;
import bot.dto.MessageEventDTO;
import bot.dto.ScoreSaberMapData;
import bot.dto.Song;
import bot.main.BotConstants;

public class Qualified {

	public static void sendQualifiedPlaylist(MessageEventDTO event, ScoreSaber ss, BeatSaver bs) {
		List<ScoreSaberMapData> qualifiedMaps = ss.getQualifiedMaps();
		List<Song> qualifiedSongs = qualifiedMaps.stream().map(map -> new Song(map.getId(), map.getName())).collect(Collectors.toList());
		bs.sendPlaylistInChannelBySongs(qualifiedSongs, "Qualified Maps", BotConstants.playlistImageQualified, event.getChannel());
	}
}
