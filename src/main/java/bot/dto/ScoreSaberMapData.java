package bot.dto;

public class ScoreSaberMapData {

    String id;
    String name;
    float bpm;
    int scores_day;
    int ranked;
    float stars;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public float getBpm() {
        return bpm;
    }

    public void setBpm(int bpm) {
        this.bpm = bpm;
    }

    public int getScores_day() {
        return scores_day;
    }

    public void setScores_day(int scores_day) {
        this.scores_day = scores_day;
    }

    public int getRanked() {
        return ranked;
    }

    public boolean isRanked() {
        return ranked == 1;
    }

    public void setRanked(int ranked) {
        this.ranked = ranked;
    }

    public float getStars() {
        return stars;
    }

    public void setStars(float stars) {
        this.stars = stars;
    }
}
