package bot.commands.chart;

import bot.dto.LeaderboardService;
import bot.dto.MessageEventDTO;
import bot.dto.player.DataBasePlayer;
import bot.main.BotConstants;
import bot.utils.ChartUtils;
import bot.utils.Messages;
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
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class PlayerChart {

    private int lineWidthSingle = 10;
    private final int lineWidthMulti = 5;
    private Color lineColor = Color.BLUE;
    private final Color DISCORD_COLOR = new Color(49, 51, 56);

    public void sendChartImage(DataBasePlayer player, Color lineColor, LeaderboardService service, MessageEventDTO event) {
        if (player == null) {
            Messages.sendMessage("Could not find player. Please check if you are registered.", event);
            return;
        }
        if (player.getHistoryValues() == null) {
            Messages.sendMessage("Could not find history values for user. Please update the user with \"ru update\".", event);
            return;
        }
        this.lineColor = lineColor;
        String filepath = BotConstants.RESOURCES_PATH + player.getId();
        storePlayerChartToFile(player, service, filepath);
        File image = new File(filepath + ".png");
        if (image.exists()) {
            Messages.sendImage(image, player.getName() + ".png", event);
            image.delete();
        }
    }

    public void sendChartImage(List<DataBasePlayer> players, MessageEventDTO event, String input) {
        double max = 1, min = 2000;

        if (input != null) {
            String[] values = input.split(" ");
            try {
                max = Double.parseDouble(values[0]);
                min = Double.parseDouble(values[1]);
            } catch (NullPointerException | NumberFormatException e) {
                Messages.sendMessage("Wrong syntax. Check out ru \"help\".", event);
                return;
            }

            if (min < max) {
                Messages.sendMessage("The minimum rank cannot be bigger than the maximum rank.", event);
                return;
            }
        }

        XYChart chart = getPlayerChart(players, max, min, LeaderboardService.SCORESABER);
        String filename = BotConstants.RESOURCES_PATH + "players";

        ChartUtils.saveChart(chart, filename);
        File image = new File(filename + ".png");
        if (image.exists()) {
            Messages.sendImage(image, "players.png", event);
            image.delete();
        }
    }

    public XYChart getPlayerChart(List<DataBasePlayer> players, double max, double min, LeaderboardService service) {
        if (players.size() > 1) {
            players = players.stream()
                    .filter(p -> p.getHistoryValues().stream().anyMatch(v -> v <= min && v >= max))
                    .collect(Collectors.toList());
        }
        // Create Chart
        int highestRank = Collections.min(players.stream()
                .map(p -> Collections.min(p.getHistoryValues()))
                .collect(Collectors.toList()));
        int lowestRank = Collections.max(players.stream()
                .map(p -> Collections.max(p.getHistoryValues()))
                .collect(Collectors.toList()));

        int chartHeight = (int) ((lowestRank - highestRank) * 0.25 + 800);
        if (chartHeight > 1200) {
            chartHeight = 1200;
        }
        XYChart chart = new XYChartBuilder()
                .width(BotConstants.chartWidth)
                .height(chartHeight)
                .theme(ChartTheme.Matlab)
                .title("Rank Change - " + service.name())
                .xAxisTitle("Days")
                .yAxisTitle("Rank")
                .build();

        setXYStyler(chart, players, min, max);
        for (DataBasePlayer player : players) {
            // Series
            List<Integer> history = player.getHistoryValues().stream().map(h -> -h).collect(Collectors.toList());
            List<Integer> time = IntStream.rangeClosed(-history.size() + 1, 0).boxed().collect(Collectors.toList());
            XYSeries series = chart.addSeries(player.getName(), time, history);
            if (players.size() == 1) {
                series.setLineWidth(lineWidthSingle);
                series.setLineColor(lineColor);
            } else {
                series.setLineWidth(lineWidthMulti);
            }
            series.setMarker(SeriesMarkers.NONE);
        }
        return chart;
    }

    private void setXYStyler(XYChart chart, List<DataBasePlayer> players, double min, double max) {
        Font font = new Font("Consolas", Font.BOLD, 20);
        Font titleFont = new Font("Consolas", Font.BOLD, 30);
        Font labelFont = new Font("Consolas", Font.BOLD, 20);
        Font labelTitleFont = new Font("Consolas", Font.BOLD, 22);
        Font legendFont = new Font("Consolas", Font.BOLD, players.size() == 1 ? 60 : 15);

        // Customize Chart
        XYStyler styler = chart.getStyler();

        styler.setYAxisMin(-min);
        styler.setYAxisMax(-max);

        styler.setDefaultSeriesRenderStyle(XYSeriesRenderStyle.Line)
                .setPlotGridLinesVisible(false)
                .setMarkerSize(15)
                .setPlotContentSize(.95)
                .setPlotBorderVisible(false)

                .setBaseFont(font)
                .setLegendFont(legendFont)
                .setChartTitleFont(titleFont);

        styler.setAxisTickLabelsFont(labelFont)
                .setAxisTitleFont(labelTitleFont)
                .setDecimalPattern("######");

        styler.setLegendPosition(LegendPosition.OutsideS)
                .setLegendLayout(LegendLayout.Horizontal);

        styler.setLegendSeriesLineLength(20)
                .setLegendBorderColor(DISCORD_COLOR)
                .setLegendBackgroundColor(DISCORD_COLOR)
                .setChartBackgroundColor(DISCORD_COLOR)
                .setPlotBackgroundColor(DISCORD_COLOR)
                .setChartFontColor(Color.WHITE);

        styler.setAxisTickLabelsColor(Color.WHITE);
    }

    public void storePlayerChartToFile(DataBasePlayer player, LeaderboardService service, String filePath) {
        List<Integer> rankValues = player.getHistoryValues();
        double max = Collections.min(rankValues), min = Collections.max(rankValues);

        XYChart chart = getPlayerChart(Collections.singletonList(player), max, min, service);
        ChartUtils.saveChart(chart, filePath);
    }

    public BufferedImage getPlayerChartImage(DataBasePlayer player) {
        List<Integer> rankValues = player.getHistoryValues();
        double max = Collections.min(rankValues), min = Collections.max(rankValues);

        setLineWidthSingle(30);

        XYChart chart = getPlayerChart(Collections.singletonList(player), max, min, LeaderboardService.SCORESABER);
        chart.getStyler()
                .setMarkerSize(100)
                .setLegendVisible(false)
                .setChartTitleVisible(false)
                .setChartTitleBoxVisible(false)
                .setPlotBorderVisible(false);
        chart.getStyler()
                .setAxisTitlesVisible(false)
                .setToolTipsAlwaysVisible(false)
                .setInfoPanelVisible(false);

        Font labelFont = new Font("Consolas", Font.BOLD, 40);
        Color transparent = new Color(0f, 0f, 0f, .0f);
        chart.getStyler()
                .setAxisTickLabelsFont(labelFont)
                .setLegendBorderColor(transparent)
                .setLegendBackgroundColor(transparent)
                .setChartBackgroundColor(transparent)
                .setChartFontColor(transparent);

        return ChartUtils.toBufferedImage(chart);
    }

    public void setLineWidthSingle(int lineWidthSingle) {
        this.lineWidthSingle = lineWidthSingle;
    }
}