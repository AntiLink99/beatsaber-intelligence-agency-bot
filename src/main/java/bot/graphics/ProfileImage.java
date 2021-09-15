package bot.graphics;

import bot.api.ApiConstants;
import bot.api.HttpMethods;
import bot.chart.PlayerChart;
import bot.dto.player.Player;
import bot.utils.JavaFXUtils;
import bot.utils.WebUtils;
import javafx.application.Application;
import javafx.embed.swing.SwingFXUtils;
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
    public static BufferedImage qrCodeImage;
    public static boolean isFinished = false;
    public static String filePath = "";

    final ImageView baseImage = new ImageView("https://i.imgur.com/WTmCg3c.png"); // Background Image

    @Override
    public void start(Stage primaryStage) throws Exception {
        Pane root = new Pane();
        root.getChildren().add(baseImage);

        //QR Code
        ImageView qrCodeView = new ImageView();
        qrCodeView.setImage(SwingFXUtils.toFXImage(qrCodeImage, null));
        qrCodeView.setPreserveRatio(true);

        double qrCodeXPos = baseImage.getImage().getWidth() - qrCodeView.getImage().getWidth() - GraphicsConstants.greyBorderWidth;
        double qrCodeYPos = baseImage.getImage().getHeight() - qrCodeView.getImage().getHeight() - GraphicsConstants.greyBorderWidth;
        qrCodeView.setTranslateX(qrCodeXPos);
        qrCodeView.setTranslateY(qrCodeYPos);
        root.getChildren().add(qrCodeView);

        //Player
        String playerPictureUrl = ApiConstants.SS_PRE_URL + player.getAvatar();
        if (!WebUtils.isURL(playerPictureUrl)) {
            playerPictureUrl = ApiConstants.NO_AVATAR_URL;
        }
        BufferedImage playerImage;
        try {
            playerImage = HttpMethods.getBufferedImagefromUrl(playerPictureUrl);
        } catch (Exception e) {
            playerImage = HttpMethods.getBufferedImagefromUrl(ApiConstants.NO_AVATAR_URL);
        }
        ImageView playerPictureView = new ImageView();
        playerPictureView.setImage(SwingFXUtils.toFXImage(playerImage, null));
        playerPictureView.setPreserveRatio(true);

        playerPictureView.setTranslateX(GraphicsConstants.greyBorderWidth);
        playerPictureView.setTranslateY(GraphicsConstants.greyBorderWidth);
        playerPictureView.setFitHeight(GraphicsConstants.playerPictureHeight);
        root.getChildren().add(playerPictureView);

        //PlayerChart
        BufferedImage chartImage = new PlayerChart().getPlayerChartImage(player);
        ImageView playerChartView = new ImageView();
        playerChartView.setImage(SwingFXUtils.toFXImage(chartImage, null));
        playerChartView.setPreserveRatio(true);
        playerChartView.setFitWidth(GraphicsConstants.playerChartWidth);

        double playerChartXPos = baseImage.getImage().getWidth() - GraphicsConstants.playerChartWidth - GraphicsConstants.greyBorderWidth;
        playerChartView.setTranslateX(playerChartXPos);
        playerChartView.setTranslateY(GraphicsConstants.greyBorderWidth);

        root.getChildren().add(playerChartView);

        //Save
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
        return qrCodeImage;
    }

    public static void setQrCode(BufferedImage qrCode) {
        ProfileImage.qrCodeImage = qrCode;
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
