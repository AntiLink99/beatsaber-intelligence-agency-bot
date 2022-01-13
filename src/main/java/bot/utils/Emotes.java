package bot.utils;

import bot.main.BotConstants;

public class Emotes {

    public static final String ARROW_LEFT = "⬅️";
    public static final String ARROW_RIGHT = "➡️";

    public static String getMessageWithMultipleEmotes() {
        String emoteName = BotConstants.defaultEmote;
        String message = "";
        for (int i = 0; i < BotConstants.milestoneEmoteCount; i++) {
            message = message.concat(emoteName);
        }
        return message;
    }

}
