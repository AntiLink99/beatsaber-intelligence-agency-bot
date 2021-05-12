package bot.commands;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import bot.dto.RandomQuotesContainer;
import bot.utils.Messages;
import net.dv8tion.jda.api.entities.Message.Attachment;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.lingala.zip4j.ZipFile;
import net.lingala.zip4j.exception.ZipException;

public class DownloadQuotes {
	public static void downloadQuotes(RandomQuotesContainer container, MessageReceivedEvent event) {
		deleteOldZip();
		List<Attachment> quotes = container.getRandomQuoteImages();
		File quotesFolder = new File("src/main/resources/quotes");
		if (!quotesFolder.exists()) {
			quotesFolder.mkdirs();
		}

		List<File> filesToAdd = new ArrayList<>();
		for (Attachment image : quotes) {
			String filePath = quotesFolder.getAbsolutePath() + "/" + quotes.indexOf(image) + "." + image.getFileExtension();
			File imageFile = new File(filePath);
			if (!imageFile.exists()) {
				try {
					imageFile.createNewFile();
				} catch (IOException e) {
					e.printStackTrace();
				}
				imageFile = image.downloadToFile(filePath).join();
			}
			filesToAdd.add(imageFile);
		}
		String path = "src/main/resources/quotes_" + System.currentTimeMillis() + ".zip";
		try {
			ZipFile zip = new ZipFile(path);
			zip.addFiles(filesToAdd);
		} catch (ZipException e) {
			e.printStackTrace();
		}
		Messages.sendTempMessage("Downloaded "+filesToAdd.size()+" quotes.", 5, event.getChannel());
		filesToAdd.forEach(File::delete);
	}

	private static void deleteOldZip() {
		File folder = new File("src/main/resources");
		File[] files = folder.listFiles();
		for (File file : files) {
			if (file.getName().contains("quotes_")) {
				file.delete();
			}
		}
	}
}
