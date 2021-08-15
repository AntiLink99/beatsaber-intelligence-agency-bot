package bot.dto;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.Message.Attachment;
import net.dv8tion.jda.api.entities.TextChannel;

public class RandomQuotesContainer {

	private List<Attachment> randomQuoteImages;

	public void initialize(TextChannel quoteChannel) {
		try {
			List<Message> quotes = quoteChannel.getIterableHistory().takeAsync(10000).get();
			randomQuoteImages = quotes.stream().map(quote -> quote.getAttachments().size() > 0 ? quote.getAttachments().get(0) : null).filter(attachment -> attachment != null && attachment.isImage()).collect(Collectors.toList());

		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public List<Attachment> getRandomQuoteImages() {
		return randomQuoteImages;
	}

	public void setRandomQuoteImages(List<Attachment> randomQuoteImages) {
		this.randomQuoteImages = randomQuoteImages;
	}

	public void addRandomQuoteImages(Message message) {
		Attachment attachment = message.getAttachments().size() > 0 ? message.getAttachments().get(0) : null;
		if (attachment != null) {
			System.out.println("Adding new quote: " + message.getId() + "[" + getRandomQuoteImages() == null ? "0" : getRandomQuoteImages().size() + " total]");
			randomQuoteImages.add(attachment);
		}
	}
}
