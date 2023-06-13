package bot.api;

import bot.main.BotConstants;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.User;

public class Supporters {
    public static boolean isUserSupporter(User user, JDA jda) {
        Guild supporterGuild = jda.getGuildById(BotConstants.supporterServerId);
        if (supporterGuild == null || user == null) {
            return false;
        }
        Member guildMember = supporterGuild.getMember(user);
        if (guildMember != null) {
            for (Role role : guildMember.getRoles()) {
                long roleId = role.getIdLong();
                if (roleId == BotConstants.twitchSupporterRoleId || roleId == BotConstants.kofiSupporterRoleId) {
                    return true;
                }
            }
        }
        return false;
    }
}
