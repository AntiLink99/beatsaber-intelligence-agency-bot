package bot.graphics;

import java.io.File;
import java.util.List;

import javax.swing.JFrame;

import bot.dto.SongScore;
import bot.main.BotConstants;
import bot.utils.FontUtils;

public class SongsFrame extends JFrame {

	private static final long serialVersionUID = 1L;
	private File recentSongsImage;

	public SongsFrame(List<SongScore> scores) {
		this.setResizable(false);
		this.setUndecorated(true);
		this.setBackground(FontUtils.translucent());
		this.setEnabled(false);
		this.setSize(BotConstants.songRectWidth + 20, BotConstants.songRectHeight * scores.size() + 200);
		this.setDefaultCloseOperation(EXIT_ON_CLOSE);
		SongRectanglesPanel graphics = new SongRectanglesPanel();
		graphics.setScores(scores);
		this.add(graphics);
		this.setVisible(true);
	}

	public File getRecentSongsImage() {
		return recentSongsImage;
	}

	public void setRecentSongsImage(File recentSongsImage) {
		this.recentSongsImage = recentSongsImage;
	}
}
