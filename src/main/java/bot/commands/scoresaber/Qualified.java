package bot.commands.scoresaber;

import bot.api.BeatSaver;
import bot.api.ScoreSaber;
import bot.dto.MessageEventDTO;
import bot.dto.ScoreSaberMapData;
import bot.dto.Song;
import bot.main.BotConstants;

import java.util.List;
import java.util.stream.Collectors;

public class Qualified {

    public void sendQualifiedPlaylist(MessageEventDTO event) {
        ScoreSaber ss = new ScoreSaber();
        BeatSaver bs = new BeatSaver();
        List<ScoreSaberMapData> qualifiedMaps = ss.getQualifiedMaps();
        List<Song> qualifiedSongs = qualifiedMaps.stream().map(map -> new Song(map.getId(), map.getName())).collect(Collectors.toList());
        bs.sendPlaylistInChannelBySongs(qualifiedSongs, "Qualified Maps", BotConstants.playlistImageQualified, event.getChannel());
    }
}
