package bot.commands;

import java.util.List;
import java.util.stream.Collectors;

import bot.api.BeatSaver;
import bot.api.ScoreSaber;
import bot.dto.ScoreSaberMapData;
import bot.dto.Song;
import bot.main.BotConstants;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class Qualified {

	public static void sendQualifiedPlaylist(MessageReceivedEvent event, ScoreSaber ss, BeatSaver bs) {
		List<ScoreSaberMapData> qualifiedMaps = ss.getQualifiedMaps();
		List<Song> qualifiedSongs = qualifiedMaps.stream().map(map -> new Song(map.getId(), map.getName())).collect(Collectors.toList());
		bs.sendPlaylistInChannelBySongs(qualifiedSongs, "Qualified Maps", BotConstants.playlistImageQualified, event.getChannel());
	}
}
