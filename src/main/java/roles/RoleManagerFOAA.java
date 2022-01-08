package roles;

import bot.main.BotConstants;
import bot.utils.ListValueUtils;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;

import java.util.List;

public class RoleManagerFOAA {

    public static boolean isNewMilestone(int rank, Member member) {
        if (member == null || rank > 5000) {
            return false;
        }
        int milestone = ListValueUtils.findMilestoneForRank(rank);
        Role milestoneRole = member.getRoles().stream().filter(role -> role.getName().equals(BotConstants.topRolePrefix + milestone)).findFirst().orElse(null);
        List<Role> milestoneRoles = RoleManager.getMemberRolesByName(member, BotConstants.topRolePrefix);
        return milestoneRole == null || milestoneRoles.size() > 1;
    }

    public static void assignMilestoneRole(int newRank, Member member) {
        int milestone = ListValueUtils.findMilestoneForRank(newRank);
        RoleManager.assignRole(member, BotConstants.topRolePrefix + milestone);
    }

    public static boolean memberHasRole(Member member, String milestoneRolesName, String roleToSearch) {
        List<Role> roles = RoleManager.getMemberRolesByName(member, milestoneRolesName);
        boolean memberHasRole = roles != null && roles.stream().anyMatch(r -> r.getName().toLowerCase().equals(roleToSearch));
        assert roles != null;
        boolean memberHasMoreThanOneRole = roles.size() > 1;
        return memberHasRole && !memberHasMoreThanOneRole;
    }
}
