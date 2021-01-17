package bot.graphics;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;

import bot.utils.Format;
import javafx.application.Application;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.Scene;
import javafx.scene.SnapshotParameters;
import javafx.scene.image.Image;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class AccuracyGrid extends Application {

	final int yOffset = 166;
	final int xOffset = 172;
	final int yStartOffset = 100;
	final int xStartOffset = 30;
	public static List<Float> accuracyValues = new ArrayList<Float>();
	private static List<Integer> notesCounts = new ArrayList<Integer>();
	private static String playerId = "";
	Stage mainStage;

	@Override
	public void start(Stage primaryStage) throws Exception {
		mainStage = primaryStage;
		
		Pane root = new Pane();
		for (int i = 0; i < 3; i++) {
			for (int j = 0; j < 4; j++) {
				float accValue = accuracyValues.get(i * 4 + j);
				String accuracy = "NaN".equals(String.valueOf(accValue)) ? "  /" : Format.decimal(accValue);
				String noteCount = String.valueOf(notesCounts.get(i * 4 + j));

				Text accText = new Text(j * xOffset + xStartOffset, (2-i) * yOffset + yStartOffset, accuracy);
				accText.setFont(Font.loadFont(getClass().getClassLoader().getResource("Consolas.ttf").openStream(), 35));
				Text countText = new Text(j * xOffset + 15, (2-i) * yOffset + 45, noteCount);
				countText.setFont(Font.loadFont(getClass().getClassLoader().getResource("Consolas.ttf").openStream(), 26));

				accText.setFill(getAccTextColor(accValue));
				root.getChildren().addAll(accText, countText);
			}
		}
		root.getStylesheets().addAll(this.getClass().getClassLoader().getResource("accgrid.css").toExternalForm());
		root.setId("grid");

		final SnapshotParameters snapPara = new SnapshotParameters();
		snapPara.setFill(Color.TRANSPARENT);

		Scene scene = new Scene(root, 687, 514);
		primaryStage.setScene(scene);
		primaryStage.setTitle("GridPane Beispiel");

		WritableImage image = scene.getRoot().snapshot(snapPara, null);
		saveFile(image, new File("src/main/resources/accGrid_"+getPlayerId()+".png"));
		primaryStage.close();
	}

	private Paint getAccTextColor(float accuracy) {
		if ("NaN".equals(String.valueOf(accuracy))) {
			return Color.BLACK;
		} else if (accuracy >= 113) {
			return Color.rgb(27, 181, 0);
		} else if (accuracy > 110) {
			return Color.rgb(24, 120, 7);
		} else if (accuracy > 106) {
			return Color.rgb(131, 138, 1);
		} else if (accuracy > 104) {
			return Color.rgb(230, 149, 0);
		} else if (accuracy > 102) {
			return Color.rgb(199, 110, 32);
		}
		return Color.rgb(181, 5, 5);
	}

	private void saveFile(Image content, File file) {
		try {
			BufferedImage bufferedImage = SwingFXUtils.fromFXImage(content, null);
			ImageIO.write(bufferedImage, "png", file);
		} catch (IOException ex) {
			ex.printStackTrace();
		}

	}

	public static void setAccuracyValues(List<Float> accuracyValuesIn) {
		accuracyValues = accuracyValuesIn;
	}

	public static List<Integer> getNotesCounts() {
		return notesCounts;
	}

	public static void setNotesCounts(List<Integer> notesCountsIn) {
		notesCounts = notesCountsIn;
	}

	public static String getPlayerId() {
		return playerId;
	}

	public static void setPlayerId(String playerIdIn) {
		playerId = playerIdIn;
	}

	
}
