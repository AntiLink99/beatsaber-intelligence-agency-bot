package bot.utils;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontFormatException;
import java.io.IOException;

public class FontUtils {

	public static Font consolas(int size) {
		try {
			Font font = Font.createFont(Font.PLAIN, FontUtils.class.getClassLoader().getResourceAsStream("Consolas.ttf"));
			return font.deriveFont(Float.valueOf(size));
		} catch (FontFormatException e) {
			// TODO Auto-generated catch block
			System.out.println("Reg");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	public static Color getDiffColor(int difficulty) {
		switch (difficulty) {
		case 1:
			return new Color(60, 179, 113);
		case 3:
			return new Color(89, 176, 231);
		case 5:
			return new Color(255, 99, 70);
		case 7:
			return new Color(191, 42, 65);
		case 9:
			return new Color(143, 72, 219);
		default:
			return Color.WHITE;
		}
	}
}
