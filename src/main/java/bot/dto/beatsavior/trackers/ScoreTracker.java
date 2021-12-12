package bot.dto.beatsavior.trackers;

import java.util.List;

public class ScoreTracker {
    long rawScore;
    long score;
    long personalBest;
    double rawRatio;
    double modifiedRatio;
    double personalBestRawRatio;
    double personalBestModifiedRatio;
    float modifiersMultiplier;
    List<String> modifiers;

    public long getRawScore() {
        return rawScore;
    }

    public void setRawScore(long rawScore) {
        this.rawScore = rawScore;
    }

    public long getScore() {
        return score;
    }

    public void setScore(long score) {
        this.score = score;
    }

    public long getPersonalBest() {
        return personalBest;
    }

    public void setPersonalBest(long personalBest) {
        this.personalBest = personalBest;
    }

    public double getRawRatio() {
        return rawRatio;
    }

    public void setRawRatio(double rawRatio) {
        this.rawRatio = rawRatio;
    }

    public double getModifiedRatio() {
        return modifiedRatio;
    }

    public void setModifiedRatio(double modifiedRatio) {
        this.modifiedRatio = modifiedRatio;
    }

    public double getPersonalBestRawRatio() {
        return personalBestRawRatio;
    }

    public void setPersonalBestRawRatio(double personalBestRawRatio) {
        this.personalBestRawRatio = personalBestRawRatio;
    }

    public double getPersonalBestModifiedRatio() {
        return personalBestModifiedRatio;
    }

    public void setPersonalBestModifiedRatio(double personalBestModifiedRatio) {
        this.personalBestModifiedRatio = personalBestModifiedRatio;
    }

    public float getModifiersMultiplier() {
        return modifiersMultiplier;
    }

    public void setModifiersMultiplier(float modifiersMultiplier) {
        this.modifiersMultiplier = modifiersMultiplier;
    }

    public List<String> getModifiers() {
        return modifiers;
    }

    public void setModifiers(List<String> modifiers) {
        this.modifiers = modifiers;
    }
}
