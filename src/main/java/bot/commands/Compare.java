package bot.commands;

import bot.api.AccSaber;
import bot.api.BeatLeader;
import bot.api.NodeBackend;
import bot.api.ScoreSaber;
import bot.dto.MessageEventDTO;
import bot.dto.accsaber.AccSaberPlayer;
import bot.dto.backend.ComparisonPlayerData;
import bot.dto.beatleader.history.PlayerHistoryItem;
import bot.dto.beatleader.player.BeatLeaderPlayer;
import bot.dto.player.DataBasePlayer;
import bot.utils.Messages;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Compare {

    NodeBackend backend;
    ScoreSaber ss;
    BeatLeader bl;
    AccSaber as;


    public Compare() {
        this.backend = new NodeBackend();
        this.ss = new ScoreSaber();
        this.bl = new BeatLeader();
        this.as = new AccSaber();
    }

    public void generateAndSendCompareImage(DataBasePlayer player1, DataBasePlayer player2, MessageEventDTO event) {
        ComparisonPlayerData player1Data = populatePlayerData(player1);
        ComparisonPlayerData player2Data = populatePlayerData(player2);
        String imagePath = backend.generateComparisonImage(player1Data, player2Data);
        if (imagePath == null) {
            Messages.sendPlainMessage("Image could not be generated.", event.getChannel());
            return;
        }
        File file = new File(imagePath);
        if (file.exists() && file.canRead()) {
            Messages.sendImage(file, player1Data.getId() + "_" + player2Data.getId() + ".png", event);
        } else {
            System.out.println("Cannot access file!");
        }
    }

    private ComparisonPlayerData populatePlayerData(DataBasePlayer dbPlayer) {
        List<Integer> ssHistory = dbPlayer.getHistoryValues();

        List<Integer> blHistory = bl.getPlayerHistoryById(dbPlayer.getPlayerIdLong()).stream()
                .map(PlayerHistoryItem::getRank)
                .collect(Collectors.toList());

        Map<String, Integer> asHistoryMap = as.getPlayerHistoryValues(dbPlayer.getId());
        List<Integer> asHistory = new ArrayList<>(asHistoryMap.values());

        BeatLeaderPlayer blPlayer = bl.getPlayerById(dbPlayer.getPlayerIdLong());
        AccSaberPlayer asPlayer = as.getPlayerById(dbPlayer.getId());

        ComparisonPlayerData playerData = new ComparisonPlayerData();
        playerData.setId(dbPlayer.getPlayerIdLong());
        playerData.setName(dbPlayer.getName());
        playerData.setAvatarURL(dbPlayer.getAvatar());
        playerData.setScoreSaber(ssHistory);
        playerData.setBeatLeader(blHistory);
        playerData.setAccSaber(asHistory);
        playerData.setPpScoreSaber(dbPlayer.getPp());

        if (blPlayer != null) {
            playerData.setPpBeatLeader(blPlayer.getPp());
            playerData.setAvgAccBeatLeader(blPlayer.getScoreStats().getAverageAccuracy() * 100);
            playerData.setHeadset(blPlayer.getScoreStats().getTopHMD());
        }

        if (asPlayer != null) {
            playerData.setAp(asPlayer.getAp());
            playerData.setAvgAccAccSaber(asPlayer.getAverageAcc()  * 100);
        }

        playerData.setAvgAccScoreSaber(dbPlayer.getScoreStats().getAverageRankedAccuracy());

        return playerData;
    }

}
