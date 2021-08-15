package bot.commands;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import bot.db.DatabaseManager;
import bot.dto.MessageEventDTO;
import bot.main.BotConstants;
import bot.utils.Messages;
import net.dv8tion.jda.api.entities.MessageChannel;

public class SetSkill {

	public static void setSkill(List<String> msgParts, DatabaseManager db, MessageEventDTO event) {
		MessageChannel channel = event.getChannel();
		if (msgParts.size() >= 3) {
			
			List<String> values = new LinkedList<>(Arrays.asList(msgParts.get(2).split(" ")));
			String skill = values.get(0);
			int newValue = -1;
			try {
				newValue = Integer.parseInt(values.get(1));
			} catch (NumberFormatException | IndexOutOfBoundsException e) {
				Messages.sendMessage("The value provided has to be an integer from 1 to 10.", channel);
				return;
			}
			if (newValue < 0 || newValue > 10) {
				Messages.sendMessage("The value for a skill has to be between 0 and 10.", channel);
				return;
			}
			
			if (BotConstants.validSkills.contains(skill)) {
				int rowsChanged = db.setSkill(event.getAuthor().getIdLong(), skill, newValue);
				if (rowsChanged != 0) {
					Messages.sendTempMessage("Skill \"" + skill + "\" updated for " + event.getAuthor().getUser().getName() + ": " + newValue, 6, channel);
				}
			} else {
				Messages.sendMessage("Sorry, i do not know that skill.", channel);
			}
		} else {
			Messages.sendMessage("Your syntax is incorrect.", channel);
		}
	}
}
