package bot.dto;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

import javax.imageio.ImageIO;

public class SongScore {
	private int rank;
	private long scoreId;
	private long score;
	private float pp;
	private float weight;
	private int maxScore;
	private int difficulty;
	private String songHash;
	private String songName;
	private String songSubName;
	private String songAuthorName;
	private String levelAuthorName;
	private String coverURL;
	private BufferedImage fetchedCover;

	private transient DecimalFormat format;

	public SongScore() {
		format = new DecimalFormat("###.##");
		format.setDecimalFormatSymbols(DecimalFormatSymbols.getInstance(Locale.ENGLISH));
	}

	public int getRank() {
		return rank;
	}

	public void setRank(int rank) {
		this.rank = rank;
	}

	public long getScoreId() {
		return scoreId;
	}

	public void setScoreId(long scoreId) {
		this.scoreId = scoreId;
	}

	public long getScore() {
		return score;
	}

	public void setScore(long score) {
		this.score = score;
	}

	public float getPp() {
		return pp;
	}

	public void setPp(float pp) {
		this.pp = pp;
	}

	public float getWeight() {
		return weight;
	}

	public String getPpString() {
		return format.format(getPp()) + "PP";
	}

	public void setWeight(float weight) {
		this.weight = weight;
	}

	public String getWeightPpString() {
		return "(" + format.format(getPp() * getWeight()) + "PP)";
	}

	public String getSongName() {
		return songName;
	}

	public void setSongName(String songName) {
		this.songName = songName;
	}

	public String getSongSubName() {
		return songSubName;
	}

	public void setSongSubName(String songSubName) {
		this.songSubName = songSubName;
	}

	public String getSongAuthorName() {
		return songAuthorName;
	}

	public void setSongAuthorName(String songAuthorName) {
		this.songAuthorName = songAuthorName;
	}

	public String getLevelAuthorName() {
		return levelAuthorName;
	}

	public void setLevelAuthorName(String levelAuthorName) {
		this.levelAuthorName = levelAuthorName;
	}

	public String getCoverURL() {
		return coverURL;
	}

	public void setCoverURL(String coverURL) {
		this.coverURL = coverURL;
	}

	public String getSongHash() {
		return songHash;
	}

	public void setSongHash(String songHash) {
		this.songHash = songHash;
	}

	public int getMaxScore() {
		return maxScore;
	}

	public void setMaxScore(int maxScore) {
		this.maxScore = maxScore;
	}

	public double getAccuracy() {
		return Double.valueOf(score) / Double.valueOf(maxScore);
	}

	public String getAccuracyString() {
		return format.format(getAccuracy() * 100d) + "%";
	}

	public int getDifficulty() {
		return difficulty;
	}

	public void setDifficulty(int difficulty) {
		this.difficulty = difficulty;
	}

	public String getDifficultyName() {
		switch (difficulty) {
		case 1:
			return "Easy";
		case 3:
			return "Normal";
		case 5:
			return "Hard";
		case 7:
			return "Expert";
		case 9:
			return "Expert+";
		default:
			return "???";
		}
	}

	public BufferedImage fetchCover() {
		try {
			if (coverURL == null) {
				return null;
			}
			System.out.println("Fetching cover "+coverURL);
			URL url = new URL(coverURL);
			final HttpURLConnection connection = (HttpURLConnection) url.openConnection();
			connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_7_5) AppleWebKit/537.31 (KHTML, like Gecko) Chrome/26.0.1410.65 Safari/537.31");
			BufferedImage image = ImageIO.read(connection.getInputStream());
			setFetchedCover(image);
			connection.disconnect();
			return image;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	public BufferedImage getFetchedCover() {
		return fetchedCover;
	}

	public void setFetchedCover(BufferedImage fetchedCover) {
		this.fetchedCover = fetchedCover;
	}
}
