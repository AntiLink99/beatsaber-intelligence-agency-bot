package bot.graphics.songsImage;

import bot.dto.LeaderboardServicePlayer;
import bot.dto.PlayerScore;
import bot.dto.supporters.SupporterInfo;

import java.util.List;

public class SongsImageParams {

    final String filePath;
    final List<PlayerScore> scores;
    final SupporterInfo supporterInfo;
    LeaderboardServicePlayer player;

    public SongsImageParams(String filePath, List<PlayerScore> scores, SupporterInfo supporterInfo, LeaderboardServicePlayer player) {
        this.filePath = filePath;
        this.scores = scores;
        this.supporterInfo = supporterInfo;
        this.player = player;
    }

    public String getFilePath() {
        return filePath;
    }

    public List<PlayerScore> getScores() {
        return scores;
    }

    public SupporterInfo getSupporterInfo() {
        return supporterInfo;
    }

    public LeaderboardServicePlayer getPlayer() {
        return player;
    }

    public void setPlayer(LeaderboardServicePlayer player) {
        this.player = player;
    }
}
