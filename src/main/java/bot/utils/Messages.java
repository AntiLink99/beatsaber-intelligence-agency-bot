package bot.utils;

import java.awt.Color;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.TextChannel;

public class Messages {

	private static Color embedColor = Color.CYAN;

	public static void sendMessage(String msg, MessageChannel channel) {
		EmbedBuilder builder = new EmbedBuilder();
		builder.setDescription(msg);
		builder.setColor(embedColor);
		channel.sendMessage(builder.build()).queue();
	}

	public static void sendMessage(String msg, String title, MessageChannel channel) {
		EmbedBuilder builder = new EmbedBuilder();
		builder.setDescription(msg);
		builder.setColor(embedColor);
		builder.setTitle(title);
		channel.sendMessage(builder.build()).queue();
	}

	public static void sendMessage(Map<String, String> values, MessageChannel channel) {
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
}
