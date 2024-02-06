package bot.dto.accsaber;

public class AccSaberPlayer {

    private int rank;
    private int rankLastWeek;
    private String playerId;
    private String playerName;
    private String avatarUrl;
    private String hmd;
    private double averageAcc;
    private double ap;
    private double averageApPerMap;
    private int rankedPlays;
    private boolean accChamp;

    public AccSaberPlayer() {}

    public AccSaberPlayer(int rank, int rankLastWeek, String playerId, String playerName,
                          String avatarUrl, String hmd, double averageAcc, double ap,
                          double averageApPerMap, int rankedPlays, boolean accChamp) {
        this.rank = rank;
        this.rankLastWeek = rankLastWeek;
        this.playerId = playerId;
        this.playerName = playerName;
        this.avatarUrl = avatarUrl;
        this.hmd = hmd;
        this.averageAcc = averageAcc;
        this.ap = ap;
        this.averageApPerMap = averageApPerMap;
        this.rankedPlays = rankedPlays;
        this.accChamp = accChamp;
    }

    public int getRank() {
        return rank;
    }

    public void setRank(int rank) {
        this.rank = rank;
    }

    public int getRankLastWeek() {
        return rankLastWeek;
    }

    public void setRankLastWeek(int rankLastWeek) {
        this.rankLastWeek = rankLastWeek;
    }

    public String getPlayerId() {
        return playerId;
    }

    public void setPlayerId(String playerId) {
        this.playerId = playerId;
    }

    public String getPlayerName() {
        return playerName;
    }

    public void setPlayerName(String playerName) {
        this.playerName = playerName;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    public String getHmd() {
        return hmd;
    }

    public void setHmd(String hmd) {
        this.hmd = hmd;
    }

    public double getAverageAcc() {
        return averageAcc;
    }

    public void setAverageAcc(double averageAcc) {
        this.averageAcc = averageAcc;
    }

    public double getAp() {
        return ap;
    }

    public void setAp(double ap) {
        this.ap = ap;
    }

    public double getAverageApPerMap() {
        return averageApPerMap;
    }

    public void setAverageApPerMap(double averageApPerMap) {
        this.averageApPerMap = averageApPerMap;
    }

    public int getRankedPlays() {
        return rankedPlays;
    }

    public void setRankedPlays(int rankedPlays) {
        this.rankedPlays = rankedPlays;
    }

    public boolean isAccChamp() {
        return accChamp;
    }

    public void setAccChamp(boolean accChamp) {
        this.accChamp = accChamp;
    }

    @Override
    public String toString() {
        return "AccSaberPlayer{" +
                "rank=" + rank +
                ", rankLastWeek=" + rankLastWeek +
                ", playerId='" + playerId + '\'' +
                ", playerName='" + playerName + '\'' +
                ", avatarUrl='" + avatarUrl + '\'' +
                ", hmd='" + hmd + '\'' +
                ", averageAcc=" + averageAcc +
                ", ap=" + ap +
                ", averageApPerMap=" + averageApPerMap +
                ", rankedPlays=" + rankedPlays +
                ", accChamp=" + accChamp +
                '}';
    }
}

