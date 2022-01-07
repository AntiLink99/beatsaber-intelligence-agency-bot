package bot.main;

import bot.api.ScoreSaber;
import bot.db.DatabaseManager;
import bot.dto.player.Player;
import bot.utils.DiscordLogger;
import bot.utils.ListValueUtils;
import bot.utils.Messages;
import bot.utils.RoleManager;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;

import java.time.LocalTime;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class LeaderboardWatcher {
    DatabaseManager db;
    ScoreSaber ss;

    public LeaderboardWatcher(DatabaseManager db, ScoreSaber ss) {
        this.db = db;
        this.ss = ss;
        this.
    }

    public static void Runnab Runnable getFOAAWatcher(TextChannel botChannel) {
        Runnable runnable = () -> {
            db.connectToDatabase();
            DiscordLogger.sendLogInChannel("----- Starting User Refresh... [" + LocalTime.now().getHour() + ":" + LocalTime.now().getMinute() + "]", DiscordLogger.FOAA_REFRESH);
            try {
                int fetchCounter = 0;
                List<Player> players = db.getAllStoredPlayers();
                for (Player storedPlayer : players) {
                    Player ssPlayer = ss.getPlayerById(storedPlayer.getPlayerId());
                    if (ssPlayer == null) {
                        continue;
                    }
                    ssPlayer.setDiscordUserId(storedPlayer.getDiscordUserId());
                    ssPlayer.setCustomAccGridImage(storedPlayer.getCustomAccGridImage());
                    if (ssPlayer.getRank() != storedPlayer.getRank() && ssPlayer.getRank() != 0) {
                        db.updatePlayer(ssPlayer);
                        if (botChannel.getGuild().getIdLong() == BotConstants.foaaServerId) {
                            Member member = botChannel.getGuild().getMembers().stream().filter(m -> m.getUser().getIdLong() == ssPlayer.getDiscordUserId()).findFirst().orElse(null);
                            if (RoleManager.isNewMilestone(ssPlayer.getRank(), member)) {
                                DiscordLogger.sendLogInChannel("Changed role: " + ssPlayer.getPlayerName() + " New Rank: " + ssPlayer.getRank() + " - Old Rank: " + storedPlayer.getRank() + "   " + "(Top " + ListValueUtils.findMilestoneForRank(ssPlayer.getRank()) + ")",
                                        DiscordLogger.FOAA_REFRESH);
                                RoleManager.removeMemberRolesByName(member, BotConstants.topRolePrefix);
                                RoleManager.assignMilestoneRole(ssPlayer.getRank(), member);
                                Messages.sendMilestoneMessage(ssPlayer, botChannel);
                            }
                        }
                    }

                    try {
                        TimeUnit.MILLISECONDS.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    fetchCounter++;

                    if (fetchCounter % 20 == 0) {
                        TimeUnit.MINUTES.sleep(1);
                    }
                }
            } catch (Exception e) {
                DiscordLogger.sendLogInChannel("There was an exception in scheduled task: " + e.getMessage(), DiscordLogger.FOAA_REFRESH);
            }
        };
        ScheduledExecutorService service = Executors.newSingleThreadScheduledExecutor();
        service.scheduleAtFixedRate(runnable, 0, 20, TimeUnit.MINUTES);
        return runnable;
    }
}
