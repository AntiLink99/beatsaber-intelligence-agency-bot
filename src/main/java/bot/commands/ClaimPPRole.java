package bot.commands;

import bot.api.ScoreSaber;
import bot.db.DatabaseManager;
import bot.dto.MessageEventDTO;
import bot.dto.player.Player;
import bot.dto.scoresaber.PlayerScore;
import bot.main.BotConstants;
import bot.utils.Format;
import bot.utils.ListValueUtils;
import bot.utils.Messages;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.Role;
import org.jetbrains.annotations.NotNull;
import roles.RoleManager;
import roles.RoleManagerFOAA;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class ClaimPPRole {

    final DatabaseManager db;

    public ClaimPPRole(DatabaseManager db) {
        this.db = db;
    }

    public void validateAndAssignRole(@NotNull Member member, MessageChannel channel, boolean showMessages) {
        ScoreSaber ss = new ScoreSaber();
        long discordUserId = member.getUser().getIdLong();
        long playerId = Long.parseLong(db.getPlayerByDiscordId(discordUserId).getId());
        if (playerId == -1) {
            Messages.sendMessage("You are not registered. Use \"ru help\".", channel);
            return;
        }

        List<PlayerScore> topScores = ss.getTopScoresByPlayerId(playerId);
        if (topScores != null && topScores.size() > 0) {
            double maxPP = topScores.get(0).getScore().getPp();
            int ppMilestone = ListValueUtils.findHighestSurpassedValue(maxPP, BotConstants.foaaPpRoleMilestones);
            if (ppMilestone != -1) {
                String milestoneRoleName = Format.foaaRole(BotConstants.foaaPpRoles[Arrays.asList(BotConstants.foaaPpRoleMilestones).indexOf(ppMilestone)]);
                if (!RoleManagerFOAA.memberHasRole(member, " PP", milestoneRoleName)) {
                    RoleManager.removeMemberRolesByName(member, BotConstants.ppRoleSuffix);
                    Role assignedRole = RoleManager.assignRole(member, milestoneRoleName);
                    if (assignedRole != null && showMessages) {
                        Messages.sendMessage(Format.bold("🎉" + member.getAsMention() + " Congrats, your PP role was updated! " + assignedRole.getAsMention()) + " 🎉", channel);
                    }
                } else if (showMessages) {
                    Messages.sendMessage(member.getAsMention() + " You already have the role, please stahp.", channel);
                }
            }
        } else {
            Messages.sendMessage(member.getAsMention() + " You do need to have at least 300pp for a pp role.", channel);
        }
    }

    public void validateAndAssignRoleForAll(MessageEventDTO event) {
        List<Player> storedPlayers = db.getAllStoredPlayers();
        for (Player player : storedPlayers) {
            Member member = event.getGuild().getMemberById(player.getDiscordUserId());
            if (member != null) {
                validateAndAssignRole(member, event.getChannel(), false);
            }
            try {
                TimeUnit.MILLISECONDS.sleep(125);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        Messages.sendMessage("Members have been updated. ✔️", event.getChannel());
    }
}
