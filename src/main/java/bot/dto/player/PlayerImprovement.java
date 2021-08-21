package bot.dto.player;

import bot.utils.Format;

public class PlayerImprovement implements Comparable<PlayerImprovement> {
    private String playername;
    private int oldRank;
    private int newRank;
    private float improvementSevenDays;
    private String improvementString;

    public PlayerImprovement(String playername, int oldRank, int newRank) {
        this.playername = playername;
        this.oldRank = oldRank;
        this.newRank = newRank;
        this.setImprovementSevenDays((1f - ((float) newRank / (float) oldRank)) * 100);

        String rankString = Format.blueCode("#" + oldRank + " ➜ " + "#" + newRank);
        String changePercent;

        if (this.improvementSevenDays > 0.0) {
            changePercent = Format.greenCode("Improvement: " + Format.decimal(improvementSevenDays) + "%");
        } else if (this.improvementSevenDays < 0.0) {
            changePercent = Format.redCode("Decay: " + Format.decimal(improvementSevenDays) + "%");
        } else {
            changePercent = Format.code("No change: " + Format.decimal(improvementSevenDays) + "%");
        }

        this.improvementString = rankString + changePercent + "\n";
    }

    public int getOldRank() {
        return oldRank;
    }

    public void setOldRank(int oldRank) {
        this.oldRank = oldRank;
    }

    public int getNewRank() {
        return newRank;
    }

    public void setNewRank(int newRank) {
        this.newRank = newRank;
    }

    public float getImprovementSevenDays() {
        return improvementSevenDays;
    }

    public void setImprovementSevenDays(float improvementSevenDays) {
        this.improvementSevenDays = improvementSevenDays;
    }

    public String getImprovementString() {
        return improvementString;
    }

    public void setImprovementString(String improvementString) {
        this.improvementString = improvementString;
    }

    @Override
    public int compareTo(PlayerImprovement imp2) {
        return Float.compare(imp2.getImprovementSevenDays(), this.improvementSevenDays);
    }

    public String getPlayername() {
        return playername;
    }

    public void setPlayername(String playername) {
        this.playername = playername;
    }
}
