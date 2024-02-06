package bot.main;

import bot.api.ScoreSaber;
import bot.db.DatabaseManager;
import bot.dto.player.DataBasePlayer;
import bot.roles.RoleManager;
import bot.roles.RoleManagerBSG;
import bot.utils.DiscordLogger;
import bot.utils.Format;
import bot.utils.ListValueUtils;
import bot.utils.Messages;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;
import org.apache.commons.lang3.exception.ExceptionUtils;

import java.awt.*;
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
        TextChannel bsgOutput = jda.getTextChannelById(BotConstants.bsgOutputChannelId);

        if (bsgOutput == null) {
            throw new NullPointerException("An output channel variable was not set correctly.");
        }
        this.watcherRunnable = () -> {
            db.connectToDatabase();
            String updatingMessage = "----- Starting User Refresh... [" + Format.oneDigitZero(LocalTime.now().getHour()) + ":" + Format.oneDigitZero(LocalTime.now().getMinute()) + "]";
            DiscordLogger.sendLogInChannel(updatingMessage, DiscordLogger.WATCHER_REFRESH);
            try {
                int fetchCounter = 0;
                List<DataBasePlayer> oldPlayers = db.getAllStoredPlayers();
                List<DataBasePlayer> updatedPlayers = new ArrayList<>();
                //Iterate over stored players
                for (DataBasePlayer storedPlayer : oldPlayers) {
                    //Fetch player from ScoreSaber API
                    DataBasePlayer updatedPlayer = ss.getPlayerById(storedPlayer.getId());
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

                    boolean shouldUpdate = (rankIsDifferent || ppIsDifferent) && (!isInactive || storedPlayer.getCountryRank() != 0) || storedPlayer.getScoreStats() == null;
                    if (shouldUpdate) {
                        db.updatePlayer(updatedPlayer);
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
                e.printStackTrace();
                DiscordLogger.sendLogInChannel(ExceptionUtils.getStackTrace(e), DiscordLogger.WATCHER_REFRESH);
            }
            DiscordLogger.sendLogInChannel("Finished!", DiscordLogger.WATCHER_REFRESH);
        };
    }

    private void handleBSGPlayerUpdate(TextChannel bsgOutput, List<DataBasePlayer> updatedPlayers, List<DataBasePlayer> oldPlayers) {
        updatedPlayers = updatedPlayers.stream().filter(player -> "DE".equals(player.getCountry())).collect(Collectors.toList());
        oldPlayers = oldPlayers.stream().filter(player -> "DE".equals(player.getCountry())).collect(Collectors.toList());

        List<Member> bsgMembers = bsgOutput.getGuild().loadMembers().get();
        for (DataBasePlayer updatedPlayer : updatedPlayers) {
            DataBasePlayer oldPlayer = oldPlayers.stream()
                    .filter(p -> p.getPlayerIdLong() == updatedPlayer.getPlayerIdLong())
                    .findFirst()
                    .orElse(null);
            if (oldPlayer == null || oldPlayer.getRank() == 0) {
                continue;
            }

            //Change certain someone's id
            if (updatedPlayer.getDiscordUserId() == 272778901183266816L) {
                updatedPlayer.setDiscordUserId(779146545755848774L);
            }

            //Role change
            boolean isInactive = updatedPlayer.getCountryRank() == 0;
            Member member = bsgMembers.stream().filter(m -> m.getIdLong() == updatedPlayer.getDiscordUserId()).findFirst().orElse(null);
            if (member != null && RoleManagerBSG.isNewMilestone(updatedPlayer.getCountryRank(), member)) {
                //Remove all "Top "... bot.roles
                RoleManager.removeMemberRolesByName(member, BotConstants.topRolePrefix);
                if (!isInactive) {
                    //Log
                    System.out.println("Updating member: "+member.getEffectiveName());
                    int milestone = ListValueUtils.findBsgMilestoneForRank(updatedPlayer.getCountryRank());
                    String newRoleMessage = "Changed BSG role: " + updatedPlayer.getName() + " New Rank: " + updatedPlayer.getCountryRank() + " - Old Rank: " + oldPlayer.getCountryRank() + "   " + "(Top " + milestone + ")";
                    DiscordLogger.sendLogInChannel(newRoleMessage, DiscordLogger.WATCHER_REFRESH);

                    //Add "Top xxx DE" role
                    RoleManagerBSG.assignMilestoneRole(updatedPlayer.getCountryRank(), member);
                    if (updatedPlayer.getCountryRank() < oldPlayer.getCountryRank()) {
                        Messages.sendBsgRankMessage("🎉 " + Format.bold(Format.underline(updatedPlayer.getName())) + " is now part of the " + Format.underline("Top " + milestone) + " in Germany! Congrats! 🎉", "", updatedPlayer.getProfileURL(), Color.RED, updatedPlayer.getProfilePicture(), bsgOutput);
                    }
                }

            }
            //Snipe Channel
            boolean playerImproved = updatedPlayer.getCountryRank() < oldPlayer.getCountryRank() && updatedPlayer.getPp() != oldPlayer.getPp();
            boolean playerHasJustWentInactive = isInactive && oldPlayer.getCountryRank() != 0;
            if (playerHasJustWentInactive) {
                String inactiveMessage = Format.underline(Format.bold(updatedPlayer.getName())) + " just went inactive! (Previously " + Format.bold("#" + oldPlayer.getCountryRank() + " DE)");
                Messages.sendBsgRankMessage(inactiveMessage, "F", updatedPlayer.getProfileURL(), Color.orange, updatedPlayer.getProfilePicture(), bsgOutput);
                return;
            }
            if (playerImproved && !isInactive) {
                //Send message (only improvement)
                System.out.println("Player " + updatedPlayer.getName() + " improved!");
                String improvementMessage = Format.underline(Format.bold(updatedPlayer.getName())) + " has advanced from rank " + Format.bold("#" + oldPlayer.getCountryRank() + " DE") + " to rank " + Format.bold("#" + updatedPlayer.getCountryRank() + " DE") + "! (" + updatedPlayer.getPp() + "pp)";

                List<DataBasePlayer> snipedPlayers = findSnipedPlayers(updatedPlayer, oldPlayer, updatedPlayers, oldPlayers);
                StringBuilder snipeMessages = new StringBuilder();
                for (int i = 0; i < snipedPlayers.size(); i++) {
                    DataBasePlayer snipedPlayer = snipedPlayers.get(i);
                    snipeMessages.append("\n...and surpassed ")
                            .append("#").append(snipedPlayer.getCountryRank()).append(" ")
                            .append(Format.link(Format.bold(snipedPlayer.getName()), snipedPlayer.getProfileURL()))
                            .append(" (").append(snipedPlayer.getPp()).append("pp)")
                            .append("!");

                    if (i == 10) {
                        snipeMessages.append("\n...");
                        break;
                    }
                }
                Messages.sendBsgRankMessage(improvementMessage, snipeMessages.toString(), updatedPlayer.getProfileURL(), Color.WHITE, updatedPlayer.getProfilePicture(), bsgOutput);
            }
        }
    }

    private List<DataBasePlayer> findSnipedPlayers(DataBasePlayer updatedPlayerWhoAdvanced, DataBasePlayer oldPlayerWhoAdvanced, List<DataBasePlayer> updatedPlayers, List<DataBasePlayer> oldPlayers) {
        List<DataBasePlayer> snipedPlayers = new ArrayList<>();

        long playerId = updatedPlayerWhoAdvanced.getPlayerIdLong();

        for (DataBasePlayer updatedOtherPlayer : updatedPlayers) {
            if (updatedOtherPlayer.getPlayerIdLong() == playerId) {
                continue;
            }
            DataBasePlayer oldOtherPlayer = oldPlayers.stream()
                    .filter(op -> op.getPlayerIdLong() == updatedOtherPlayer.getPlayerIdLong())
                    .findFirst()
                    .orElse(null);
            if (oldOtherPlayer == null) {
                continue;
            }

            boolean otherPlayerWasBetter = oldOtherPlayer.getCountryRank() < oldPlayerWhoAdvanced.getCountryRank();
            boolean otherPlayerIsNowWorse = updatedOtherPlayer.getCountryRank() > updatedPlayerWhoAdvanced.getCountryRank();
            if (otherPlayerWasBetter && otherPlayerIsNowWorse) {
                snipedPlayers.add(updatedOtherPlayer);
            }
        }

        return snipedPlayers;
    }

    public void start() {
        ScheduledExecutorService service = Executors.newSingleThreadScheduledExecutor();
        service.scheduleAtFixedRate(this.watcherRunnable, 0, 20, TimeUnit.MINUTES);
    }
}
