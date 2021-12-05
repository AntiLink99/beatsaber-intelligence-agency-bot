package bot.chart;

import bot.dto.MessageEventDTO;
import bot.dto.beatsaviour.BeatSaviourPlayerScore;
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
    public static void sendChartImage(BeatSaviourPlayerScore score, String playerName, String diffName, MessageEventDTO event) {

        XYChart chart = AccuracyChart.getAccuracyChart(score, playerName, diffName);
        String filename = BotConstants.RESOURCES_PATH+"accuracyChart_" + score.getPlayerID();
        ChartUtils.saveChart(chart, filename);
        File image = new File(filename + ".png");
        if (image.exists()) {
            Messages.sendImage(image, "accuracyChart_" + score.getPlayerID() + ".png", event.getChannel());
            image.deleteOnExit();
        }
    }

    private static XYChart getAccuracyChart(BeatSaviourPlayerScore score, String playerName, String diffName) {
        LinkedMap<String, Double> accuracyGraph = score.getTrackers().getScoreGraphTracker().getGraph();

        double[] accuracyValues = accuracyGraph.values().stream().mapToDouble(Double::doubleValue).toArray();
        double[] songTimeValues = accuracyGraph.keySet().stream().mapToDouble(Double::valueOf).toArray();

        // Create Chart
        XYChart chart = new XYChartBuilder().width(BotConstants.chartWidth).theme(ChartTheme.Matlab).title("Accuracy Plot").xAxisTitle("Time").yAxisTitle("Accuracy").build();

        Font font = new Font("Consolas", Font.BOLD, 20);
        Font titleFont = new Font("Consolas", Font.BOLD, 30);
        Font labelFont = new Font("Consolas", Font.BOLD, 20);
        Font labelTitleFont = new Font("Consolas", Font.BOLD, 22);
        Font legendFont = new Font("Consolas", Font.BOLD, 60);

        // Customize Chart
        XYStyler styler = chart.getStyler();

        styler.setDefaultSeriesRenderStyle(XYSeriesRenderStyle.Line);
        styler.setPlotGridLinesVisible(false);
        styler.setMarkerSize(15);
        styler.setPlotContentSize(.95);

        styler.setBaseFont(font);
        styler.setLegendFont(legendFont);
        styler.setChartTitleFont(titleFont);
        styler.setAxisTickLabelsFont(labelFont);
        styler.setAxisTitleFont(labelTitleFont);

        styler.setLegendPosition(LegendPosition.OutsideS);
        styler.setLegendLayout(LegendLayout.Horizontal);
        styler.setLegendSeriesLineLength(20);
        styler.setLegendBorderColor(Color.DARK_GRAY);
        styler.setLegendBackgroundColor(Color.DARK_GRAY);

        styler.setChartBackgroundColor(Color.DARK_GRAY);
        styler.setChartFontColor(Color.WHITE);

        styler.setAxisTickLabelsColor(Color.WHITE);

        // Series
        XYSeries series = chart.addSeries(playerName + ": " + score.getSongName() + " (" + diffName + ")", songTimeValues, accuracyValues);
        series.setLineWidth(10);
        series.setMarker(SeriesMarkers.NONE);
        series.setLineColor(Color.RED);
        return chart;
    }
}
