package bot.utils;

import java.util.List;
import java.util.Random;

public class RandomUtils {

    private static final Random randomGenerator = new Random();

    public static <T> T getRandomItem(List<T> list) {
        int index = getRandomNum(list.size());
        return list.get(index);
    }

    public static int getRandomNum(int max) {
        return randomGenerator.nextInt(max);
    }
}
