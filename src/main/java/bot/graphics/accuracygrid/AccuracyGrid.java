package bot.graphics.accuracygrid;

import bot.api.HttpMethods;
import bot.graphics.GraphicsConstants;
import bot.utils.Format;
import bot.utils.JavaFXUtils;
import javafx.application.Application;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.SnapshotParameters;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.ImageView;
import javafx.scene.image.PixelReader;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

public class AccuracyGrid extends Application {

    public static List<Float> accuracyValues = new ArrayList<>();
    private static List<Integer> notesCounts = new ArrayList<>();
    private static String customImageUrl = "";
    private static String filePath = "";
    private static boolean isFinished = false;

    final ImageView grid = new ImageView("https://i.imgur.com/8Y3FNri.png"); // Grid Image

    public AccuracyGrid(AccuracyGridParams params) {
        accuracyValues = params.getGridAcc();
        notesCounts = params.getNotesCounts();
        customImageUrl = params.getCustomImageUrl();
        filePath = params.getFilePath();
    }

    @Override
    public void start(Stage primaryStage) throws IOException, ExecutionException, InterruptedException, TimeoutException {
        isFinished = false;

        Pane root = new Pane();
        root.getChildren().add(grid);

        boolean hasCustomImage = customImageUrl != null && !customImageUrl.isEmpty();
        if (hasCustomImage) {
            ImageView background = new ImageView(); // Custom Image
            BufferedImage image = HttpMethods.getBufferedImagefromUrl(customImageUrl);
            background.setImage(SwingFXUtils.toFXImage(image, null));
            background.setOpacity(0.5);
            background.setPreserveRatio(true);

            double bgHeight = image.getHeight();
            double bgWidth = image.getWidth();
            if (bgHeight >= bgWidth) {
                background.setFitWidth(GraphicsConstants.recentSongsWidth);
            } else {
                background.setFitHeight(GraphicsConstants.recentSongsHeight);
            }

            ColorAdjust colorAdjust = new ColorAdjust();
            colorAdjust.setBrightness(-0.5);
            background.setEffect(colorAdjust);

            root.getChildren().add(background);
        }
        DropShadow accShadow = new DropShadow();
        accShadow.setColor(Color.WHITE);
        accShadow.setSpread(0.85);
        accShadow.setRadius(30);

        DropShadow countShadow = new DropShadow();
        countShadow.setColor(Color.WHITE);
        countShadow.setSpread(0.8);
        countShadow.setRadius(15);

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 4; j++) {
                float accValue = accuracyValues.get(i * 4 + j);
                String accuracy = "NaN".equals(String.valueOf(accValue)) ? "  /" : Format.decimal(accValue);
                String noteCount = String.valueOf(notesCounts.get(i * 4 + j));

                Text accText = new Text(j * GraphicsConstants.accGridXOffset + GraphicsConstants.accGridXStartOffset, (2 - i) * GraphicsConstants.accGridYOffset + GraphicsConstants.accGridYStartOffset, accuracy);
                accText.setFont(Font.loadFont(Objects.requireNonNull(getClass().getClassLoader().getResource("Consolas.ttf")).openStream(), 35));
                accText.setFill(Color.WHITE);

                Text countText = new Text(j * GraphicsConstants.accGridXOffset + 15, (2 - i) * GraphicsConstants.accGridYOffset + 45, noteCount);
                countText.setFont(Font.loadFont(Objects.requireNonNull(Objects.requireNonNull(getClass().getClassLoader().getResource("Consolas.ttf"))).openStream(), 26));

                if (hasCustomImage) {
                    accText.setEffect(accShadow);
                    countText.setEffect(countShadow);
                }
                accText.setFill(getAccTextColor(accValue));
                root.getChildren().addAll(accText, countText);
            }
        }

        final SnapshotParameters snapPara = new SnapshotParameters();
        snapPara.setFill(Color.TRANSPARENT);
        WritableImage resultImage = root.snapshot(snapPara, null);

        // Cut to size
        PixelReader reader = resultImage.getPixelReader();
        resultImage = new WritableImage(reader, 0, 0, GraphicsConstants.accGridWidth, GraphicsConstants.accGridHeight);

        JavaFXUtils.saveFile(resultImage, new File(filePath));
        isFinished = true;
        primaryStage.close();
        accuracyValues.clear();
        notesCounts.clear();
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

    public boolean isFinished() {
        return isFinished;
    }
}
