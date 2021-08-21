package bot.commands;

import bot.api.BeatSaver;
import bot.dto.MessageEventDTO;
import bot.dto.Song;
import bot.dto.beatsaviour.BeatSaviourRankedMap;
import bot.dto.beatsaviour.RankedMaps;
import bot.main.BotConstants;
import bot.utils.Format;
import bot.utils.Messages;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Ranked {

    final RankedMaps ranked;
    final BeatSaver bs;

    public Ranked(RankedMaps ranked) {
        this.ranked = ranked;
        this.bs = new BeatSaver();
    }

    public void sendStarRangeRankedPlaylist(float min, float max, MessageEventDTO event) {
        List<BeatSaviourRankedMap> rankedMaps = new ArrayList<>(ranked.getRankedMaps());
        rankedMaps.removeIf(map -> !isInStarRange(map, min, max));
        List<Song> rankedSongs = rankedMaps.stream().map(map -> new Song(map.getHash(), map.getName())).collect(Collectors.toList());
        if (rankedSongs.size() == 0) {
            Messages.sendMessage("Could not find any maps in the given star range.", event.getChannel());
            return;
        }
        Messages.sendMessage("Found " + Format.bold(String.valueOf(rankedSongs.size())) + " maps.", event.getChannel());
        bs.sendPlaylistInChannelBySongs(rankedSongs, "Ranked Maps (" + Format.decimal(min) + " - " + Format.decimal(max) + "★)", BotConstants.playlistImageRanked, event.getChannel());
    }

    public void sendRecentRankedPlaylist(int recentAmount, MessageEventDTO event) {
        List<BeatSaviourRankedMap> rankedMaps = ranked.getRankedMaps();
        if (recentAmount > rankedMaps.size()) {
            Messages.sendMessage("The given value exceeds the maximum map count, new playlist size was set to: " + Format.bold(String.valueOf(rankedMaps.size())), event.getChannel());
        } else {
            rankedMaps = rankedMaps.subList(0, recentAmount);
        }
        List<Song> rankedSongs = rankedMaps.stream().map(map -> new Song(map.getHash(), map.getName())).collect(Collectors.toList());
        bs.sendPlaylistInChannelBySongs(rankedSongs, "Latest " + rankedMaps.size() + " Ranked Maps", BotConstants.playlistImageRanked, event.getChannel());
    }

    private boolean isInStarRange(BeatSaviourRankedMap map, float min, float max) {
        List<Float> stars = map.getStars();
        return stars.stream().anyMatch(star -> star >= min && star <= max);
    }
}
