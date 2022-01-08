package bot.main;

import bot.api.ScoreSaber;
import bot.db.DatabaseManager;
import bot.dto.player.Player;
import bot.utils.DiscordLogger;
import bot.utils.DiscordUtils;
import bot.utils.ListValueUtils;
import bot.utils.Messages;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;
import roles.RoleManager;
import roles.RoleManagerBSG;
import roles.RoleManagerFOAA;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class LeaderboardWatcher {
    DatabaseManager db;
    ScoreSaber ss;
    JDA jda;
    Runnable watcherRunnable;

    public LeaderboardWatcher(DatabaseManager db, ScoreSaber ss, JDA jda) {
        this.db = db;
        this.ss = ss;
        this.jda = jda;
    }

    public void createNewLeaderboardWatcher() {
        TextChannel foaaOutput = jda.getTextChannelById(BotConstants.foaaOutputChannelId);
        TextChannel bsgOutput = jda.getTextChannelById(BotConstants.bsgOutputChannelId);

        if (foaaOutput == null || bsgOutput == null) {
            throw new NullPointerException("An output channel variable was not set correctly.");
        }
        this.watcherRunnable = () -> {
            db.connectToDatabase();
            String updatingMessage = "----- Starting User Refresh... [" + LocalTime.now().getHour() + ":" + LocalTime.now().getMinute() + "]";
            DiscordLogger.sendLogInChannel(updatingMessage, DiscordLogger.WATCHER_REFRESH);
            try {
                int fetchCounter = 0;
                List<Player> oldPlayers = db.getAllStoredPlayers();

                List<Player> updatedPlayers = new ArrayList<>();
                //Iterate over stored players
                for (Player storedPlayer : oldPlayers) {
                    //Fetch player from ScoreSaber API
                    Player updatedPlayer = ss.getPlayerById(storedPlayer.getId());
                    if (updatedPlayer == null) {
                        continue;
                    }
                    updatedPlayer.setDiscordUserId(storedPlayer.getDiscordUserId());
                    updatedPlayer.setCustomAccGridImage(storedPlayer.getCustomAccGridImage());
                    updatedPlayers.add(updatedPlayer);

                    //Update Condition
                    boolean rankIsDifferent = updatedPlayer.getRank() != storedPlayer.getRank();
                    boolean ppIsDifferent = updatedPlayer.getPp() != storedPlayer.getPp();
                    boolean isInactive = updatedPlayer.getRank() == 0;
                    boolean shouldUpdate = (rankIsDifferent || ppIsDifferent) && !isInactive;
                    if (shouldUpdate) {
                        //Set DB Player
                        db.updatePlayer(updatedPlayer);

                        //FOAA
                        handleFOAAPlayerUpdate(foaaOutput, updatedPlayer, storedPlayer);
                    }

                    try {
                        TimeUnit.MILLISECONDS.sleep(50);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    fetchCounter++;

                    if (fetchCounter % 200 == 0) {
                        TimeUnit.MINUTES.sleep(1);
                    }
                }

                //BSG
                handleBSGPlayerUpdate(bsgOutput, updatedPlayers, oldPlayers);
            } catch (Exception e) {
                DiscordLogger.sendLogInChannel("There was an exception in scheduled task: " + e.getMessage(), DiscordLogger.WATCHER_REFRESH);
            }
        };
    }

    private void handleFOAAPlayerUpdate(TextChannel foaaOutput, Player updatedPlayer, Player storedPlayer) {
            Member member = DiscordUtils.getMemberByChannelAndId(foaaOutput, updatedPlayer.getDiscordUserId());
            if (member == null || RoleManagerFOAA.isNewMilestone(updatedPlayer.getRank(), member)) {
                //Log
                String newRoleMessage = "Changed role: " + updatedPlayer.getName() + " New Rank: " + updatedPlayer.getRank() + " - Old Rank: " + storedPlayer.getRank() + "   " + "(Top " + ListValueUtils.findMilestoneForRank(updatedPlayer.getRank()) + ")";
                DiscordLogger.sendLogInChannel(newRoleMessage, DiscordLogger.WATCHER_REFRESH);

                //Remove role
                RoleManager.removeMemberRolesByName(member, BotConstants.topRolePrefix);

                //Add role
                RoleManagerFOAA.assignMilestoneRole(updatedPlayer.getRank(), member);

                //Send message
                Messages.sendMilestoneMessage(updatedPlayer, foaaOutput);
            }
    }

    private void handleBSGPlayerUpdate(TextChannel bsgOutput, List<Player> updatedPlayers, List<Player> oldPlayers) {
        updatedPlayers = updatedPlayers.stream().filter(player -> "DE".equals(player.getCountry())).collect(Collectors.toList());
        oldPlayers = oldPlayers.stream().filter(player -> "DE".equals(player.getCountry())).collect(Collectors.toList());

        for (Player updatedPlayer : updatedPlayers) {
            Player oldPlayer = oldPlayers.stream().filter(p -> p.getPlayerIdLong() == updatedPlayer.getPlayerIdLong()).findFirst().orElse(null);
            if (oldPlayer == null) {
                continue;
            }

            List<Player> snipedPlayers;
            if (updatedPlayer.getCountryRank() < oldPlayer.getCountryRank()) {
               //Improvement
                snipedPlayers = findSnipedPlayers(updatedPlayer, oldPlayer,  updatedPlayers, oldPlayers);
            } else if (updatedPlayer.getCountryRank() > oldPlayer.getCountryRank()) {
                //Decay

            }

            Member member = DiscordUtils.getMemberByChannelAndId(bsgOutput, updatedPlayer.getDiscordUserId());
            if (member == null || RoleManagerBSG.isNewMilestone(updatedPlayer.getRank(), member)) {
                //Log
                String newRoleMessage = "Changed role: " + updatedPlayer.getName() + " New Rank: " + updatedPlayer.getRank() + " - Old Rank: " + oldPlayer.getRank() + "   " + "(Top " + ListValueUtils.findMilestoneForRank(updatedPlayer.getRank()) + ")";
                DiscordLogger.sendLogInChannel(newRoleMessage, DiscordLogger.WATCHER_REFRESH);

                //Remove role
                RoleManager.removeMemberRolesByName(member, BotConstants.topRolePrefix);

                //Add role
                RoleManagerBSG.assignMilestoneRole(updatedPlayer.getCountryRank(), member);

                //Send message (only improvement)
                EmbedBuilder embed = new EmbedBuilder();
                String improvementMessage = updatedPlayer.getName() + " has advanced from rank #" + oldPlayer.getCountryRank() + " to rank " + updatedPlayer.getCountryRank() + " ("+ updatedPlayer.getPp()+"pp)";
                embed.setThumbnail(updatedPlayer.getProfilePicture());
                //Send Embed
            }
        }
    }

    private List<Player> findSnipedPlayers(Player updatedPlayer, Player oldPlayer, List<Player> updatedPlayers, List<Player> oldPlayers) {
        List<Player> snipedPlayers = new ArrayList<>();
        return snipedPlayers;
    }

    public void start() {
        ScheduledExecutorService service = Executors.newSingleThreadScheduledExecutor();
        service.scheduleAtFixedRate(this.watcherRunnable, 0, 20, TimeUnit.MINUTES);
    }
}
