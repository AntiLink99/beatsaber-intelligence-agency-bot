package bot.chart;

import java.awt.Color;
import java.awt.Font;
import java.io.File;

import org.knowm.xchart.RadarChart;
import org.knowm.xchart.RadarChartBuilder;
import org.knowm.xchart.style.RadarStyler;
import org.knowm.xchart.style.Styler.LegendLayout;
import org.knowm.xchart.style.Styler.LegendPosition;

import bot.dto.MessageEventDTO;
import bot.dto.player.PlayerSkills;
import bot.main.BotConstants;
import bot.utils.ChartUtils;
import bot.utils.Messages;

public class RadarStatsChart {
	public static void sendChartImage(PlayerSkills skills, long memberId, MessageEventDTO event) {

		RadarChart chart = getRadarChart(skills);
		String filename = "src/main/resources/players";

		ChartUtils.saveChart(chart, filename);
		File image = new File(filename + ".png");
		if (image.exists()) {
			Messages.sendImage(image, "players.png", event.getChannel());
			image.delete();
		}
	}

	private static RadarChart getRadarChart(PlayerSkills skills) {
		double accuracy = Double.valueOf(skills.getAccuracy()) / 10d;
		double speed = Double.valueOf(skills.getSpeed()) / 10;
		double stamina = Double.valueOf(skills.getStamina()) / 10;
		double reading = Double.valueOf(skills.getReading()) / 10;

		RadarChart chart = new RadarChartBuilder().width(BotConstants.radarWidth).height(BotConstants.radarHeight).title("Player skills").build();

		Font font = new Font("Consolas", Font.BOLD, 40);
		Font titleFont = new Font("Consolas", Font.BOLD, 60);
		Font labelTitleFont = new Font("Consolas", Font.BOLD, 40);
		Font legendFont = new Font("Consolas", Font.BOLD, 40 / skills.getPlayerName().length() + 60);

		// Customize Chart
		RadarStyler styler = chart.getStyler();

		styler.setPlotGridLinesVisible(false);
		styler.setMarkerSize(15);
		styler.setPlotContentSize(.95);

		styler.setBaseFont(font);
		styler.setLegendFont(legendFont);
		styler.setChartTitleFont(titleFont);
		styler.setAxisTitleFont(labelTitleFont);
		styler.setDecimalPattern("######");

		styler.setLegendPosition(LegendPosition.OutsideS);
		styler.setLegendLayout(LegendLayout.Horizontal);
		styler.setLegendSeriesLineLength(20);
		styler.setLegendBorderColor(Color.DARK_GRAY);
		styler.setLegendBackgroundColor(Color.DARK_GRAY);

		styler.setChartBackgroundColor(Color.DARK_GRAY);
		styler.setChartFontColor(Color.WHITE);

		styler.setPlotBackgroundColor(Color.DARK_GRAY);
		styler.setPlotGridLinesColor(Color.WHITE);
		styler.setAnnotationsFontColor(Color.WHITE);

		styler.setSeriesColors(new Color[] { new Color(0f, 0.8f, 1f, 0.8f) });
		styler.setYAxisTitleColor(Color.WHITE);
		chart.setVariableLabels(new String[] { "Accuracy", "Speed", "Stamina", "Reading" });
		chart.addSeries(skills.getPlayerName(), new double[] { accuracy, speed, stamina, reading });
		return chart;
	}
}
