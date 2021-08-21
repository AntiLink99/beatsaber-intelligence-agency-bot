package bot.commands;

import bot.api.ApiConstants;
import bot.api.HttpMethods;
import bot.utils.Messages;
import com.google.gson.JsonObject;
import net.dv8tion.jda.api.entities.TextChannel;

public class RandomMeme {

    public void sendRandomMeme(TextChannel channel) {
        HttpMethods http = new HttpMethods();
        JsonObject json = http.fetchJsonObject(ApiConstants.MEME_URL);
        String title = json.get("title").getAsString();
        String url = json.get("url").getAsString();
        Messages.sendImageEmbed(url, title, channel);
    }
}
