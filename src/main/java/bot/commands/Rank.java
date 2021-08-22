package bot.commands;

import bot.api.ApiConstants;
import bot.api.ScoreSaber;
import bot.dto.MessageEventDTO;
import bot.dto.leaderboards.LeaderboardEntry;
import bot.dto.leaderboards.LeaderboardType;
import bot.dto.player.Player;
import bot.utils.Format;
import bot.utils.Messages;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public class Rank {

    public void sendGlobalRank(Player player, MessageEventDTO event) {
        int startPage = getPageNrFromPlayerRank(player.getRank() - 2);
        sendRank(player, startPage, 200, null, LeaderboardType.GLOBAL, event);
    }

    public void sendLocalRank(Player player, MessageEventDTO event) {
        int startPage = getPageNrFromPlayerRank(player.getCountryRank() - 2);
        sendRank(player, startPage, 200, player.getCountry(), LeaderboardType.LOCAL, event);
    }

    public void sendDACHRank(Player player, MessageEventDTO event) {
        String[] dachCodes = {"de","at","ch"};
        if (!Arrays.asList(dachCodes).contains(player.getCountry().toLowerCase())) {
            Messages.sendMessage("Your are not from Germany, Austria or Switzerland.", event.getChannel());
            return;
        }
        sendRank(player, 1, 10000, "de,at,ch", LeaderboardType.DACH, event);
    }

    private void sendRank(Player player, int startPage, int sizeLimit, String countryCode, LeaderboardType leaderboardType, MessageEventDTO event) {
        ScoreSaber ss = new ScoreSaber();
        List<LeaderboardEntry> leaderboardEntries = ss.findLeaderboardEntriesAroundPlayer(player, countryCode, startPage, sizeLimit);
        if (leaderboardEntries == null) {
            Messages.sendMessage("Could not extract ScoreSaber profiles. Maybe your rank is too low or there is another error.", event.getChannel());
            return;
        }
        LeaderboardEntry playerEntry = leaderboardEntries.stream()
                .filter(entry -> entry.getPlayerId() == player.getPlayerIdLong())
                .findFirst()
                .orElse(null);
        int playerIndex = leaderboardEntries.indexOf(playerEntry);
        assert playerEntry != null;
        float playerPp = playerEntry.getPp();

        String resultMessage = Format.bold("------------------------------------") + "\n";
        if (playerIndex > 1) {
            resultMessage += toEntryString(leaderboardEntries.get(playerIndex - 2), playerPp, leaderboardType);
        }
        if (playerIndex > 0) {
            resultMessage += toEntryString(leaderboardEntries.get(playerIndex - 1), playerPp, leaderboardType);
        }
        resultMessage += toEntryString(playerEntry, -1, leaderboardType);
        resultMessage += toEntryString(leaderboardEntries.get(playerIndex + 1), playerPp, leaderboardType);
        resultMessage += toEntryString(leaderboardEntries.get(playerIndex + 2), playerPp, leaderboardType);

        String title = "";
        switch (leaderboardType) {
            case LOCAL:
                title = "Local leaderboard";
                break;
            case GLOBAL:
                title = "Global leaderboard";
                break;
            case DACH:
                title = "Leaderboard for Germany, Austria & Switzerland";
                break;
        }
        String titleUrl = "";
        switch (leaderboardType) {
            case LOCAL:
                titleUrl = ss.getLeaderboardUrl(getPageNrFromPlayerRank(player.getCountryRank()), countryCode);
                break;
            case GLOBAL:
                titleUrl = ss.getLeaderboardUrl(getPageNrFromPlayerRank(player.getRank()), countryCode);
                break;
            case DACH:
                titleUrl = ss.getLeaderboardUrl(getPageNrFromPlayerRank(playerIndex), countryCode);
                break;
        }

        Messages.sendMessageWithTitle(resultMessage, Format.underline(title), titleUrl, event.getChannel());
    }

    private String toEntryString(LeaderboardEntry playerEntry, float ownPP, LeaderboardType type) {
        String entryString = "";
        if (playerEntry == null) {
            return entryString;
        }
        boolean isOwnEntry = ownPP == -1;
        entryString += "#" + playerEntry.getPlayerRank();
        String countryName = new Locale("", playerEntry.getCountryCode().toUpperCase()).getDisplayCountry(Locale.ENGLISH);
        if (type == LeaderboardType.LOCAL) {
            entryString += " in " + countryName + " :flag_" + playerEntry.getCountryCode().toLowerCase() + ": \n";
        } else {
            entryString += "\nFrom " + countryName + " :flag_" + playerEntry.getCountryCode().toLowerCase() + ": \n";
        }
        entryString += Format.bold(Format.link(playerEntry.getPlayerName(), ApiConstants.SS_PRE_URL + playerEntry.getPlayerUrl()) + (isOwnEntry ? " (You)" : "")) + "\n";
        if (!isOwnEntry) {
            float ppDiff = playerEntry.getPp() - ownPP;
            entryString += (ppDiff > 0 ? Format.decimal(ppDiff) + "pp more than you." : Format.decimal(-ppDiff) + "pp less than you.") + "\n";
        } else {
            entryString += "Your PP: " + playerEntry.getPp() + "pp\n";
        }
        return entryString + Format.bold("------------------------------------") + "\n";
    }

    private static int getPageNrFromPlayerRank(int rank) {
        if (rank <= 0) {
            return 1;
        }
        int modulus = rank % 50;
        int page = rank / 50;
        if (modulus != 0) {
            page++;
        }
        return page;
    }
}
