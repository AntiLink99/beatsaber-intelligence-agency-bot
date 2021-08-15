package bot.listeners;

import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import bot.api.ApiConstants;
import bot.api.BeatSaver;
import bot.dto.MessageEventDTO;
import bot.dto.Playlist;
import bot.dto.Song;
import bot.dto.Song.Diff;
import bot.main.BotConstants;
import bot.utils.Format;
import bot.utils.Messages;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class PlaylistDifficultyListener extends ListenerAdapter {

	final long authorId;
	Playlist playlist;
	List<Song> playlistSongsWithMoreAttributes;
	Song currentSong;
	TextChannel channel;
	BeatSaver bs;
	Message newestMessage;
	Map<String, String> resultMessage = new LinkedHashMap<>();

	public PlaylistDifficultyListener(Playlist playlist, List<Song> playlistSongsWithMoreAttributes, MessageEventDTO event, BeatSaver bs) {
		this.playlist = playlist;
		this.playlistSongsWithMoreAttributes = playlistSongsWithMoreAttributes;
		this.channel = event.getChannel();
		this.authorId = event.getAuthor().getIdLong();
		this.bs = bs;

		currentSong = playlistSongsWithMoreAttributes.get(0);
		askForSongDifficulty(currentSong);
		new java.util.Timer().schedule(new java.util.TimerTask() {
			@Override
			public void run() {
				event.getJDA().removeEventListener(PlaylistDifficultyListener.this);
			}
		}, 300 * 1000);
	}

	private void askForSongDifficulty(Song song) {
		HashMap<String, String> result = new HashMap<>();
		result.put("Please react with the difficulty for this song:", Format.bold(song.getName()));
		newestMessage = Messages.sendMessageStringMap(result, null, channel);
		reactWithDifficultiesOnMessage(newestMessage, song.getVersions().get(0).getDiffs());
	}

	private void reactWithDifficultiesOnMessage(Message message, List<Diff> list) {
		boolean isEasy = list.stream().anyMatch(diff -> "Easy".equals(diff.getDifficulty()));
		boolean isNormal = list.stream().anyMatch(diff -> "Normal".equals(diff.getDifficulty()));
		boolean isHard = list.stream().anyMatch(diff -> "Hard".equals(diff.getDifficulty()));
		boolean isExpert = list.stream().anyMatch(diff -> "Expert".equals(diff.getDifficulty()));
		boolean isExpertPlus = list.stream().anyMatch(diff -> "ExpertPlus".equals(diff.getDifficulty()));
		if (isEasy) {
			message.addReaction(BotConstants.playlistDifficultyEmotes[0]).queue();
		}	
		if (isNormal) {
			message.addReaction(BotConstants.playlistDifficultyEmotes[1]).queue();
		}
		if (isHard) {
			message.addReaction(BotConstants.playlistDifficultyEmotes[2]).queue();
		}
		if (isExpert) {
			message.addReaction(BotConstants.playlistDifficultyEmotes[3]).queue();
		}
		if (isExpertPlus) {
			message.addReaction(BotConstants.playlistDifficultyEmotes[4]).queue();
		}
	}

	@Override
	public void onMessageReactionAdd(MessageReactionAddEvent event) {
		if (event.getUserIdLong() != authorId)
			return;
		if (event.getMember().getUser().isBot())
			return;
		if (!Arrays.asList(BotConstants.playlistDifficultyEmotes).contains(event.getReactionEmote().getEmoji()))
			return;

		newestMessage.delete().queue();
		addSongDifficultyByEmote(currentSong, event.getReactionEmote().getEmoji());
		if (hasNextSong(currentSong)) {
			currentSong = getNextSong(currentSong);
			askForSongDifficulty(currentSong);
		} else {
			finished();
		}
	}

	private void addSongDifficultyByEmote(Song song, String emoji) {
		resultMessage.put(song.getName(), getDifficultyNameByEmote(emoji) + "\n" + ApiConstants.BS_DEFAULT_MAP_URL + song.getId() + "\n" + ApiConstants.BS_PRE_URL + "/" + song.getId().toLowerCase()+".zip");
	}

	private String getDifficultyNameByEmote(String emoji) {

		if (emoji.equals(BotConstants.playlistDifficultyEmotes[0]))
			return "Easy";
		else if (emoji.equals(BotConstants.playlistDifficultyEmotes[1]))
			return "Normal";
		else if (emoji.equals(BotConstants.playlistDifficultyEmotes[2]))
			return "Hard";
		else if (emoji.equals(BotConstants.playlistDifficultyEmotes[3]))
			return "Expert";
		else if (emoji.equals(BotConstants.playlistDifficultyEmotes[4]))
			return "Expert+";
		else
			return "???";
	}

	private boolean hasNextSong(Song currentSong) {
		return playlistSongsWithMoreAttributes.indexOf(currentSong) < playlist.getSongs().size() - 1;
	}

	private Song getNextSong(Song currentSong) {
		return playlistSongsWithMoreAttributes.get(playlistSongsWithMoreAttributes.indexOf(currentSong) + 1);
	}

	public void finished() {
		Messages.sendMessageStringMapWithTitle(resultMessage, Format.underline(Format.bold("This week's maps:")), channel);
		bs.createAndSendPlaylistFile(playlist, channel);
		channel.getJDA().removeEventListener(PlaylistDifficultyListener.this);
	}
}
