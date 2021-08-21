package bot.dto.beatsaviour.trackers;

public class WinTracker {
    boolean won;
    String rank;
    double endTime;
    int nbOfPause;

    public boolean isWon() {
        return won;
    }

    public void setWon(boolean won) {
        this.won = won;
    }

    public String getRank() {
        return rank;
    }

    public void setRank(String rank) {
        this.rank = rank;
    }

    public double getEndTime() {
        return endTime;
    }

    public void setEndTime(double endTime) {
        this.endTime = endTime;
    }

    public int getNbOfPause() {
        return nbOfPause;
    }

    public void setNbOfPause(int nbOfPause) {
        this.nbOfPause = nbOfPause;
    }
}
