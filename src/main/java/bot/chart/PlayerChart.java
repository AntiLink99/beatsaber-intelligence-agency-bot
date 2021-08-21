package bot.chart;

import bot.dto.MessageEventDTO;
import bot.dto.player.Player;
import bot.main.BotConstants;
import bot.utils.ChartUtils;
import bot.utils.ListValueUtils;
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
import java.io.File;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class PlayerChart {

    public void sendChartImage(Player player, MessageEventDTO event) {
        if (player.getHistoryValues() == null) {
            Messages.sendMessage("Could not find history values for user. Please update the user with \"ru updated <ScoreSaber URL>\".", event.getChannel());
            return;
        }
        List<Integer> rankValues = ListValueUtils.addElementReturnList(player.getHistoryValues(), player.getRank());
        double max = Collections.min(rankValues), min = Collections.max(rankValues);

        XYChart chart = getPlayerChart(Collections.singletonList(player), max, min);
        String filename = "src/main/resources/" + player.getPlayerId();
        ChartUtils.saveChart(chart, filename);
        File image = new File(filename + ".png");
        if (image.exists()) {
            Messages.sendImage(image, player.getPlayerName() + ".png", event.getChannel());
            image.delete();
        }
    }

    public void sendChartImage(List<Player> players, MessageEventDTO event, String input) {
        double max = 1, min = 2000;

        if (input != null) {
            String[] values = input.split(" ");
            try {
                max = Double.parseDouble(values[0]);
                min = Double.parseDouble(values[1]);
            } catch (NullPointerException | NumberFormatException e) {
                Messages.sendMessage("Wrong syntax. Check out ru \"help\".", event.getChannel());
                return;
            }

            if (min < max) {
                Messages.sendMessage("The minimum rank cannot be bigger than the maximum rank.", event.getChannel());
                return;
            }
        }

        XYChart chart = getPlayerChart(players, max, min);
        String filename = "src/main/resources/players";

        ChartUtils.saveChart(chart, filename);
        File image = new File(filename + ".png");
        if (image.exists()) {
            Messages.sendImage(image, "players.png", event.getChannel());
            image.delete();
        }
    }

    private XYChart getPlayerChart(List<Player> players, double max, double min) {
        if (players.size() > 1) {
            players = players.stream().filter(p -> p.getHistoryValues().stream().anyMatch(v -> v <= min && v >= max)).collect(Collectors.toList());
        }
        // Create Chart
        int highestRank = Collections.min(players.stream().map(p -> Collections.min(ListValueUtils.addElementReturnList(p.getHistoryValues(), p.getRank()))).collect(Collectors.toList()));
        int lowestRank = Collections.max(players.stream().map(p -> Collections.max(ListValueUtils.addElementReturnList(p.getHistoryValues(), p.getRank()))).collect(Collectors.toList()));

        int chartHeight = (int) ((lowestRank - highestRank) * 0.25 + 800);
        if (chartHeight > 1200) {
            chartHeight = 1200;
        }
        XYChart chart = new XYChartBuilder().width(BotConstants.chartWidth).height(chartHeight).theme(ChartTheme.Matlab).title("Rank change").xAxisTitle("Days").yAxisTitle("Rank").build();

        XYStyler styler = getXYStyler(chart, players, min, max);
        for (Player player : players) {
            // Series
            List<Integer> history = ListValueUtils.addElementReturnList(player.getHistoryValues(), player.getRank()).stream().map(h -> -h).collect(Collectors.toList());
            List<Integer> time = IntStream.rangeClosed(-history.size() + 1, 0).boxed().collect(Collectors.toList());
            XYSeries series = chart.addSeries(player.getPlayerName() + (players.indexOf(player) % 5 == 0 ? "\n" : ""), time, history);
            series.setLineWidth(players.size() == 1 ? 10 : 5);
            series.setMarker(SeriesMarkers.NONE);
        }
        return chart;
    }

    private XYStyler getXYStyler(XYChart chart, List<Player> players, double min, double max) {
        Font font = new Font("Consolas", Font.BOLD, 20);
        Font titleFont = new Font("Consolas", Font.BOLD, 30);
        Font labelFont = new Font("Consolas", Font.BOLD, 20);
        Font labelTitleFont = new Font("Consolas", Font.BOLD, 22);
        Font legendFont = new Font("Consolas", Font.BOLD, players.size() == 1 ? 60 : 15);

        // Customize Chart
        XYStyler styler = chart.getStyler();

        styler.setYAxisMin(-min);
        styler.setYAxisMax(-max);

        styler.setDefaultSeriesRenderStyle(XYSeriesRenderStyle.Line);
        styler.setPlotGridLinesVisible(false);
        styler.setMarkerSize(15);
        styler.setPlotContentSize(.95);

        styler.setBaseFont(font);
        styler.setLegendFont(legendFont);
        styler.setChartTitleFont(titleFont);
        styler.setAxisTickLabelsFont(labelFont);
        styler.setAxisTitleFont(labelTitleFont);
        styler.setDecimalPattern("######");

        styler.setLegendPosition(LegendPosition.OutsideS);
        styler.setLegendLayout(LegendLayout.Horizontal);
        styler.setLegendSeriesLineLength(20);
        styler.setLegendBorderColor(Color.DARK_GRAY);
        styler.setLegendBackgroundColor(Color.DARK_GRAY);

        styler.setChartBackgroundColor(Color.DARK_GRAY);
        styler.setChartFontColor(Color.WHITE);

        styler.setAxisTickLabelsColor(Color.WHITE);
        return styler;
    }
}