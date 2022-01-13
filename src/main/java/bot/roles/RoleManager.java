package bot.roles;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.exceptions.HierarchyException;

import java.util.List;
import java.util.stream.Collectors;

public class RoleManager {
    public static void removeMemberRolesByName(Member member, String name) {
        List<Role> milestoneRoles = getMemberRolesByName(member, name);
        for (Role role : milestoneRoles) {
            try {
                member.getGuild().removeRoleFromMember(member, role).queue();
            } catch (Exception e) {
                //ok
            }
        }
    }

    public static List<Role> getMemberRolesByName(Member member, String name) {
        return member.getRoles().stream().filter(role -> role.getName().toLowerCase().contains(name.toLowerCase())).collect(Collectors.toList());
    }

    public static Role assignRole(Member member, String roleName) {
        List<Role> milestoneRoles = member.getGuild().getRolesByName(roleName, true);
        if (milestoneRoles.size() > 0) {
            Role milestoneRole = milestoneRoles.get(0);
            try {
                member.getGuild().addRoleToMember(member, milestoneRole).queue();
            } catch (HierarchyException e) {
                System.out.println("Role " + milestoneRole.getName() + " is higher than me!");
            }
            return milestoneRole;
        }
        return null;
    }
}
