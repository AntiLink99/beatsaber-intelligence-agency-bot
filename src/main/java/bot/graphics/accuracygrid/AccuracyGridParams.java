package bot.graphics.accuracygrid;

import java.util.List;

public class AccuracyGridParams {
    private List<Float> gridAcc;
    private List<Integer> notesCounts;
    private String customImageUrl;
    private String filePath;

    public AccuracyGridParams(List<Float> gridAcc, List<Integer> notesCounts, String customImageUrl, String filePath) {
        this.gridAcc = gridAcc;
        this.notesCounts = notesCounts;
        this.customImageUrl = customImageUrl;
        this.filePath = filePath;
    }

    public List<Float> getGridAcc() {
        return gridAcc;
    }

    public void setGridAcc(List<Float> gridAcc) {
        this.gridAcc = gridAcc;
    }

    public List<Integer> getNotesCounts() {
        return notesCounts;
    }

    public void setNotesCounts(List<Integer> notesCounts) {
        this.notesCounts = notesCounts;
    }

    public String getCustomImageUrl() {
        return customImageUrl;
    }

    public void setCustomImageUrl(String customImageUrl) {
        this.customImageUrl = customImageUrl;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }
}
