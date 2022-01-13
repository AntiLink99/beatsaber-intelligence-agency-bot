package bot.roles;

import bot.main.BotConstants;
import bot.utils.ListValueUtils;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;

import java.util.List;

public class RoleManagerBSG {

    public static boolean isNewMilestone(int countryRank, Member member) {
        int minBsgCountryRank = BotConstants.bsgCountryRankMilestones[BotConstants.bsgCountryRankMilestones.length - 1];
        if (member == null || countryRank > minBsgCountryRank) {
            return false;
        }
        int milestone = ListValueUtils.findBsgMilestoneForRank(countryRank);
        Role milestoneRole = member.getRoles().stream()
                .filter(role -> role.getName().equals(BotConstants.topRolePrefix + milestone + " DE"))
                .findFirst()
                .orElse(null);
        List<Role> milestoneRoles = RoleManager.getMemberRolesByName(member, BotConstants.topRolePrefix);
        return milestoneRole == null || milestoneRoles.size() > 1;
    }

    public static void assignMilestoneRole(int countryRank, Member member) {
        int milestone = ListValueUtils.findBsgMilestoneForRank(countryRank);
        RoleManager.assignRole(member, BotConstants.topRolePrefix + milestone + " DE");
    }
}
