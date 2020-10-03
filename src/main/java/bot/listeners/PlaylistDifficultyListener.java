package bot.listeners;

import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import bot.api.ApiConstants;
import bot.api.BeatSaver;
import bot.dto.Playlist;
import bot.dto.PlaylistSong;
import bot.dto.PlaylistSongDifficulties;
import bot.main.BotConstants;
import bot.utils.Format;
import bot.utils.Messages;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class PlaylistDifficultyListener extends ListenerAdapter {

	final long authorId;
	Playlist playlist;
	PlaylistSong currentSong;
	TextChannel channel;
	BeatSaver bs;
	Message newestMessage;
	Map<String, String> resultMessage = new LinkedHashMap<>();

	public PlaylistDifficultyListener(Playlist playlist, MessageReceivedEvent event, BeatSaver bs) {
		this.playlist = playlist;
		this.channel = event.getTextChannel();
		this.authorId = event.getAuthor().getIdLong();
		this.bs = bs;

		currentSong = playlist.getSongs().get(0);
		askForSongDifficulty(currentSong);
		new java.util.Timer().schedule(new java.util.TimerTask() {
			@Override
			public void run() {
				event.getJDA().removeEventListener(PlaylistDifficultyListener.this);
			}
		}, 300 * 1000);
	}

	private void askForSongDifficulty(PlaylistSong song) {
		HashMap<String, String> result = new HashMap<>();
		result.put("Please react with the difficulty for this song:", Format.bold(song.getSongName()));
		newestMessage = Messages.sendMessageStringMap(result, channel);
		reactWithDifficultiesOnMessage(newestMessage, song.getDifficulties());
	}

	private void reactWithDifficultiesOnMessage(Message message, PlaylistSongDifficulties difficulties) {
		if (difficulties.isEasy()) {
			message.addReaction(BotConstants.playlistDifficultyEmotes[0]).queue();
		}
		if (difficulties.isNormal()) {
			message.addReaction(BotConstants.playlistDifficultyEmotes[1]).queue();
		}
		if (difficulties.isHard()) {
			message.addReaction(BotConstants.playlistDifficultyEmotes[2]).queue();
		}
		if (difficulties.isExpert()) {
			message.addReaction(BotConstants.playlistDifficultyEmotes[3]).queue();
		}
		if (difficulties.isExpertPlus()) {
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
		if (hasNextSong(playlist, currentSong)) {
			currentSong = getNextSong(currentSong);
			askForSongDifficulty(currentSong);
		} else {
			finished();
		}
	}

	private void addSongDifficultyByEmote(PlaylistSong song, String emoji) {
		resultMessage.put(song.getSongName(), getDifficultyNameByEmote(emoji) + "\n" + ApiConstants.BS_DOWNLOAD_URL + song.getSongKey());
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

	private boolean hasNextSong(Playlist playlist, PlaylistSong currentSong) {
		return playlist.getSongs().indexOf(currentSong) < playlist.getSongs().size() - 1;
	}

	private PlaylistSong getNextSong(PlaylistSong currentSong) {
		return playlist.getSongs().get(playlist.getSongs().indexOf(currentSong) + 1);
	}

	public void finished() {
		Messages.sendMessageStringMapWithTitle(resultMessage, Format.underline(Format.bold("This week's maps:")), channel);
		bs.createAndSendPlaylistFile(playlist, channel);
		channel.getJDA().removeEventListener(PlaylistDifficultyListener.this);
	}
}
