package bot.dto.beatsaviour;

public class RankedMapCharacteristics {
    String name;
    RankedMapCharacteristicsDifficulties difficulties;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public RankedMapCharacteristicsDifficulties getDifficulties() {
        return difficulties;
    }

    public void setDifficulties(RankedMapCharacteristicsDifficulties difficulties) {
        this.difficulties = difficulties;
    }
}
