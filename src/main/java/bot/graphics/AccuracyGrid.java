package bot.graphics;

import bot.api.HttpMethods;
import bot.utils.Format;
import javafx.application.Application;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.SnapshotParameters;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.PixelReader;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class AccuracyGrid extends Application {

    public static List<Float> accuracyValues = new ArrayList<>();
    private static List<Integer> notesCounts = new ArrayList<>();
    private static String playerId = "";
    private static String messageId = "";
    private static String customImageUrl = "";
    private static boolean isFinished = false;
    Stage mainStage;
    final ImageView grid = new ImageView("https://i.imgur.com/8Y3FNri.png"); // Grid Image

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

    public static String getMessageId() {
        return messageId;
    }

    public static void setMessageId(String messageId) {
        AccuracyGrid.messageId = messageId;
    }

    public static String getCustomImageUrl() {
        return customImageUrl;
    }

    public static void setCustomImageUrl(String customImageUrl) {
        AccuracyGrid.customImageUrl = customImageUrl;
    }

    public static boolean isFinished() {
        return isFinished;
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        isFinished = false;
        mainStage = primaryStage;

        Pane root = new Pane();
        root.getChildren().add(grid);

        boolean hasCustomImage = getCustomImageUrl() != null && !getCustomImageUrl().isEmpty();
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

        saveFile(resultImage, new File("src/main/resources/accGrid_" + getPlayerId() + "_" + getMessageId() + ".png"));
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
            isFinished = true;
        } catch (IOException ex) {
            ex.printStackTrace();
        }

    }
}
