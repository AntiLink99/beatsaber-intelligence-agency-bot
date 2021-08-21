package bot.dto.beatsaviour;

import com.google.gson.annotations.SerializedName;

public class RankedMapDifficulty {
    @SerializedName(value = "bombs", alternate = {"Bombs"})
    int bombs;
    @SerializedName(value = "notes", alternate = {"Notes"})
    int notes;
    @SerializedName(value = "obstables", alternate = {"Obstables"})
    int obstables;
    @SerializedName(value = "njs", alternate = {"Njs"})
    double njs;
    @SerializedName(value = "njsOffset", alternate = {"NjsOffset"})
    double njsOffset;

    public int getBombs() {
        return bombs;
    }

    public void setBombs(int bombs) {
        this.bombs = bombs;
    }

    public int getNotes() {
        return notes;
    }

    public void setNotes(int notes) {
        this.notes = notes;
    }

    public int getObstables() {
        return obstables;
    }

    public void setObstables(int obstables) {
        this.obstables = obstables;
    }

    public double getNjs() {
        return njs;
    }

    public void setNjs(int njs) {
        this.njs = njs;
    }

    public double getNjsOffset() {
        return njsOffset;
    }

    public void setNjsOffset(int njsOffset) {
        this.njsOffset = njsOffset;
    }
}
