package bot.main;

import java.util.List;
import java.util.stream.Collectors;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;

public class RoleManager {

	private static int[] rankMilestones = { 50, 100, 200, 300, 400, 500, 600, 700, 800, 900, 1000, 2000, 3000, 4000, 5000 };
	private static String rolePrefix = "Top ";

	public static boolean isNewMilestone(int rank, Member member) {
		if (member == null || rank > 5000) {
			return false;
		}
		int milestone = findMilestoneForRank(rank);
		Role milestoneRole = member.getRoles().stream().filter(role -> role.getName().equals(rolePrefix + milestone)).findFirst().orElse(null);
		List<Role> milestoneRoles = getMilestoneRolesFromMember(member);
		return milestoneRole == null || milestoneRoles.size() > 1;
	}

	public static int findMilestoneForRank(int rank) {
		for (int milestone : rankMilestones) {
			if (rank <= milestone) {
				return milestone;
			}
		}
		return -1;
	}

	public static void assignMilestoneRole(int newRank, Member member) {
		int milestone = findMilestoneForRank(newRank);
		List<Role> milestoneRoles = member.getGuild().getRolesByName(rolePrefix + milestone, true);
		if (milestoneRoles.size() != 0) {
			Role milestoneRole = milestoneRoles.get(0);
			member.getGuild().addRoleToMember(member, milestoneRole).queue();
		}
	}

	public static void removeAllMilestoneRoles(Member member) {
		List<Role> milestoneRoles = getMilestoneRolesFromMember(member);
		for (Role role : milestoneRoles) {
			member.getGuild().removeRoleFromMember(member, role).queue();
		}
	}
	
	private static List<Role> getMilestoneRolesFromMember(Member member) {
		return member.getRoles().stream().filter(role -> role.getName().contains(rolePrefix)).collect(Collectors.toList());
	}
}
