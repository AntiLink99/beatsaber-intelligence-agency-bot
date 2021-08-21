package bot.utils;

import org.knowm.xchart.BitmapEncoder;
import org.knowm.xchart.BitmapEncoder.BitmapFormat;
import org.knowm.xchart.internal.chartpart.Chart;

import java.io.IOException;

public class ChartUtils {
    public static void saveChart(Chart<?, ?> chart, String name) {
        try {
            BitmapEncoder.saveBitmap(chart, name, BitmapFormat.PNG);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
