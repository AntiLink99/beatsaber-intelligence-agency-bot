package bot.commands;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

import javax.imageio.ImageIO;

import bot.api.HttpMethods;
import bot.db.DatabaseManager;
import bot.dto.MessageEventDTO;
import bot.dto.player.Player;
import bot.utils.Messages;

public class AccGridImage {
	public static void sendAccGridImage(String urlString, DatabaseManager db, MessageEventDTO event) {
		BufferedImage image;
		try {
			image = HttpMethods.getBufferedImagefromUrl(urlString);
			if (image == null) {
				Messages.sendMessage("Was not able to fetch image. Please check the URL and source.", event.getChannel());
				return;
			}
			if (image.getWidth() > 1000 || image.getHeight() > 1000) {
				Messages.sendMessage("The image is too big. Max. size: 1000px x 1000px", event.getChannel());
				return;
			}
		} catch (MalformedURLException e) {
			Messages.sendMessage("Was not able to fetch image, because the URL is incorrect.", event.getChannel());
			return;
		} catch (IOException e) {
			Messages.sendMessage("Was not able to fetch image. Please check the URL and source.", event.getChannel());
			return;
		} catch (TimeoutException | InterruptedException | ExecutionException e) {
			Messages.sendMessage("Image could not be loaded after 4 seconds.", event.getChannel());
			return;
		}
		
		ByteArrayOutputStream tmp = new ByteArrayOutputStream();
		try {
			ImageIO.write(image, "png", tmp);
			tmp.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		Integer contentLength = tmp.size();
		long sizeKB = contentLength.longValue() / 1000l;
		if (sizeKB > 3000) {
			Messages.sendMessage("The image is too large. (" + sizeKB + "KB) Max. is 3000KB", event.getChannel());
			return;
		}

		Player storedPlayer = db.getPlayerByDiscordId(event.getAuthor().getIdLong());
		storedPlayer.setCustomAccGridImage(urlString);
		db.updatePlayer(storedPlayer);
		Messages.sendMessage("Image URL was updated successfully!", event.getChannel());
	}

	public static void resetImage(DatabaseManager db, MessageEventDTO event) {
		Player storedPlayer = db.getPlayerByDiscordId(event.getAuthor().getIdLong());
		storedPlayer.setCustomAccGridImage(null);
		db.updatePlayer(storedPlayer);
		Messages.sendMessage("Image URL was reset successfully!", event.getChannel());
	}
}
