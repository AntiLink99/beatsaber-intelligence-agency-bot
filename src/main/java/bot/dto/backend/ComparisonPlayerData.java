package bot.dto.backend;

import java.util.List;

public class ComparisonPlayerData {
    private long id;
    private String name;
    private String avatarURL;
    private List<Integer> scoreSaber;
    private List<Integer> beatLeader;
    private List<Integer> accSaber;
    private float ppScoreSaber;
    private double ppBeatLeader;
    private double ap;
    private double avgAccScoreSaber;
    private double avgAccBeatLeader;
    private double avgAccAccSaber;
    private int headset;

    public ComparisonPlayerData() {}

    public ComparisonPlayerData(long id, String name, String avatarURL, List<Integer> scoreSaber,
                                List<Integer> beatLeader, List<Integer> accSaber, float ppScoreSaber,
                                double ppBeatLeader, double ap, double avgAccScoreSaber,
                                double avgAccBeatLeader, double avgAccAccSaber, int headset) {
        this.id = id;
        this.name = name;
        this.avatarURL = avatarURL;
        this.scoreSaber = scoreSaber;
        this.beatLeader = beatLeader;
        this.accSaber = accSaber;
        this.ppScoreSaber = ppScoreSaber;
        this.ppBeatLeader = ppBeatLeader;
        this.ap = ap;
        this.avgAccScoreSaber = avgAccScoreSaber;
        this.avgAccBeatLeader = avgAccBeatLeader;
        this.avgAccAccSaber = avgAccAccSaber;
        this.headset = headset;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAvatarURL() {
        return avatarURL;
    }

    public void setAvatarURL(String avatarURL) {
        this.avatarURL = avatarURL;
    }

    public List<Integer> getScoreSaber() {
        return scoreSaber;
    }

    public void setScoreSaber(List<Integer> scoreSaber) {
        this.scoreSaber = scoreSaber;
    }

    public List<Integer> getBeatLeader() {
        return beatLeader;
    }

    public void setBeatLeader(List<Integer> beatLeader) {
        this.beatLeader = beatLeader;
    }

    public List<Integer> getAccSaber() {
        return accSaber;
    }

    public void setAccSaber(List<Integer> accSaber) {
        this.accSaber = accSaber;
    }

    public float getPpScoreSaber() {
        return ppScoreSaber;
    }

    public void setPpScoreSaber(float ppScoreSaber) {
        this.ppScoreSaber = ppScoreSaber;
    }

    public double getPpBeatLeader() {
        return ppBeatLeader;
    }

    public void setPpBeatLeader(double ppBeatLeader) {
        this.ppBeatLeader = ppBeatLeader;
    }

    public double getAp() {
        return ap;
    }

    public void setAp(double ap) {
        this.ap = ap;
    }

    public double getAvgAccScoreSaber() {
        return avgAccScoreSaber;
    }

    public void setAvgAccScoreSaber(double avgAccScoreSaber) {
        this.avgAccScoreSaber = avgAccScoreSaber;
    }

    public double getAvgAccBeatLeader() {
        return avgAccBeatLeader;
    }

    public void setAvgAccBeatLeader(double avgAccBeatLeader) {
        this.avgAccBeatLeader = avgAccBeatLeader;
    }

    public double getAvgAccAccSaber() {
        return avgAccAccSaber;
    }

    public void setAvgAccAccSaber(double avgAccAccSaber) {
        this.avgAccAccSaber = avgAccAccSaber;
    }

    public int getHeadset() {
        return headset;
    }

    public void setHeadset(int headset) {
        this.headset = headset;
    }

    @Override
    public String toString() {
        return "PlayerData{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", avatarURL='" + avatarURL + '\'' +
                ", scoreSaber=" + scoreSaber +
                ", beatLeader=" + beatLeader +
                ", accSaber=" + accSaber +
                ", ppScoreSaber=" + ppScoreSaber +
                ", ppBeatLeader=" + ppBeatLeader +
                ", ap=" + ap +
                ", avgAccScoreSaber=" + avgAccScoreSaber +
                ", avgAccBeatLeader=" + avgAccBeatLeader +
                ", avgAccAccSaber=" + avgAccAccSaber +
                ", headsetName='" + headset + '\'' +
                '}';
    }
}
