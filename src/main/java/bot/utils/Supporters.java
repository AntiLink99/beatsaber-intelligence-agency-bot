package bot.utils;

import bot.dto.supporters.SupportType;
import bot.main.BotConstants;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.User;

import java.util.ArrayList;
import java.util.List;

public class Supporters {
    public static List<SupportType> getUserSupportTypes(User user) {
        if (user == null) {
            return new ArrayList<>();
        }
        List<SupportType> supportTypes = new ArrayList<>();
        Guild supporterGuild = user.getJDA().getGuildById(BotConstants.supporterServerId);
        if (supporterGuild == null) {
            return supportTypes;
        }
        Member guildMember = supporterGuild.getMember(user);
        if (guildMember != null) {
            for (Role role : guildMember.getRoles()) {
                long roleId = role.getIdLong();
                if (roleId == BotConstants.twitchSupporterRoleId) {
                    supportTypes.add(SupportType.TWITCH);
                } else if (roleId == BotConstants.kofiSupporterRoleId) {
                    supportTypes.add(SupportType.KOFI);
                } else if (roleId == BotConstants.patreonSupporterRoleId) {
                    supportTypes.add(SupportType.PATREON);
                } else if (roleId == BotConstants.patreonSmallSupporterRoleId) {
                    supportTypes.add(SupportType.PATREON_SMALL);
                } else if (roleId == BotConstants.patreonMediumSupporterRoleId) {
                    supportTypes.add(SupportType.PATREON_MEDIUM);
                } else if (roleId == BotConstants.patreonChadSupporterRoleId) {
                    supportTypes.add(SupportType.PATREON_CHAD);
                }
            }
        }
        return supportTypes;
    }
}
