package bot.utils;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.GraphicsDevice;
import java.awt.GraphicsDevice.WindowTranslucency;
import java.awt.GraphicsEnvironment;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.JFrame;

import bot.dto.ScrapedMapDiff;
import bot.dto.SongScore;
import bot.main.BotConstants;

public class GraphicsUtils {

	public static Graphics2D drawSongRectangles(Graphics2D g2d, int x, int y, List<SongScore> scores) {

		GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
		GraphicsDevice gd = ge.getDefaultScreenDevice();
		if (gd.isWindowTranslucencySupported(WindowTranslucency.TRANSLUCENT)) {
			g2d.setBackground(Color.LIGHT_GRAY);
		}
		
		HashMap<String, BufferedImage> alreadyFetchedImages = new HashMap<>();
		for (int i = 0; scores.size() > i; i++) {
			SongScore score = scores.get(i);
			String coverURL = score.getCoverURL();
			BufferedImage image = null;
			if (!alreadyFetchedImages.containsKey(coverURL)) {
				try {
					if (coverURL == null) {
						continue;
					}
					URL url = new URL(coverURL);
					final HttpURLConnection connection = (HttpURLConnection) url.openConnection();
					connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_7_5) AppleWebKit/537.31 (KHTML, like Gecko) Chrome/26.0.1410.65 Safari/537.31");
					image = ImageIO.read(connection.getInputStream());
					alreadyFetchedImages.put(coverURL, image);
				} catch (IOException e) {
					e.printStackTrace();
				}
			} else {
				image = alreadyFetchedImages.get(coverURL);
			}
			if (image == null) {
				System.out.println("NULL");
				continue;
			}
			int relativeY = y + (BotConstants.songRectHeight + BotConstants.songRectYOffset) * i;

			g2d.setPaint(FontUtils.gradient(new Color(40, 40, 40), new Color(75, 75, 75)));
			g2d.fillRoundRect(x, relativeY, BotConstants.songRectWidth, BotConstants.songRectHeight, 25, 25);

			g2d.setFont(FontUtils.consolas(55));

			// Song Name
			g2d.setColor(Color.WHITE);
			g2d.setFont(score.getSongName().length() > 36 ? FontUtils.consolasBold(36 * 55 / score.getSongName().length()) : FontUtils.consolasBold(55));
			g2d.drawString(score.getSongName(), x + BotConstants.songRectHeight + 20, relativeY + relToHeight(0.22));

			// Author
			g2d.setFont(FontUtils.consolas(40));
			g2d.drawString(score.getLevelAuthorName(), x + BotConstants.songRectHeight + 20, relativeY + relToHeight(0.37));

			// Diff
			g2d.setFont(FontUtils.consolasBold(60));
			g2d.setColor(FontUtils.getDiffColor(score.getDifficulty()));
			g2d.drawString(score.getDifficultyName(), x + BotConstants.songRectHeight + 20, relativeY + relToHeight(0.88));

			// Cover
			g2d.setFont(FontUtils.consolasBold(30));
			g2d.drawImage(image.getScaledInstance(BotConstants.songRectHeight, BotConstants.songRectHeight, Image.SCALE_SMOOTH), x, relativeY, null);

			Color rankColor = Color.WHITE;
			switch (score.getRank()) {
			case 1:
				rankColor = new Color(255, 215, 0); // Gold
				break;
			case 2:
				rankColor = new Color(176, 184, 185); // Silber
				break;
			case 3:
				rankColor = new Color(204, 142, 52); // Bronze
				break;
			}

			// Rank
			g2d.setColor(rankColor);
			g2d.setFont(FontUtils.consolasBold(60));
			g2d.drawString(String.valueOf("#" + score.getRank()), x + relToWidth(0.9) - String.valueOf(score.getRank()).length() * 23, relativeY + relToHeight(0.45));

			if (score.getMaxScore() != 0) {
				// RANKED

				// PP Raw
				g2d.setColor(new Color(66, 245, 108));
				g2d.setFont(FontUtils.consolasBold(58));
				g2d.drawString(score.getPpString(), x + relToWidth(0.55), relativeY + relToHeight(0.72));

				// PP Weight
				g2d.setColor(new Color(24, 161, 56));
				g2d.setFont(FontUtils.consolasBold(42));
				g2d.drawString(score.getWeightPpString(), x + relToWidth(0.55), relativeY + relToHeight(0.88));

				// Accuracy
				g2d.setPaint(new Color(143, 193, 255));
				g2d.setFont(FontUtils.consolasBold(60));
				g2d.drawString(score.getAccuracyString(), x + relToWidth(0.82), relativeY + relToHeight(0.88));

				// Player leaderboard rank
				if (score.getWeight() != 0) {
					g2d.setFont(FontUtils.consolasBold(50));
					int rankOnPlayerLeaderboard = Format.roundDouble((Math.log10(score.getWeight()) + Math.log10(0.965)) / Math.log10(0.965));
					g2d.setPaint(rankOnPlayerLeaderboard == 1 ? new Color(255, 122, 82) : new Color(219, 219, 219));
					g2d.drawString(getScoreMessage(rankOnPlayerLeaderboard), x + relToWidth(0.82) - String.valueOf(rankOnPlayerLeaderboard).length() * 21, relativeY + relToHeight(0.66));
				}
				// Stern Rating
				g2d.setFont(FontUtils.consolasBold(50));
				g2d.setColor(new Color(252, 255, 71));
				List<ScrapedMapDiff> diffs = JsonUtils.getMapByHash(score.getSongHash()).getDiffs();
				String starRating = diffs.stream().filter(diff -> score.getDifficultyName().equals(diff.getDiff())).findFirst().get().getStar();
				g2d.drawString(starRating, relToWidth(0.195), relativeY + relToHeight(0.65));

				// Stern
				double scale = 0.75;
				int[] xStarCoords = { 42, 52, 72, 55, 60, 40, 15, 25, 9, 32, 42 };
				int[] yStarCoords = { 38, 60, 60, 80, 105, 90, 102, 80, 60, 60, 38 };
				for (int index = 0; index < yStarCoords.length; index++) {
					yStarCoords[index] = (int) (yStarCoords[index] * scale + relativeY + relToHeight(0.37));
					xStarCoords[index] = (int) (xStarCoords[index] * scale) + starRating.length() * 21 + relToWidth(0.22);
				}
				g2d.setColor(new Color(240, 240, 0));
				g2d.fillPolygon(xStarCoords, yStarCoords, 11);

			}
			// Outline
			g2d.setColor(new Color(25, 25, 25));
			g2d.setStroke(new BasicStroke(10f));
			g2d.drawRoundRect(x, relativeY, BotConstants.songRectWidth, BotConstants.songRectHeight, 25, 25);
		}
		return g2d;
	}

	public static File saveFrameScreenshot(JFrame frame, String filePath) {
		BufferedImage img = new BufferedImage(frame.getWidth(), frame.getHeight(), BufferedImage.TYPE_INT_ARGB);
		Graphics2D g2d = img.createGraphics();
		frame.printAll(g2d);
		g2d.dispose();

		File outputFile = new File(filePath);
		try {
			ImageIO.write(img, "png", outputFile);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return outputFile;
	}

	private static int relToHeight(double value) {
		return (int) (BotConstants.songRectHeight * value);
	}

	private static int relToWidth(double value) {
		return (int) (BotConstants.songRectWidth * value);
	}

	private static String getSuffix(final int n) {
		String[] sufixes = new String[] { "th", "st", "nd", "rd", "th", "th", "th", "th", "th", "th" };
		switch (n % 100) {
		case 11:
		case 12:
		case 13:
			return "th";
		default:
			return sufixes[n % 10];

		}
	}

	private static String getScoreMessage(int rankOnPlayerLeaderboard) {
		if (rankOnPlayerLeaderboard == 1) {
			return "Top play";
		}
		String suffix = getSuffix(rankOnPlayerLeaderboard);
		return rankOnPlayerLeaderboard + suffix + " b.p.";
	}
}
