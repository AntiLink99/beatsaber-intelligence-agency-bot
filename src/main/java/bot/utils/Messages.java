package bot.utils;

import java.awt.Color;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.io.FileUtils;

import bot.foaa.dto.Player;
import bot.listeners.EmbedReactionListener;
import bot.main.BotConstants;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Emote;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.TextChannel;

public class Messages {

	private static Color embedColor = Color.CYAN;

	public static void sendMessage(String msg, MessageChannel channel) {
		try {
			EmbedBuilder builder = new EmbedBuilder();
			builder.setDescription(msg);
			builder.setColor(embedColor);
			channel.sendMessage(builder.build()).queue();
		} catch (Exception e) {
			System.out.println("Could not send message because of lacking permissions.");
		}
	}

	public static void sendMessageStringMap(Map<String, String> values, TextChannel channel) {
		EmbedBuilder builder = new EmbedBuilder();
		for (String key : values.keySet()) {
			builder.addField(key, values.get(key), false);
		}
		builder.setColor(embedColor);
		channel.sendMessage(builder.build()).queue();
	}

	public static void sendMessage(List<String> values, MessageChannel channel) {
		EmbedBuilder builder = new EmbedBuilder();
		for (String value : values) {
			builder.addField(value, " ", true);
		}
		builder.setColor(embedColor);
		channel.sendMessage(builder.build()).queue();
	}

	public static void sendImage(String imagePath, String title, TextChannel channel) {
		File imageFile = new File(title);
		try {
			FileUtils.copyURLToFile(new URL(imagePath), imageFile);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		channel.sendFile(imageFile, title).queue();
		imageFile.delete();
	}

	public static void sendImage(File image, String title, TextChannel channel) {
		channel.sendFile(image, title).queue();
		image.delete();
	}

	public static void sendPlainMessage(String message, TextChannel channel) {
		try {
			channel.sendMessage(message).queue();
		} catch (Exception e) {
			System.out.println("Could not send message because of lacking permissions.");
		}
	}

	public static void sendMilestoneMessage(Player player, TextChannel channel) {
		try {
			Emote emote = Emotes.getEmoteByRank(player.getRank(), channel.getGuild().getEmotes());
			String emoteLine = Emotes.getMessageWithMultipleEmotes(emote);

			sendPlainMessage(emoteLine, channel);
			sendMessage(player.getPlayerName() + "'s milestone role was updated! " + Format.bold("(Top " + String.valueOf(RoleManager.findMilestoneForRank(player.getRank())) + ")"), channel);
			sendPlainMessage(emoteLine, channel);
			sendPlainMessage(Format.ping(String.valueOf(player.getDiscordUserId())), channel);
		} catch (Exception e) {

		}
	}

	public static void sendMultiPageMessage(Map<String, String> values, String title, TextChannel channel) {
		EmbedBuilder builder = new EmbedBuilder();
		for (String key : values.keySet().stream().limit(BotConstants.entriesPerReactionPage).collect(Collectors.toCollection(LinkedHashSet::new))) {
			builder.addField(key, values.get(key), false);
		}
		builder.setColor(embedColor);
		builder.setTitle(title);
		Message reactionMessage = channel.sendMessage(builder.build()).complete();
		channel.getJDA().addEventListener(new EmbedReactionListener(reactionMessage, title, values));
	}

	public static void editMessageStringMap(long messageId, Map<String, String> values, String title, String footer, TextChannel channel) {
		EmbedBuilder builder = new EmbedBuilder();
		for (String key : values.keySet()) {
			builder.addField(key, values.get(key), false);
		}
		builder.setColor(embedColor);
		builder.setTitle(title);
		builder.setFooter(footer);
		channel.editMessageById(messageId, builder.build()).queue();
	}
}
