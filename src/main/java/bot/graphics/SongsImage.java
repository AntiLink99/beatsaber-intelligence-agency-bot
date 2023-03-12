package bot.graphics;

import bot.api.ApiConstants;
import bot.api.HttpMethods;
import bot.dto.LeaderboardService;
import bot.dto.LeaderboardServicePlayer;
import bot.dto.PlayerScore;
import bot.utils.*;
import javafx.application.Application;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.SnapshotParameters;
import javafx.scene.effect.DropShadow;
import javafx.scene.effect.Glow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.StrokeType;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.awt.image.BufferedImage;
import java.io.File;
import java.util.List;
import java.util.Objects;

//This code sucks, please don't look
public class SongsImage extends Application {

    private static boolean isFinished = false;
    private static List<PlayerScore> scores;
    private static LeaderboardServicePlayer player;
    private static String filePath;
    final ImageView baseImage = new ImageView("https://anti.link/img/scoresImage.png"); // Rectangle Image
    final Image starImage = new Image(Objects.requireNonNull(getClass().getClassLoader().getResourceAsStream("star.png")));

    @Override
    public void start(Stage primaryStage) throws Exception {
        setFinished(false);

        final boolean isScoreSaber = LeaderboardService.SCORESABER.equals(scores.get(0).getService());
        final String leaderboardIconUrl = isScoreSaber ? "https://anti.link/img/scoresaber.png" : "https://anti.link/img/beatleader.png";
        final ImageView leaderboardImage = new ImageView(new Image(leaderboardIconUrl ,200, 200, false, false));

        Pane root = new Pane();
        root.getChildren().add(baseImage);

        DropShadow textShadow = new DropShadow();
        textShadow.setColor(Color.BLACK);
        textShadow.setSpread(0.4);
        textShadow.setRadius(40);

        for (int i = 0; i < scores.size(); i++) {
            PlayerScore score = scores.get(i);
            String songName = score.getSongName();
            String author = score.getAuthorName();
            int rank = score.getRank();
            Color rankColor = FontUtils.getRankColor(rank, Color.WHITE);
            String pp = score.getPPString();
            String ppWeight = score.getWeightedPPString();
            String diff = score.getDifficultyName();
            String acc = score.getAccuracyString();
            float stars = score.getStars();
            String relativeTime = score.getRelativeTimeString();
            boolean isRanked = score.isRanked();
            int rankOnPlayerLeaderboard = isRanked && score.getWeight() > 0 ? Format.roundDouble((Math.log10(score.getWeight()) + Math.log10(0.965)) / Math.log10(0.965)) : -1;
            Color playerRankShadowColor = FontUtils.getRankColor(rankOnPlayerLeaderboard, Color.WHITE);

            // Cover Image
            ImageView cover = new ImageView(); // Custom Image
            String coverUrl = WebUtils.isURL(score.getCoverURL()) ? score.getCoverURL() : ApiConstants.NO_AVATAR_URL;
            BufferedImage coverImage;
            try {
                coverImage = HttpMethods.getBufferedImagefromUrl(coverUrl);
            } catch (Exception e) {
                coverImage = HttpMethods.getBufferedImagefromUrl(ApiConstants.NO_AVATAR_URL);
            }
            cover.setImage(SwingFXUtils.toFXImage(coverImage, null));
            cover.setOpacity(1);
            cover.setPreserveRatio(true);
            int coverSize = 234;
            cover.setFitHeight(coverSize);

            int coverStartYOffset = 10;
            int coverYOffset = 41;
            cover.setTranslateY(i * (coverSize + coverYOffset) + coverStartYOffset);
            cover.setTranslateX(10);

            // Shader Lights Cover Image
            boolean topThreeRank = rank <= 3;
            DropShadow coverShadow = new DropShadow();
            coverShadow.setColor(topThreeRank ? rankColor : Color.BLACK);
            coverShadow.setSpread(topThreeRank ? 1 : 0.9);
            Glow glow = new Glow();
            glow.setLevel(topThreeRank ? 0.5 : 0.25);
            coverShadow.setInput(glow);
            cover.setEffect(coverShadow);

            root.getChildren().add(cover);

            int rightTextsX = 265;
            int itemStartYOffset = 65;
            int itemYOffset = 275;
            int rightTextsY = i * itemYOffset + itemStartYOffset;
            // Songname
            Text songNameText = new Text(rightTextsX, rightTextsY - 10, songName);
            songNameText.setFont(songName.length() > 36 ? FontUtils.consolasBold(36 * 55f / score.getSongName().length()) : FontUtils.consolasBold(55));
            songNameText.setFill(Color.WHITE);
            songNameText.setEffect(textShadow);

            // Author
            Text authorText = new Text(rightTextsX, rightTextsY + 35, author);
            authorText.setFont(FontUtils.consolas(40));
            authorText.setFill(Color.WHITE);
            authorText.setEffect(textShadow);

            // Relative Time
            boolean veryLongTitle = songName.length() > 26;
            Text relTime = new Text(rightTextsX - relativeTime.length() * 28 + 1170 + (veryLongTitle ? 18 : 0), rightTextsY + (veryLongTitle ? 45 : -10), relativeTime);
            relTime.setFont(FontUtils.consolasBold(veryLongTitle ? 38 : 40));
            relTime.setFill(Color.LIGHTGREY);
            relTime.setEffect(textShadow);

            // Diff
            Color diffColor = FontUtils.getDiffColor(score.getDifficultyValue());
            Text diffText = new Text(rightTextsX, rightTextsY + 160, diff);
            diffText.setFont(FontUtils.consolasBold(60));
            diffText.setFill(diffColor);
            diffText.setEffect(textShadow);

            // Accuracy
            Text accText = new Text(rightTextsX + 880 + (veryLongTitle ? 50 : 0), rightTextsY + 76 + (veryLongTitle ? 30 : 0), acc);
            accText.setFont(FontUtils.consolasBold(veryLongTitle ? 55 : 70));
            accText.setFill(Color.WHITE);
            accText.setEffect(textShadow);

            // Rank
            Text rankText = new Text(rightTextsX + 980 + (veryLongTitle ? 30 : 0), rightTextsY + 150 + (veryLongTitle ? 10 : 0), "#" + rank);

            if (rank < 100) {
                rankText.setX(rankText.getX() + (veryLongTitle ? 22 * (3 - String.valueOf(rank).length()) : 26 * (3 - String.valueOf(rank).length())));
            } else if (rank > 999) {
                rankText.setX(rankText.getX() - String.valueOf(rank).length() * 8);
            }
            rankText.setFont(FontUtils.consolasBold(veryLongTitle ? 45 : 60));
            rankText.setFill(rankColor);
            rankText.setEffect(textShadow);

            root.getChildren().addAll(songNameText, authorText, diffText, accText, rankText, relTime);

            // RANKED
            if (isRanked) {
                // Star Text
                Text starsText = new Text(rightTextsX, rightTextsY + 100, Format.decimal(stars));
                starsText.setFont(FontUtils.consolasBold(50));
                starsText.setFill(Color.YELLOW);
                starsText.setEffect(textShadow);

                // Star Drawing
                ImageView star = new ImageView(starImage); // Star Image, Bug fix
                star.setX(275 + (Format.decimal(stars).length() == 4 ? 10 : 40));
                star.setY(i * itemYOffset + itemStartYOffset - 35);
                star.setScaleX(0.2);
                star.setScaleY(0.2);
                star.setOpacity(1);
                star.setPreserveRatio(true);
                star.setFitHeight(coverSize);
                DropShadow starShadow = new DropShadow();
                starShadow.setColor(i < 5 ? Color.BLACK : Color.YELLOW);
                starShadow.setSpread(i < 5 ? 0.4 : 1);
                starShadow.setRadius(i < 5 ? 50 : 0);
                star.setEffect(starShadow);

                // PP
                Text ppText = new Text(rightTextsX + 500, rightTextsY + 115, pp);
                ppText.setFont(FontUtils.consolasBold(58));
                ppText.setFill(Color.rgb(66, 245, 108));
                ppText.setEffect(textShadow);

                // PP Weight
                Text ppWeightText = new Text(rightTextsX + 520, rightTextsY + 160, ppWeight);
                ppWeightText.setFont(FontUtils.consolasBold(45));
                ppWeightText.setFill(Color.rgb(24, 161, 56));
                ppWeightText.setEffect(textShadow);

                // Leaderboard
                Text leaderboardText = new Text(25, rightTextsY - 20, rankOnPlayerLeaderboard + ".");
                boolean isTopTenPlayerScore = rankOnPlayerLeaderboard <= 10;
                boolean isTopThreePlayerScore = rankOnPlayerLeaderboard <= 3;

                int playerRankYOffset = isTopTenPlayerScore ? (11 - rankOnPlayerLeaderboard) * -2 : 0;
                leaderboardText.setY(leaderboardText.getY() - playerRankYOffset);
                int additionalFontSize = isTopTenPlayerScore ? (11 - rankOnPlayerLeaderboard) * 2 : 0;

                leaderboardText.setFont(FontUtils.consolasBold(30 + additionalFontSize));
                Color playerRankTextColor = isTopThreePlayerScore ? Color.BLACK : Color.WHITE;
                leaderboardText.setFill(playerRankTextColor);
                leaderboardText.setStroke(playerRankShadowColor);
                leaderboardText.setStroke(Color.BLACK);
                leaderboardText.setStrokeType(StrokeType.OUTSIDE);
                leaderboardText.setStrokeWidth(isTopThreePlayerScore ? 2 : 5);

                DropShadow leaderboardTextShadow = new DropShadow();
                leaderboardTextShadow.setColor(playerRankShadowColor);
                leaderboardTextShadow.setSpread(isTopThreePlayerScore ? 0.9 : 0.6);
                leaderboardTextShadow.setRadius(10);
                if (isTopThreePlayerScore) {
                    Glow playerRankFlow = new Glow();
                    playerRankFlow.setLevel(0.5);
                    leaderboardTextShadow.setInput(glow);
                }
                leaderboardText.setEffect(leaderboardTextShadow);

                root.getChildren().addAll(starsText, star, ppText, ppWeightText, leaderboardText);
                float SCALE = 1f;
                root.setScaleY(SCALE);
                root.setScaleX(SCALE);

            }
        }

        leaderboardImage.setTranslateX(1480);
        leaderboardImage.setTranslateY(30);

        float LEADERBOARD_SCALE = 1.4f;
        leaderboardImage.setScaleX(LEADERBOARD_SCALE);
        leaderboardImage.setScaleY(LEADERBOARD_SCALE);

        root.getChildren().add(leaderboardImage);

        float PROFILE_SCALE = 1.4f;
        String profilePic = player.getAvatar();
        ImageView profilePicView = new ImageView(profilePic);
        profilePicView.setTranslateX(1485);
        profilePicView.setTranslateY(350);
        profilePicView.setScaleX(PROFILE_SCALE);
        profilePicView.setScaleY(PROFILE_SCALE);
        root.getChildren().add(profilePicView);

        BufferedImage qrCode = GraphicsUtils.generateQRCode(player.getProfileURL(), 512, 512);
        if (qrCode != null) {
            float QR_SCALE = 0.505f;
            ImageView qrImageView = new ImageView(SwingFXUtils.toFXImage(qrCode, null));
            qrImageView.setTranslateX(1320);
            qrImageView.setTranslateY(510);
            qrImageView.setScaleX(QR_SCALE);
            qrImageView.setScaleY(QR_SCALE);
            root.getChildren().add(qrImageView);
        }

        root.autosize();
        final SnapshotParameters snapPara = new SnapshotParameters();
        snapPara.setFill(Color.TRANSPARENT);
        WritableImage resultImage = root.snapshot(snapPara, null);

        JavaFXUtils.saveFile(resultImage, new File(getFilePath()));
        setFinished(true);
        primaryStage.close();
    }

    public static boolean isFinished() {
        return isFinished;
    }

    public static void setFinished(boolean isFinished) {
        SongsImage.isFinished = isFinished;
    }

    public static List<PlayerScore> getScores() {
        return scores;
    }

    public static void setScores(List<PlayerScore> scores) {
        SongsImage.scores = scores;
    }

    public static String getFilePath() {
        return filePath;
    }

    public static void setFilePath(String filePath) {
        SongsImage.filePath = filePath;
    }

    public static LeaderboardServicePlayer getPlayer() {
        return player;
    }

    public static void setPlayer(LeaderboardServicePlayer player) {
        SongsImage.player = player;
    }
}
