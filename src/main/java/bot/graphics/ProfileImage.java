package bot.graphics;

import bot.dto.player.Player;
import bot.utils.JavaFXUtils;
import javafx.application.Application;
import javafx.scene.SnapshotParameters;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.awt.image.BufferedImage;
import java.io.File;

public class ProfileImage extends Application {
    public static Player player;
    public static BufferedImage qrCode;
    public static boolean isFinished = false;
    public static String filePath = "";

    final ImageView baseImage = new ImageView("https://i.imgur.com/WTmCg3c.png"); // Background Image

    @Override
    public void start(Stage primaryStage) {
        Pane root = new Pane();
        root.getChildren().add(baseImage);

        final SnapshotParameters snapPara = new SnapshotParameters();
        snapPara.setFill(Color.TRANSPARENT);
        WritableImage resultImage = root.snapshot(snapPara, null);

        JavaFXUtils.saveFile(resultImage, new File(getFilePath()));
        setFinished(true);
        primaryStage.close();
    }

    public static Player getPlayer() {
        return player;
    }

    public static void setPlayer(Player player) {
        ProfileImage.player = player;
    }

    public static BufferedImage getQrCode() {
        return qrCode;
    }

    public static void setQrCode(BufferedImage qrCode) {
        ProfileImage.qrCode = qrCode;
    }

    public static boolean isFinished() {
        return isFinished;
    }

    public static void setFinished(boolean isFinished) {
        ProfileImage.isFinished = isFinished;
    }

    public static String getFilePath() {
        return filePath;
    }

    public static void setFilePath(String filePath) {
        ProfileImage.filePath = filePath;
    }
}
