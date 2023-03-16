package bot.commands.chart;

import bot.dto.MessageEventDTO;
import bot.dto.player.PlayerSkills;
import bot.main.BotConstants;
import bot.utils.ChartUtils;
import bot.utils.Messages;
import org.knowm.xchart.RadarChart;
import org.knowm.xchart.RadarChartBuilder;
import org.knowm.xchart.style.RadarStyler;
import org.knowm.xchart.style.Styler.LegendLayout;
import org.knowm.xchart.style.Styler.LegendPosition;

import java.awt.*;
import java.io.File;

public class RadarStatsChart {
    public void sendChartImage(PlayerSkills skills, MessageEventDTO event) {

        RadarChart chart = getRadarChart(skills);
        String filename = BotConstants.RESOURCES_PATH+"players";

        ChartUtils.saveChart(chart, filename);
        File image = new File(filename + ".png");
        if (image.exists()) {
            Messages.sendImage(image, "players.png", event);
            image.delete();
        }
    }

    private RadarChart getRadarChart(PlayerSkills skills) {
        double accuracy = (double) skills.getAccuracy() / 10d;
        double speed = (double) skills.getSpeed() / 10;
        double stamina = (double) skills.getStamina() / 10;
        double reading = (double) skills.getReading() / 10;

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

        styler.setSeriesColors(new Color[]{new Color(0f, 0.8f, 1f, 0.8f)});
        styler.setYAxisTitleColor(Color.WHITE);
        chart.setVariableLabels(new String[]{"Accuracy", "Speed", "Stamina", "Reading"});
        chart.addSeries(skills.getPlayerName(), new double[]{accuracy, speed, stamina, reading});
        return chart;
    }
}
