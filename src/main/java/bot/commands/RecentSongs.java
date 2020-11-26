package bot.commands;

import java.awt.image.BufferedImage;
import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import bot.api.BeatSaver;
import bot.api.ScoreSaber;
import bot.dto.Player;
import bot.dto.Song;
import bot.dto.SongScore;
import bot.graphics.SongsFrame;
import bot.utils.GraphicsUtils;
import bot.utils.JsonUtils;
import bot.utils.Messages;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class RecentSongs {

	public static void sendRecentSongsImage(Player player, ScoreSaber ss, BeatSaver bs, MessageReceivedEvent event) {
		long playerId = Long.parseLong(player.getPlayerId());
		List<SongScore> scores = ss.getRecentScoresByPlayerId(playerId);
		sendScoresImage(scores, ss, bs, event);
	}

	public static void sendTopSongsImage(Player player, ScoreSaber ss, BeatSaver bs, MessageReceivedEvent event) {
		long playerId = Long.parseLong(player.getPlayerId());
		List<SongScore> scores = ss.getTopScoresByPlayerId(playerId);
		sendScoresImage(scores, ss, bs, event);
	}

	private static void sendScoresImage(List<SongScore> scores, ScoreSaber ss, BeatSaver bs, MessageReceivedEvent event) {
		Map<String, String> coversByHash = JsonUtils.getCoverByHashList();
		HashMap<String, String> alreadyFetchedURLs = new HashMap<>();
		HashMap<String, BufferedImage> alreadyFetchedCovers = new HashMap<>();
		for (SongScore score : scores) {
			if (coversByHash != null && coversByHash.containsKey(score.getSongHash())) {
				score.setCoverURL(coversByHash.get(score.getSongHash()));
				BufferedImage coverImage = score.fetchCover();
				alreadyFetchedCovers.put(score.getSongHash(), coverImage);
			} else if (!alreadyFetchedURLs.containsKey(score.getSongHash())) {
				Song song = bs.fetchSongByHash(score.getSongHash());
				if (song != null) {
					score.setCoverURL(song.getCoverURL());
					BufferedImage coverImage = score.fetchCover();
					alreadyFetchedURLs.put(score.getSongHash(), song.getCoverURL());
					alreadyFetchedCovers.put(score.getSongHash(), coverImage);
				}
			} else {
				score.setCoverURL(alreadyFetchedURLs.get(score.getSongHash()));
				score.setFetchedCover(alreadyFetchedCovers.get(score.getSongHash()));
			}

		}
		JsonUtils.addToCoversByHash(alreadyFetchedURLs);
		SongsFrame frame = new SongsFrame(scores);
		String outputPath = "recentSongs.png";
		File outputFile = GraphicsUtils.saveFrameScreenshot(frame, outputPath);
		frame.setVisible(false);
		Messages.sendImage(outputFile, outputPath, event.getTextChannel());
		frame.dispose();
	}
}
