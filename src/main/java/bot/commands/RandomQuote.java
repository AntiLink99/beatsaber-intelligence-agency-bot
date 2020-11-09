package bot.commands;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

import bot.main.BotConstants;
import bot.utils.Messages;
import bot.utils.RandomUtils;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.Message.Attachment;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class RandomQuote {

	public static void sendRandomQuote(MessageReceivedEvent event) {
		Guild foaaGuild = event.getJDA().getGuildById(BotConstants.foaaServerId);
		if (foaaGuild == null) {
			Messages.sendMessage("Could not find guild which contains the quote channel.", event.getChannel());
			return;
		}
		TextChannel quoteChannel = foaaGuild.getTextChannels().stream().filter(c -> c.getName().equals("quotes")).findFirst().orElse(null);
		try {
			List<Message> quotes = quoteChannel.getIterableHistory().takeAsync(10000).get();
			List<Attachment> attachments = quotes.stream().map(quote -> quote.getAttachments().size() > 0 ? quote.getAttachments().get(0) : null).filter(attachment -> attachment != null && attachment.isImage()).collect(Collectors.toList());
			if (attachments.size() > 0) {
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
				Messages.sendImage(image, image.getName(), event.getTextChannel());
				image.deleteOnExit();
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (ExecutionException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
