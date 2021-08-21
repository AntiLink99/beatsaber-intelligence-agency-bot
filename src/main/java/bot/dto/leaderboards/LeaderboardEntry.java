package bot.dto.leaderboards;

public class LeaderboardEntry {
    String imageUrl;
    String countryCode;
    long playerId;
    String playerUrl;
    String playerName;
    int playerRank;
    float pp;
    int weeklyChange;

    public LeaderboardEntry(String imageUrl, String countryCode, String playerName, long playerId, String playerUrl, int playerRank, float pp, int weeklyChange) {
        this.imageUrl = imageUrl;
        this.countryCode = countryCode;
        this.playerId = playerId;
        this.playerUrl = playerUrl;
        this.playerName = playerName;
        this.playerRank = playerRank;
        this.pp = pp;
        this.weeklyChange = weeklyChange;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    public String getPlayerName() {
        return playerName;
    }

    public void setPlayerName(String playerName) {
        this.playerName = playerName;
    }

    public int getPlayerRank() {
        return playerRank;
    }

    public void setPlayerRank(int playerRank) {
        this.playerRank = playerRank;
    }

    public float getPp() {
        return pp;
    }

    public void setPp(float pp) {
        this.pp = pp;
    }

    public int getWeeklyChange() {
        return weeklyChange;
    }

    public void setWeeklyChange(int weeklyChange) {
        this.weeklyChange = weeklyChange;
    }

    public long getPlayerId() {
        return playerId;
    }


    public void setPlayerId(long playerId) {
        this.playerId = playerId;
    }

    public String getPlayerUrl() {
        return playerUrl;
    }

    public void setPlayerUrl(String playerUrl) {
        this.playerUrl = playerUrl;
    }

}
