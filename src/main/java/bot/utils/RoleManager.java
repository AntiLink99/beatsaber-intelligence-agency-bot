package bot.utils;

import java.util.List;
import java.util.stream.Collectors;

import bot.main.BotConstants;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.exceptions.HierarchyException;

public class RoleManager {

	public static boolean isNewMilestone(int rank, Member member) {
		if (member == null || rank > 5000) {
			return false;
		}
		int milestone = ListValueUtils.findMilestoneForRank(rank);
		Role milestoneRole = member.getRoles().stream().filter(role -> role.getName().equals(BotConstants.topRolePrefix + milestone)).findFirst().orElse(null);
		List<Role> milestoneRoles = getMemberRolesByName(member, BotConstants.topRolePrefix);
		return milestoneRole == null || milestoneRoles.size() > 1;
	}

	public static void assignMilestoneRole(int newRank, Member member) {
		int milestone = ListValueUtils.findMilestoneForRank(newRank);
		assignRole(member, BotConstants.topRolePrefix + milestone);
	}

	public static void removeMemberRolesByName(Member member, String name) {
		List<Role> milestoneRoles = getMemberRolesByName(member, name);
		for (Role role : milestoneRoles) {
			member.getGuild().removeRoleFromMember(member, role).queue();
		}
	}

	private static List<Role> getMemberRolesByName(Member member, String name) {
		return member.getRoles().stream().filter(role -> role.getName().toLowerCase().contains(name.toLowerCase())).collect(Collectors.toList());
	}

	public static Role assignRole(Member member, String rolename) {
		List<Role> milestoneRoles = member.getGuild().getRolesByName(rolename, true);
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

	public static boolean memberHasRole(Member member, String milestoneRolesName, String roleToSearch) {
		List<Role> roles = getMemberRolesByName(member, milestoneRolesName);
		boolean memberHasRole = roles != null && roles.stream().anyMatch(r -> r.getName().toLowerCase().equals(roleToSearch));
		boolean memberHasMoreThanOneRole = roles.size() > 1;
		return memberHasRole && !memberHasMoreThanOneRole;
	}
}
