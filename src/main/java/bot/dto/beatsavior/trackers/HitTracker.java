package bot.dto.beatsavior.trackers;

public class HitTracker {
    int leftNoteHit;
    int rightNoteHit;
    int bombHit;
    int miss;
    int maxCombo;

    public int getLeftNoteHit() {
        return leftNoteHit;
    }

    public void setLeftNoteHit(int leftNoteHit) {
        this.leftNoteHit = leftNoteHit;
    }

    public int getRightNoteHit() {
        return rightNoteHit;
    }

    public void setRightNoteHit(int rightNoteHit) {
        this.rightNoteHit = rightNoteHit;
    }

    public int getBombHit() {
        return bombHit;
    }

    public void setBombHit(int bombHit) {
        this.bombHit = bombHit;
    }

    public int getMiss() {
        return miss;
    }

    public void setMiss(int miss) {
        this.miss = miss;
    }

    public int getMaxCombo() {
        return maxCombo;
    }

    public void setMaxCombo(int maxCombo) {
        this.maxCombo = maxCombo;
    }
}
