package bot.commands;

import bot.db.DatabaseManager;
import bot.dto.MessageEventDTO;
import bot.main.BotConstants;
import bot.utils.Messages;
import net.dv8tion.jda.api.entities.MessageChannel;

import java.util.List;

public class SetSkill {

    public static void setSkill(List<String> msgParts, DatabaseManager db, MessageEventDTO event) {
        MessageChannel channel = event.getChannel();
        if (msgParts.size() >= 3) {

            String skill = msgParts.get(2);
            int newValue;
            try {
                newValue = Integer.parseInt(msgParts.get(3));
            } catch (NumberFormatException | IndexOutOfBoundsException e) {
                Messages.sendMessage("The value provided has to be an integer from 1 to 10.", event);
                return;
            }
            if (newValue < 0 || newValue > 10) {
                Messages.sendMessage("The value for a skill has to be between 0 and 10.", event);
                return;
            }

            if ("acc".equals(skill)) {
                skill = "accuracy";
            }
            if (BotConstants.validSkills.contains(skill)) {
                int rowsChanged = db.setSkill(event.getAuthor().getIdLong(), skill, newValue);
                if (rowsChanged != 0) {
                    Messages.sendTempMessage("Skill \"" + skill + "\" updated for " + event.getAuthor().getUser().getName() + ": " + newValue, 6, channel);
                }
            } else {
                Messages.sendMessage("Sorry, i do not know that skill.", event);
            }
        } else {
            Messages.sendMessage("Your syntax is incorrect.", event);
        }
    }
}
