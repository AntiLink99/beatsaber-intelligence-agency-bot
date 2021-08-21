package bot.utils;

import javafx.scene.paint.Color;
import javafx.scene.text.Font;

public class FontUtils {

    public static Font consolas(double size) {
        return Font.loadFont(FontUtils.class.getClassLoader().getResourceAsStream("Consolas.ttf"), size);
    }

    public static Font consolasBold(double size) {
        return Font.loadFont(FontUtils.class.getClassLoader().getResourceAsStream("ConsolasBold.ttf"), size);
    }

    public static Color getDiffColor(int difficulty) {
        switch (difficulty) {
            case 1:
                return Color.rgb(60, 179, 113);
            case 3:
                return Color.rgb(89, 176, 231);
            case 5:
                return Color.rgb(255, 99, 70);
            case 7:
                return Color.rgb(191, 42, 65);
            case 9:
                return Color.rgb(143, 72, 219);
            default:
                return Color.WHITE;
        }
    }

    public static Color getRankColor(int rank, Color fallback) {
        Color rankColor = fallback;
        switch (rank) {
            case 1:
                rankColor = Color.rgb(255, 215, 0); // Gold
                break;
            case 2:
                rankColor = Color.rgb(176, 184, 185); // Silver
                break;
            case 3:
                rankColor = Color.rgb(204, 142, 52); // Bronze
                break;
        }
        return rankColor;
    }
}
