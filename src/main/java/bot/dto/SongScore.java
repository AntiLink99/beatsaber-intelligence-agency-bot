package bot.dto;

import org.ocpsoft.prettytime.PrettyTime;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Locale;

public class SongScore {
    private int rank;
    private long scoreId;
    private long score;
    private float pp;
    private float weight;
    private double accuracy = -1;
    private String timeSet;
    private long leaderboardId;
    private int maxScore;
    private int difficulty;
    private String songHash;
    private String songName;
    private String songSubName;
    private String songAuthorName;
    private String levelAuthorName;
    private String coverURL; //manually set
    private float songStars; //manually set

    private final transient DecimalFormat format;

    public SongScore() {
        format = new DecimalFormat("##0.00");
        format.setDecimalFormatSymbols(DecimalFormatSymbols.getInstance(Locale.ENGLISH));
    }

    public int getRank() {
        return rank;
    }

    public void setRank(int rank) {
        this.rank = rank;
    }

    public long getScoreId() {
        return scoreId;
    }

    public void setScoreId(long scoreId) {
        this.scoreId = scoreId;
    }

    public long getScore() {
        return score;
    }

    public void setScore(long score) {
        this.score = score;
    }

    public float getPp() {
        return pp;
    }

    public void setPp(float pp) {
        this.pp = pp;
    }

    public float getWeight() {
        return weight;
    }

    public void setWeight(float weight) {
        this.weight = weight;
    }

    public String getPpString() {
        return format.format(getPp()) + "PP";
    }

    public String getWeightPpString() {
        return "(" + format.format(getPp() * getWeight()) + "PP)";
    }

    public String getSongName() {
        return songName;
    }

    public void setSongName(String songName) {
        this.songName = songName;
    }

    public String getSongSubName() {
        return songSubName;
    }

    public void setSongSubName(String songSubName) {
        this.songSubName = songSubName;
    }

    public String getSongAuthorName() {
        return songAuthorName;
    }

    public void setSongAuthorName(String songAuthorName) {
        this.songAuthorName = songAuthorName;
    }

    public String getLevelAuthorName() {
        return levelAuthorName;
    }

    public void setLevelAuthorName(String levelAuthorName) {
        this.levelAuthorName = levelAuthorName;
    }

    public String getCoverURL() {
        return coverURL;
    }

    public void setCoverURL(String coverURL) {
        this.coverURL = coverURL;
    }

    public String getSongHash() {
        return songHash;
    }

    public void setSongHash(String songHash) {
        this.songHash = songHash;
    }

    public int getMaxScore() {
        return maxScore;
    }

    public void setMaxScore(int maxScore) {
        this.maxScore = maxScore;
    }

    public double getAccuracy() {
        if (accuracy != -1) {
            return accuracy;
        }
        if (maxScore != 0) {
            return (double) score / (double) maxScore;
        }
        return -1;
    }

    public void setAccuracy(float accuracy) {
        this.accuracy = accuracy;
    }

    public String getAccuracyString() {
        double acc = getAccuracy();
        if (acc != -1 && !Double.isInfinite(acc)) {
            return format.format(getAccuracy() * 100d) + "%";
        }
        return "";
    }

    public int getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(int difficulty) {
        this.difficulty = difficulty;
    }

    public String getDifficultyName() {
        switch (difficulty) {
            case 1:
                return "Easy";
            case 3:
                return "Normal";
            case 5:
                return "Hard";
            case 7:
                return "Expert";
            case 9:
                return "Expert+";
            default:
                return "???";
        }
    }

    public long getLeaderboardId() {
        return leaderboardId;
    }

    public void setLeaderboardId(long leaderboardId) {
        this.leaderboardId = leaderboardId;
    }

    public String getTimeSet() {
        return timeSet;
    }

    public void setTimeSet(String timeSet) {
        this.timeSet = timeSet;
    }

    public LocalDateTime getTimeSetLocalDateTime() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        return LocalDateTime.parse(timeSet, formatter);
    }

    public String getRelativeTimeString() {
        return new PrettyTime(Locale.ENGLISH).format(Date.from(getTimeSetLocalDateTime().atZone(ZoneOffset.UTC).toInstant()));
    }

    public float getSongStars() {
        return songStars;
    }

    public void setSongStars(float songStars) {
        this.songStars = songStars;
    }
}
