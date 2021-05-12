package bot.commands;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import bot.api.BeatSaver;
import bot.dto.Song;
import bot.dto.beatsaviour.BeatSaviourRankedMap;
import bot.dto.beatsaviour.RankedMaps;
import bot.main.BotConstants;
import bot.utils.Format;
import bot.utils.Messages;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class Ranked {

	public static void sendStarRangeRankedPlaylist(RankedMaps ranked, float min, float max, BeatSaver bs, MessageReceivedEvent event) {
		List<BeatSaviourRankedMap> rankedMaps = new ArrayList<BeatSaviourRankedMap>(ranked.getRankedMaps());
		rankedMaps.removeIf(map -> !isInStarRange(map, min, max));
		List<Song> rankedSongs = rankedMaps.stream().map(map -> new Song(map.getHash(), map.getName())).collect(Collectors.toList());
		if (rankedSongs.size() == 0) {
			Messages.sendMessage("Could not find any maps in the given star range.", event.getChannel());
			return;
		}
		Messages.sendMessage("Found " + Format.bold(String.valueOf(rankedSongs.size())) + " maps.", event.getChannel());
		bs.sendPlaylistInChannelBySongs(rankedSongs, "Ranked Maps (" + Format.decimal(min) + " - " + Format.decimal(max) + "★)", BotConstants.playlistImageRanked, event.getChannel());
	}

	public static void sendRecentRankedPlaylist(RankedMaps ranked, int recentAmount, BeatSaver bs, MessageReceivedEvent event) {
		List<BeatSaviourRankedMap> rankedMaps = ranked.getRankedMaps();
		if (recentAmount > rankedMaps.size()) {
			Messages.sendMessage("The given value exceeds the maximum map count, new playlist size was set to: " + Format.bold(String.valueOf(rankedMaps.size())), event.getChannel());
		} else {
			rankedMaps = rankedMaps.subList(0, recentAmount);
		}
		List<Song> rankedSongs = rankedMaps.stream().map(map -> new Song(map.getHash(), map.getName())).collect(Collectors.toList());
		bs.sendPlaylistInChannelBySongs(rankedSongs, "Latest " + rankedMaps.size() + " Ranked Maps", BotConstants.playlistImageRanked, event.getChannel());
	}

	private static boolean isInStarRange(BeatSaviourRankedMap map, float min, float max) {
		List<Float> stars = map.getStars();
		return stars.stream().anyMatch(star -> star >= min && star <= max);
	}
}
