package bot.commands.chart;

import bot.dto.MessageEventDTO;
import bot.dto.beatsavior.BeatSaviorPlayerScore;
import bot.main.BotConstants;
import bot.utils.ChartUtils;
import bot.utils.Messages;
import org.apache.commons.collections4.map.LinkedMap;
import org.knowm.xchart.XYChart;
import org.knowm.xchart.XYChartBuilder;
import org.knowm.xchart.XYSeries;
import org.knowm.xchart.XYSeries.XYSeriesRenderStyle;
import org.knowm.xchart.style.Styler.ChartTheme;
import org.knowm.xchart.style.Styler.LegendLayout;
import org.knowm.xchart.style.Styler.LegendPosition;
import org.knowm.xchart.style.XYStyler;
import org.knowm.xchart.style.markers.SeriesMarkers;

import java.awt.*;
import java.io.File;

public class AccuracyChart {

    private static final Color DISCORD_COLOR = new Color(49, 51, 56);

    public static void sendChartImage(BeatSaviorPlayerScore score, String playerName, String diffName, MessageEventDTO event) {
        XYChart chart = AccuracyChart.getAccuracyChart(score, playerName, diffName);
        String filename = BotConstants.RESOURCES_PATH + "accuracyChart_" + score.getPlayerID();
        ChartUtils.saveChart(chart, filename);
        File image = new File(filename + ".png");
        if (image.exists()) {
            Messages.sendImage(image, "accuracyChart_" + score.getPlayerID() + ".png", event);
            image.delete();
        }
    }

    private static XYChart getAccuracyChart(BeatSaviorPlayerScore score, String playerName, String diffName) {
        LinkedMap<String, Double> accuracyGraph = score.getTrackers().getScoreGraphTracker().getGraph();

        double[] accuracyValues = accuracyGraph.values().stream().mapToDouble(Double::doubleValue).toArray();
        double[] songTimeValues = accuracyGraph.keySet().stream().mapToDouble(Double::valueOf).toArray();

        // Create Chart
        XYChart chart = new XYChartBuilder()
                .width(BotConstants.chartWidth)
                .theme(ChartTheme.Matlab)
                .title("Accuracy Plot")
                .xAxisTitle("Time")
                .yAxisTitle("Accuracy")
                .build();

        Font font = new Font("Consolas", Font.BOLD, 20);
        Font titleFont = new Font("Consolas", Font.BOLD, 30);
        Font labelFont = new Font("Consolas", Font.BOLD, 20);
        Font labelTitleFont = new Font("Consolas", Font.BOLD, 22);
        Font legendFont = new Font("Consolas", Font.BOLD, 60);

        // Customize Chart
        XYStyler styler = chart.getStyler();

        styler.setDefaultSeriesRenderStyle(XYSeriesRenderStyle.Line)
                .setPlotGridLinesVisible(false)
                .setMarkerSize(15)
                .setPlotContentSize(.95)
                .setPlotBorderVisible(false)
                .setBaseFont(font)
                .setLegendFont(legendFont)
                .setChartTitleFont(titleFont);

        styler.setAxisTickLabelsFont(labelFont);
        styler.setAxisTitleFont(labelTitleFont);

        styler.setLegendPosition(LegendPosition.OutsideS)
                .setLegendLayout(LegendLayout.Horizontal);

        styler.setLegendSeriesLineLength(20)
                .setLegendBorderColor(DISCORD_COLOR)
                .setLegendBackgroundColor(DISCORD_COLOR)
                .setChartBackgroundColor(DISCORD_COLOR)
                .setPlotBackgroundColor(DISCORD_COLOR)
                .setChartFontColor(Color.WHITE);

        styler.setAxisTickLabelsColor(Color.WHITE);

        // Series
        XYSeries series = chart.addSeries(playerName + ": " + score.getSongName() + " (" + diffName + ")", songTimeValues, accuracyValues);
        series.setLineWidth(10);
        series.setMarker(SeriesMarkers.NONE);
        series.setLineColor(Color.RED);
        return chart;
    }
}
