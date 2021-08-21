package bot.dto.beatsaviour;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class RankedMapRootDifficulties {
    RankedMapRootDifficulty easy;
    RankedMapRootDifficulty normal;
    RankedMapRootDifficulty hard;
    RankedMapRootDifficulty expert;
    RankedMapRootDifficulty expertplus;

    public RankedMapRootDifficulty getEasy() {
        return easy;
    }

    public void setEasy(RankedMapRootDifficulty easy) {
        this.easy = easy;
    }

    public RankedMapRootDifficulty getNormal() {
        return normal;
    }

    public void setNormal(RankedMapRootDifficulty normal) {
        this.normal = normal;
    }

    public RankedMapRootDifficulty getHard() {
        return hard;
    }

    public void setHard(RankedMapRootDifficulty hard) {
        this.hard = hard;
    }

    public RankedMapRootDifficulty getExpert() {
        return expert;
    }

    public void setExpert(RankedMapRootDifficulty expert) {
        this.expert = expert;
    }

    public RankedMapRootDifficulty getExpertPlus() {
        return expertplus;
    }

    public void setExpertPlus(RankedMapRootDifficulty expertPlus) {
        this.expertplus = expertPlus;
    }

    public List<RankedMapRootDifficulty> getDifficultiesAsList() {
        List<RankedMapRootDifficulty> diffs = new ArrayList<>();
        diffs.add(easy);
        diffs.add(normal);
        diffs.add(hard);
        diffs.add(expert);
        diffs.add(expertplus);
        diffs.removeIf(Objects::isNull);
        return diffs;
    }

    public static class RankedMapRootDifficulty extends RankedMapDifficulty {
        @SerializedName(value = "diff", alternate = {"Diff"})
        String diff;
        @SerializedName(value = "scores", alternate = {"Scores"})
        int scores;
        @SerializedName(value = "stars", alternate = {"Stars"})
        float stars;
        @SerializedName(value = "ranked", alternate = {"Ranked"})
        boolean ranked;

        public String getDiff() {
            return diff;
        }

        public void setDiff(String diff) {
            this.diff = diff;
        }

        public int getScores() {
            return scores;
        }

        public void setScores(int scores) {
            this.scores = scores;
        }

        public float getStars() {
            return stars;
        }

        public void setStars(float stars) {
            this.stars = stars;
        }

        public boolean isRanked() {
            return ranked;
        }

        public void setRanked(boolean ranked) {
            this.ranked = ranked;
        }
    }
}
