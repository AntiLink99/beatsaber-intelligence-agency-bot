package bot.commands;

import com.google.gson.JsonObject;

import bot.api.ApiConstants;
import bot.api.HttpMethods;
import bot.utils.Messages;
import net.dv8tion.jda.api.entities.TextChannel;

public class RandomMeme {

	public static void sendRandomMeme(TextChannel channel) {
		HttpMethods http = new HttpMethods();
		JsonObject json = http.fetchJson(ApiConstants.MEME_URL);
		String title = json.get("title").getAsString();
		String url = json.get("url").getAsString();		
		Messages.sendImageEmbed(url, title, channel);
	}
}
