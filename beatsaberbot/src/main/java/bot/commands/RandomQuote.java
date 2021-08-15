package bot.commands;

import java.io.File;
import java.io.IOException;
import java.util.List;

import bot.dto.MessageEventDTO;
import bot.dto.RandomQuotesContainer;
import bot.main.BotConstants;
import bot.utils.Messages;
import bot.utils.RandomUtils;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message.Attachment;

public class RandomQuote {

	public static void sendRandomQuote(RandomQuotesContainer quotesContainer, MessageEventDTO event) {
		Guild foaaGuild = event.getJDA().getGuildById(BotConstants.foaaServerId);
		if (foaaGuild == null) {
			Messages.sendMessage("Could not find guild which contains the quote channel.", event.getChannel());
			return;
		}
		try {
			List<Attachment> attachments = quotesContainer.getRandomQuoteImages();
			Attachment randomImage = RandomUtils.getRandomItem(attachments);

			File resourcesFolder = new File("src/main/resources/");
			if (!resourcesFolder.exists()) {
				resourcesFolder.mkdirs();
			}

			String filePath = resourcesFolder.getAbsolutePath() + "/" + randomImage.getFileName();
			File image = new File(filePath);
			if (!image.exists()) {
				image.createNewFile();
				image = randomImage.downloadToFile(filePath).join();
			}
			Messages.sendImage(image, image.getName(), event.getChannel());
			image.deleteOnExit();

		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
