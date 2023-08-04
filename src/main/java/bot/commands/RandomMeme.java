package bot.commands;

import bot.api.ApiConstants;
import bot.api.HttpMethods;
import bot.utils.Messages;
import bot.utils.RandomUtils;
import com.google.gson.JsonObject;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;

public class RandomMeme {

    public void sendRandomMeme(TextChannel channel) {
        HttpMethods http = new HttpMethods();

        String requestUrl = ApiConstants.MEME_URL + RandomUtils.getRandomItem(ApiConstants.MEME_SUBREDDITS);
        JsonObject json = http.fetchJsonObject(requestUrl);

        String title = json.get("title").getAsString();
        if (title.length() > 255) {
            title = title.substring(0, 255);
        }
        String url = json.get("url").getAsString();
        String postUrl = json.get("postLink").getAsString();
        int upvotes = json.get("ups").getAsInt();
        String subreddit = json.get("subreddit").getAsString();
        Message memeEmbed = Messages.sendMemeEmbed(url, title, postUrl, upvotes, subreddit, channel);
        memeEmbed.addReaction("⬆").queue();
        memeEmbed.addReaction("⬇").queue();
    }
}
